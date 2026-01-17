package com.fitflow.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String bio;
    private String preferredTrainingType;
    private String experienceLevel;
    private String trainingGoal;
    private Integer trainingDaysPerWeek;
    private Boolean notificationsEnabled;
}
