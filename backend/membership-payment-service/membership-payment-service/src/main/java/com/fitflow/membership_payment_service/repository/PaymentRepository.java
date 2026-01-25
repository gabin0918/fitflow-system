package com.fitflow.membership_payment_service.repository;

import com.fitflow.membership_payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUserId(Long userId);

    List<Payment> findByMembershipId(Long membershipId);

    List<Payment> findByStatus(String status);
}
