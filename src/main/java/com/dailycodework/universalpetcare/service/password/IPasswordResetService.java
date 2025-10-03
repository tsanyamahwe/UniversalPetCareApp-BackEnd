package com.dailycodework.universalpetcare.service.password;

import com.dailycodework.universalpetcare.model.PasswordReset;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.request.ChangePasswordRequest;

import java.util.Optional;

public interface IPasswordResetService {
    Optional<User> findUserByPasswordResetToken(String token);
    void passwordResetRequest(String email);
    String resetPassword(String password, User user);
    String resetPassword(String token, String newPassword);
    String resetPassword(String token, ChangePasswordRequest changePasswordRequest);
    PasswordReset validatePasswordResetToken(String token);
}
