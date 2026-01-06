package com.fitflow.gym_operations.dto;

import lombok.Data;

@Data
public class BookingRequestDTO {
    private Long userId;
    private Long gymClassId;
}