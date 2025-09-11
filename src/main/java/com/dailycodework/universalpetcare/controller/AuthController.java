package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.request.LoginRequest;
import com.dailycodework.universalpetcare.response.APIResponse;
import com.dailycodework.universalpetcare.response.JwtResponse;
import com.dailycodework.universalpetcare.security.jwt.JwtUtils;
import com.dailycodework.universalpetcare.security.user.UPCUserDetails;
import com.dailycodework.universalpetcare.service.token.VerificationTokenService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import com.dailycodework.universalpetcare.utils.UrlMapping;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.GONE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@CrossOrigin("http://localhost:5173")
@RequiredArgsConstructor
@RestController
@RequestMapping(UrlMapping.AUTH)
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final VerificationTokenService verificationTokenService;

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
}
