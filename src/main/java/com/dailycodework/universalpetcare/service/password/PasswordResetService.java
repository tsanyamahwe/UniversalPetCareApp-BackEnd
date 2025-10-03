package com.dailycodework.universalpetcare.service.password;

import com.dailycodework.universalpetcare.event.listener.PasswordResetEvent;
import com.dailycodework.universalpetcare.exception.PasswordChangeNotAllowedException;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.PasswordReset;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.model.VerificationToken;
import com.dailycodework.universalpetcare.repository.PasswordResetRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.repository.VerificationTokenRepository;
import com.dailycodework.universalpetcare.request.ChangePasswordRequest;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import com.dailycodework.universalpetcare.utils.SystemUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService implements IPasswordResetService{
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PasswordResetRepository passwordResetRepository;

    @Override
    public Optional<User> findUserByPasswordResetToken(String token) {
        return verificationTokenRepository.findByToken(token).map(VerificationToken::getUser);
    }

    @Override
    @Transactional
    public void passwordResetRequest(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND));
        passwordResetRepository.deleteByUser(user);
        passwordResetRepository.flush();

        String token = UUID.randomUUID().toString();
        PasswordReset passwordReset = new PasswordReset();
        passwordReset.setToken(token);
        passwordReset.setUser(user);
        passwordReset.setExpirationTime(SystemUtils.getExpirationTime());
        passwordReset.setCreatedAt(new Date());

        passwordResetRepository.save(passwordReset);
        applicationEventPublisher.publishEvent(new PasswordResetEvent(this, user, token));
    }

    @Override
    public String resetPassword(String password, User user) {
        if(passwordEncoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException(FeedBackMessage.PASSWORD_REUSE_FORBIDDEN);
        }
        if(isValidPassword(password)){
            throw new IllegalArgumentException(FeedBackMessage.INVALID_PASSWORD_FORMAT);
        }
        try{
            user.setPassword(passwordEncoder.encode(password));
            user.updatePasswordChangeInfo();
            userRepository.save(user);
            invalidatePasswordResetToken(user);
            return FeedBackMessage.PASSWORD_RESET;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public String resetPassword(String token, String newPassword){
        try{
            PasswordReset passwordReset = validatePasswordResetToken(token);
            User user = passwordReset.getUser();
            if(!user.canChangePassword()){
                long daysRemaining = user.getDaysUntilPasswordChangeAllowed();
                throw new PasswordChangeNotAllowedException("Password was recently changed. Please wait "+daysRemaining+" more days before resetting again");
            }
            String result = this.resetPassword(newPassword, user);
            passwordResetRepository.delete(passwordReset);
            return result;
        }catch (PasswordChangeNotAllowedException e) {
            throw new PasswordChangeNotAllowedException(e.getMessage());
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("Password reset failed: "+ e.getMessage());
        }
    }

    @Override
    @Transactional
    public String resetPassword(String token, ChangePasswordRequest changePasswordRequest){
        PasswordReset passwordReset = validatePasswordResetToken(token);
        User user = passwordReset.getUser();
        try{
            if(!user.canChangePassword()){
                long daysRemaining = user.getDaysUntilPasswordChangeAllowed();
                throw new PasswordChangeNotAllowedException("Password was recently changed. Please wait "+daysRemaining+" more days before resetting again");
            }
            if(!StringUtils.hasText(changePasswordRequest.getNewPassword()) || !StringUtils.hasText(changePasswordRequest.getConfirmNewPassword())){
                throw new IllegalArgumentException("New Password and confirmation are required");
            }
            if(!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())){
                throw new IllegalArgumentException("Password confirmation does not match");
            }
            if(passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword())){
                throw  new IllegalArgumentException("New password must be different from current password");
            }
            if(isValidPassword(changePasswordRequest.getNewPassword())){
                throw new IllegalArgumentException(FeedBackMessage.INVALID_PASSWORD_FORMAT);
            }
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            user.updatePasswordChangeInfo();
            userRepository.save(user);
            passwordResetRepository.delete(passwordReset);
            invalidatePasswordResetToken(user);
            return "Password reset successful";
        }catch (PasswordChangeNotAllowedException e){
            throw new PasswordChangeNotAllowedException(e.getMessage());
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("Password reset failed: " +e.getMessage());
        }
    }

    private boolean isValidPassword(String password) {
        // Regex for at least 8 characters, 1 capital letter, 1 number, and 1 special character
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$";
        return !password.matches(passwordRegex);
    }

    private void invalidatePasswordResetToken(User user) {
        try {
            Optional<VerificationToken> tokenOpt = verificationTokenRepository.findByUser(user);
            tokenOpt.ifPresent(verificationTokenRepository::delete);
        } catch (Exception e) {
            System.err.println("Warning: Could not invalidate reset token: " + e.getMessage());
        }
    }

    @Override
    public PasswordReset validatePasswordResetToken(String token){
        PasswordReset passwordReset = passwordResetRepository.findByToken(token);
        if(passwordReset == null){
            throw new IllegalArgumentException("Invalid token");
        }
        if(passwordReset.getExpirationTime().before(new Date())){
            throw new IllegalArgumentException("Token has expired");
        }
        return passwordReset;
    }
}
