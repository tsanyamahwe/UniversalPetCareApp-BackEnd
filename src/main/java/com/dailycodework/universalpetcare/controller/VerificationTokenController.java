package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.model.VerificationToken;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.VerificationTokenRequest;
import com.dailycodework.universalpetcare.response.APIResponse;
import com.dailycodework.universalpetcare.service.token.IVerificationTokenService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import com.dailycodework.universalpetcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping(UrlMapping.TOKEN_VERIFICATION)
public class VerificationTokenController {
    private final IVerificationTokenService verificationTokenService;
    private final UserRepository userRepository;

    @GetMapping(UrlMapping.VALIDATE_TOKEN)
    public ResponseEntity<APIResponse> validateToken(String token){
        String result = verificationTokenService.validateToken(token);
        APIResponse response = switch (result){
            case "INVALID" -> new APIResponse(FeedBackMessage.VERIFICATION_TOKEN_INVALID, null);
            case "VERIFIED" -> new APIResponse(FeedBackMessage.VERIFICATION_TOKEN_VERIFIED, null);
            case "EXPIRED" -> new APIResponse(FeedBackMessage.VERIFICATION_TOKEN_EXPIRED, null);
            case "VALID" -> new APIResponse(FeedBackMessage.VERIFICATION_TOKEN_VALID, null);
            default -> new APIResponse(FeedBackMessage.VERIFICATION_VALIDATION_ERROR, null);
        };
        return ResponseEntity.ok(response);
    }

    @GetMapping(UrlMapping.CHECK_TOKEN_EXPIRATION)
    public ResponseEntity<APIResponse> checkTokenExpiration(String token){
        boolean isExpired = verificationTokenService.isTokenExpired(token);
        APIResponse response;
        if(isExpired){
            response = new APIResponse(FeedBackMessage.VERIFICATION_TOKEN_EXPIRED, null);
        }else{
            response = new APIResponse(FeedBackMessage.VERIFICATION_TOKEN_VALID, null);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(UrlMapping.SAVE_TOKEN)
    public ResponseEntity<APIResponse> saveVerificationTokenForUsers(@RequestBody VerificationTokenRequest verificationTokenRequest){
        User user = userRepository.findById(verificationTokenRequest.getUser().getId()).orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.TOKEN_NOT_FOUND));
        verificationTokenService.saveVerificationTokenForUser(verificationTokenRequest.getToken(), user);
        return ResponseEntity.ok(new APIResponse(FeedBackMessage.TOKEN_SAVED_SUCCESS, null));
    }

    @PutMapping(UrlMapping.GENERATE_NEW_TOKEN)
    public ResponseEntity<APIResponse> generateNewVerificationToken(@RequestParam String oldToken){
        VerificationToken newToken = verificationTokenService.generateNewVerificationToken(oldToken);
        return ResponseEntity.ok(new APIResponse(FeedBackMessage.NEW_TOKEN, newToken));
    }

    @DeleteMapping(UrlMapping.DELETE_TOKEN)
    public ResponseEntity<APIResponse> deleteUserToken(@RequestParam Long userId){
        verificationTokenService.deleteVerificationToken(userId);
        return ResponseEntity.ok(new APIResponse(FeedBackMessage.TOKEN_DELETED, null));
    }
}
