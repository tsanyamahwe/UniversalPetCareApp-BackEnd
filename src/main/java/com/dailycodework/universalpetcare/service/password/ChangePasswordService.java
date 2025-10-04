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
            throw new IllegalArgumentException(FeedBackMessage.CHANGED_PASSWORD+daysRemaining+FeedBackMessage.MORE_DAYS);
        }
        // Better validation for empty/null fields
        if (!StringUtils.hasText(changePasswordRequest.getCurrentPassword()) ||
                !StringUtils.hasText(changePasswordRequest.getNewPassword()) ||
                !StringUtils.hasText(changePasswordRequest.getConfirmNewPassword())) {
            throw new IllegalArgumentException(FeedBackMessage.REQUIRE_ALL_FIELDS);
        }
        // Use passwordEncoder to check if current password matches
        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException(FeedBackMessage.CURRENT_PASS_WRONG);
        }
        // Check if new password is different from current password
        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException(FeedBackMessage.NEW_PASS_DIFFERS);
        }
        // Check if new passwords match
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
            throw new IllegalArgumentException(FeedBackMessage.CONF_PASS_NO_MATCH);
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
