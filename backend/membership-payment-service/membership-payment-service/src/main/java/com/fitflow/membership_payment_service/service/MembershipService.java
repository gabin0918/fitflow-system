package com.fitflow.membership_payment_service.service;

import com.fitflow.membership_payment_service.dto.MembershipPurchaseRequest;
import com.fitflow.membership_payment_service.dto.MembershipResponse;
import com.fitflow.membership_payment_service.entity.Membership;
import com.fitflow.membership_payment_service.entity.MembershipPlan;
import com.fitflow.membership_payment_service.entity.Payment;
import com.fitflow.membership_payment_service.repository.MembershipPlanRepository;
import com.fitflow.membership_payment_service.repository.MembershipRepository;
import com.fitflow.membership_payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final PaymentRepository paymentRepository;

    // Pobierz wszystkie aktywne plany członkostwa
    public List<MembershipPlan> getAvailablePlans() {
        return membershipPlanRepository.findByIsActive(true);
    }

    // Pobierz członkostwa użytkownika
    public List<MembershipResponse> getUserMemberships(Long userId) {
        return membershipRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Kup nowe członkostwo
    @Transactional
    public MembershipResponse purchaseMembership(MembershipPurchaseRequest request) {
        // Znajdź plan
        MembershipPlan plan = membershipPlanRepository.findById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        // Sprawdź czy użytkownik nie ma już aktywnego członkostwa
        membershipRepository.findByUserIdAndStatus(request.getUserId(), "ACTIVE")
                .ifPresent(m -> {
                    throw new RuntimeException("User already has an active membership");
                });

        // Utwórz nowe członkostwo
        Membership membership = new Membership();
        membership.setUserId(request.getUserId());
        membership.setType(plan.getName());
        membership.setStatus("ACTIVE");
        membership.setPrice(plan.getPrice());
        membership.setStartDate(LocalDateTime.now());
        membership.setEndDate(LocalDateTime.now().plusDays(plan.getDurationDays()));

        membership = membershipRepository.save(membership);

        // Utwórz płatność
        Payment payment = new Payment();
        payment.setMembershipId(membership.getId());
        payment.setUserId(request.getUserId());
        payment.setAmount(plan.getPrice());
        payment.setStatus("COMPLETED");
        payment.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "CREDIT_CARD");
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setPaymentDate(LocalDateTime.now());

        paymentRepository.save(payment);

        return convertToResponse(membership);
    }

    // Zawieś członkostwo
    @Transactional
    public MembershipResponse suspendMembership(Long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new RuntimeException("Membership not found"));

        if (!"ACTIVE".equals(membership.getStatus())) {
            throw new RuntimeException("Only active memberships can be suspended");
        }

        membership.setStatus("SUSPENDED");
        membership = membershipRepository.save(membership);

        return convertToResponse(membership);
    }

    // Wznów członkostwo
    @Transactional
    public MembershipResponse resumeMembership(Long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new RuntimeException("Membership not found"));

        if (!"SUSPENDED".equals(membership.getStatus())) {
            throw new RuntimeException("Only suspended memberships can be resumed");
        }

        // Sprawdź czy nie wygasło
        if (membership.getEndDate() != null && membership.getEndDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Membership has expired");
        }

        membership.setStatus("ACTIVE");
        membership = membershipRepository.save(membership);

        return convertToResponse(membership);
    }

    // Anuluj członkostwo
    @Transactional
    public MembershipResponse cancelMembership(Long membershipId) {
        Membership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new RuntimeException("Membership not found"));

        if ("CANCELLED".equals(membership.getStatus())) {
            throw new RuntimeException("Membership is already cancelled");
        }

        membership.setStatus("CANCELLED");
        membership = membershipRepository.save(membership);

        return convertToResponse(membership);
    }

    // Konwersja do DTO
    private MembershipResponse convertToResponse(Membership membership) {
        MembershipResponse response = new MembershipResponse();
        response.setId(membership.getId());
        response.setUserId(membership.getUserId());
        response.setType(membership.getType());
        response.setStatus(membership.getStatus());
        response.setStartDate(membership.getStartDate());
        response.setEndDate(membership.getEndDate());
        response.setPrice(membership.getPrice());
        response.setCreatedAt(membership.getCreatedAt());
        response.setUpdatedAt(membership.getUpdatedAt());
        return response;
    }
}
