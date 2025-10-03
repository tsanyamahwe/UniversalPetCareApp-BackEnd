package com.dailycodework.universalpetcare.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "New password is required")
    private String currentPassword;
    @NotBlank(message = "New password is required")
    private String newPassword;
    @NotBlank(message = "New password is required")
    private String confirmNewPassword;
}
