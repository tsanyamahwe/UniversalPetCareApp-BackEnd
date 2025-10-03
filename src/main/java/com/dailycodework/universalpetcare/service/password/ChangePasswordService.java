package com.dailycodework.universalpetcare.service.password;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.ChangePasswordRequest;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChangePasswordService implements IChangePasswordService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void changePassword(Long userId, ChangePasswordRequest changePasswordRequest){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.NOT_FOUND));

        if(!user.canChangePassword()){
            long daysRemaining = user.getDaysUntilPasswordChangeAllowed();
            throw new IllegalArgumentException("Password change is restricted. You must wait "+daysRemaining+" more days.");
        }
        // Better validation for empty/null fields
        if (!StringUtils.hasText(changePasswordRequest.getCurrentPassword()) ||
                !StringUtils.hasText(changePasswordRequest.getNewPassword()) ||
                !StringUtils.hasText(changePasswordRequest.getConfirmNewPassword())) {
            throw new IllegalArgumentException("All fields are required");
        }
        // Use passwordEncoder to check if current password matches
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password does not match (it is not correct)");
        }
        // Check if new password is different from current password
        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }
        // Check if new passwords match
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Password confirmation mis-match");
        }
        if(!isValidPassword(changePasswordRequest.getNewPassword())){
            throw new IllegalArgumentException(FeedBackMessage.INVALID_PASSWORD_FORMAT);
        }
        // Encode the new password before saving
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        user.updatePasswordChangeInfo();
        userRepository.save(user);
    }

    private boolean isValidPassword(String password) {
        // Regex for at least 8 characters, 1 capital letter, 1 number, and 1 special character
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";
        return password.matches(passwordRegex);
    }
}
