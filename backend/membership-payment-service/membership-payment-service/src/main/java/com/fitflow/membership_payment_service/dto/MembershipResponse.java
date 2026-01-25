package com.fitflow.membership_payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipResponse {
    private Long id;
    private Long userId;
    private String type;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
