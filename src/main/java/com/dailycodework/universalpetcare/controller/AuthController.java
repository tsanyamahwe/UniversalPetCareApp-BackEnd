package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.event.listener.RegistrationCompleteEvent;
import com.dailycodework.universalpetcare.exception.PasswordChangeNotAllowedException;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.PasswordReset;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.model.VerificationToken;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.ChangePasswordRequest;
import com.dailycodework.universalpetcare.request.LoginRequest;
import com.dailycodework.universalpetcare.request.PasswordResetRequest;
import com.dailycodework.universalpetcare.response.APIResponse;
import com.dailycodework.universalpetcare.response.JwtResponse;
import com.dailycodework.universalpetcare.security.jwt.JwtUtils;
import com.dailycodework.universalpetcare.security.user.UPCUserDetails;
import com.dailycodework.universalpetcare.service.password.ChangePasswordService;
import com.dailycodework.universalpetcare.service.password.IPasswordResetService;
import com.dailycodework.universalpetcare.service.token.VerificationTokenService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import com.dailycodework.universalpetcare.utils.UrlMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@CrossOrigin("http://localhost:5173")
@RequiredArgsConstructor
@RestController
@RequestMapping(UrlMapping.AUTH)
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final VerificationTokenService verificationTokenService;
    private final IPasswordResetService passwordResetService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ChangePasswordService changePasswordService;
    private final UserRepository userRepository;

    @PostMapping(UrlMapping.LOGIN)
    public ResponseEntity<APIResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        try{
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateTokenForUser(authentication);
            UPCUserDetails upcUserDetails = (UPCUserDetails) authentication.getPrincipal();
            JwtResponse jwtResponse = new JwtResponse(upcUserDetails.getId(), jwt);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.SUCCESS_AUTH, jwtResponse));
        } catch (DisabledException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(new APIResponse(FeedBackMessage.AUTH_DISABLED, null));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(new APIResponse(FeedBackMessage.AUTH_FAILED, FeedBackMessage.AUTH_REASON));
        }
    }

    @GetMapping(UrlMapping.VERIFY_EMAIL)
    public ResponseEntity<APIResponse> verifyEmail(@RequestParam("token") String token){
        String result = verificationTokenService.validateToken(token);
        return  switch (result){
            case "VALID" -> ResponseEntity.ok(new APIResponse(FeedBackMessage.VERIFICATION_TOKEN_VALID, null));
            case "VERIFIED" -> ResponseEntity.ok(new APIResponse(FeedBackMessage.VERIFICATION_TOKEN_VERIFIED, null));
            case "EXPIRED" -> ResponseEntity.status(HttpStatus.GONE).body(new APIResponse(FeedBackMessage.VERIFICATION_TOKEN_EXPIRED, null));
            case "INVALID" -> ResponseEntity.status(GONE).body(new APIResponse(FeedBackMessage.VERIFICATION_TOKEN_INVALID, null));
            default -> ResponseEntity.internalServerError().body(new APIResponse(FeedBackMessage.VERIFICATION_VALIDATION_ERROR, null));
        };
    }

    @PutMapping(UrlMapping.RESEND_TOKEN)
    public ResponseEntity<APIResponse> resendVerificationToken(@RequestParam("token") String oldToken){
        try{
            VerificationToken verificationToken = verificationTokenService.generateNewVerificationToken(oldToken);
            User theUser = verificationToken.getUser();
            applicationEventPublisher.publishEvent(new RegistrationCompleteEvent(theUser));
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.NEW_VERIF_TOKEN_SEND, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new APIResponse(e.getMessage(), null));
        }
    }

    @PostMapping(UrlMapping.REQUEST_PASS_RESET)
    public ResponseEntity<APIResponse> requestPasswordReset(@RequestBody Map<String, String> requestBody){
        String email = requestBody.get("email");
        if(email == null || email.trim().isEmpty()){
            return ResponseEntity.badRequest().body(new APIResponse(FeedBackMessage.VERIFICATION_NOTICE, null));
        }
        try {
            //check if user can reset password before sending email
            if(!verificationTokenService.canUserResetPassword(email)){
                long daysRemaining = verificationTokenService.getDaysUntilPasswordResetAllowed(email);
                return ResponseEntity.status(TOO_EARLY).body(new APIResponse(FeedBackMessage.CHANGED_PASSWORD+daysRemaining+FeedBackMessage.MORE_DAYS, null));
            }
            passwordResetService.passwordResetRequest(email);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.VERIFICATION_UPDATE, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new APIResponse(e.getMessage(), null));
        }
    }

    @PostMapping(UrlMapping.RESET_PASSWORD)
    public ResponseEntity<APIResponse> passwordReset(@RequestBody PasswordResetRequest passwordResetRequest){
        try {
            String token = passwordResetRequest.getToken();
            String newPassword = passwordResetRequest.getNewPassword();

            if(token == null || token.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()){
                return ResponseEntity.badRequest().body(new APIResponse(FeedBackMessage.MISSING_AUTH, null));
            }

            String result = passwordResetService.resetPassword(token, newPassword);
            return ResponseEntity.ok(new APIResponse(result, null));
        } catch (PasswordChangeNotAllowedException e) {
            return ResponseEntity.status(TOO_EARLY).body(new APIResponse(e.getMessage(), null));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new APIResponse(e.getMessage(), null));
        }
    }

    @PostMapping(UrlMapping.RESETTING_PASS)
    public ResponseEntity<APIResponse> passwordResetAdvanced(@RequestBody Map<String, Object> requestBody){
        try{
            String token = (String) requestBody.get("token");
            String newPassword = (String) requestBody.get("newPassword");
            String confirmNewPassword = (String) requestBody.get("confirmNewPassword");

            if(token == null || token.trim().isEmpty() ||
                    newPassword == null || newPassword.trim().isEmpty() ||
                            confirmNewPassword == null || confirmNewPassword.trim().isEmpty()){
                return ResponseEntity.badRequest().body(new APIResponse(FeedBackMessage.REQUIRE_ALL_FIELDS, null));
            }

            ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
            changePasswordRequest.setNewPassword(newPassword);
            changePasswordRequest.setConfirmNewPassword(confirmNewPassword);

            String result = passwordResetService.resetPassword(token, changePasswordRequest);
            return ResponseEntity.ok(new APIResponse(result, null));
        }catch (PasswordChangeNotAllowedException e){
            return ResponseEntity.status(TOO_EARLY).body(new APIResponse(e.getMessage(), null));
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.CAN_RESET_PASS)
    public ResponseEntity<Map<String, Object>> canResetPassword(@RequestParam String email){
        try{
            boolean canReset = verificationTokenService.canUserResetPassword(email);
            Map<String, Object> response = new HashMap<>();
            response.put("canReset", canReset);

            if(!canReset){
                Long daysRemaining = verificationTokenService.getDaysUntilPasswordResetAllowed(email);
                response.put("daysRemaining", daysRemaining);
                response.put("message", FeedBackMessage.CHANGED_PASSWORD+daysRemaining+FeedBackMessage.MORE_DAYS);
            }
            return ResponseEntity.ok(response);
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(Map.of("error", FeedBackMessage.USER_NOT_FOUND));
        }
    }

    @GetMapping(UrlMapping.RESET_BY_TOKEN)
    public ResponseEntity<Map<String, Object>> canResetPasswordByToken(@RequestParam String token){
        try{
            PasswordReset passwordReset = passwordResetService.validatePasswordResetToken(token);
            User user = passwordReset.getUser();

            boolean canReset = user.canChangePassword();
            Map<String, Object> response = new HashMap<>();
            response.put("canReset", canReset);

            if(!canReset){
                Long daysRemaining = user.getDaysUntilPasswordChangeAllowed();
                response.put("daysRemaining", daysRemaining);
                response.put("message", FeedBackMessage.CHANGED_PASSWORD+daysRemaining+FeedBackMessage.MORE_DAYS);
            }
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return ResponseEntity.status(BAD_REQUEST).body(Map.of("error", FeedBackMessage.INVALID_TOKEN));
        }
    }

    @PostMapping(UrlMapping.CHANGE_PASSWORD)
    public ResponseEntity<APIResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, Authentication authentication){
        try{
            UPCUserDetails userDetails = (UPCUserDetails)authentication.getPrincipal();
            Long userId = userDetails.getId();

            User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND));
            if(!user.canChangePassword()){
                long daysRemaining = user.getDaysUntilPasswordChangeAllowed();
                return ResponseEntity.status(TOO_EARLY).body(new APIResponse(FeedBackMessage.CHANGED_PASSWORD+daysRemaining+FeedBackMessage.MORE_DAYS, null));
            }
            changePasswordService.changePassword(userId, changePasswordRequest);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.PASSWORD_RESET, null));
        }catch (IllegalStateException e){
            return ResponseEntity.badRequest().body(new APIResponse(e.getMessage(), null));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(FeedBackMessage.USER_NOT_FOUND, null));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(new APIResponse(FeedBackMessage.PASS_RESET_FAILED, null));
        }
    }
}
