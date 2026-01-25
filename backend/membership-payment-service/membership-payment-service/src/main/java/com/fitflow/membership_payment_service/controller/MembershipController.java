package com.fitflow.membership_payment_service.controller;
import com.fitflow.membership_payment_service.dto.MembershipPurchaseRequest;
import com.fitflow.membership_payment_service.dto.MembershipResponse;
import com.fitflow.membership_payment_service.entity.MembershipPlan;
import com.fitflow.membership_payment_service.service.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
@Tag(name = "Memberships", description = "Membership management endpoints")
public class MembershipController {
    private final MembershipService membershipService;
    @GetMapping("/plans")
    @Operation(summary = "Get available membership plans")
    public ResponseEntity<List<MembershipPlan>> getAvailablePlans() {
        return ResponseEntity.ok(membershipService.getAvailablePlans());
    }
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user memberships")
    public ResponseEntity<List<MembershipResponse>> getUserMemberships(@PathVariable Long userId) {
        return ResponseEntity.ok(membershipService.getUserMemberships(userId));
    }
    @PostMapping("/purchase")
    @Operation(summary = "Purchase a new membership")
    public ResponseEntity<MembershipResponse> purchaseMembership(@Valid @RequestBody MembershipPurchaseRequest request) {
        MembershipResponse response = membershipService.purchaseMembership(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PutMapping("/{membershipId}/suspend")
    @Operation(summary = "Suspend a membership")
    public ResponseEntity<MembershipResponse> suspendMembership(@PathVariable Long membershipId) {
        MembershipResponse response = membershipService.suspendMembership(membershipId);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{membershipId}/resume")
    @Operation(summary = "Resume a suspended membership")
    public ResponseEntity<MembershipResponse> resumeMembership(@PathVariable Long membershipId) {
        MembershipResponse response = membershipService.resumeMembership(membershipId);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{membershipId}/cancel")
    @Operation(summary = "Cancel a membership")
    public ResponseEntity<MembershipResponse> cancelMembership(@PathVariable Long membershipId) {
        MembershipResponse response = membershipService.cancelMembership(membershipId);
        return ResponseEntity.ok(response);
    }
}
