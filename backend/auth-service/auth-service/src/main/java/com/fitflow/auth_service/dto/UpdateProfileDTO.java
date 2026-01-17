package com.fitflow.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileDTO {
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
