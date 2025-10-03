package com.dailycodework.universalpetcare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeInfoDTO {
    private Boolean canChangePassword;
    private Long daysUntilAllowed;
    private LocalDateTime lastPasswordChange;
    private Integer passwordChangeCount;
}
