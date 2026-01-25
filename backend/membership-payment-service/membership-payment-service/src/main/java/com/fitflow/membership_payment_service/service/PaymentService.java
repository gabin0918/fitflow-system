package com.fitflow.membership_payment_service.service;

import com.fitflow.membership_payment_service.dto.PaymentResponse;
import com.fitflow.membership_payment_service.entity.Payment;
import com.fitflow.membership_payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // Pobierz płatności użytkownika
    public List<PaymentResponse> getUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Pobierz płatności dla członkostwa
    public List<PaymentResponse> getMembershipPayments(Long membershipId) {
        return paymentRepository.findByMembershipId(membershipId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Pobierz szczegóły płatności
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return convertToResponse(payment);
    }

    // Konwersja do DTO
    private PaymentResponse convertToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setMembershipId(payment.getMembershipId());
        response.setUserId(payment.getUserId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setTransactionId(payment.getTransactionId());
        response.setPaymentDate(payment.getPaymentDate());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }
}
