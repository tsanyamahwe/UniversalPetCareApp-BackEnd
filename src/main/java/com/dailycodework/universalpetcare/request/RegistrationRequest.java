package com.dailycodework.universalpetcare.request;

import com.dailycodework.universalpetcare.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrationRequest {
    private Long id;
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "Gender is required")
    private String gender;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;
    @NotBlank(message = "Password is required")
    @ValidPassword
    private String password;
    @NotBlank(message = "User type is required")
    private String userType;
    private boolean isEnabled;
    private String specialization;
}
