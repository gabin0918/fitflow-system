package com.fitflow.membership_payment_service.controller;
import com.fitflow.membership_payment_service.dto.PaymentResponse;
import com.fitflow.membership_payment_service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management endpoints")
public class PaymentController {
    private final PaymentService paymentService;
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user payments")
    public ResponseEntity<List<PaymentResponse>> getUserPayments(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getUserPayments(userId));
    }
    @GetMapping("/membership/{membershipId}")
    @Operation(summary = "Get membership payments")
    public ResponseEntity<List<PaymentResponse>> getMembershipPayments(@PathVariable Long membershipId) {
        return ResponseEntity.ok(paymentService.getMembershipPayments(membershipId));
    }
    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment details")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }
}
