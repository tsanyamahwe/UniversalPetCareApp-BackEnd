package com.dailycodework.universalpetcare.service.token;

import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.model.VerificationToken;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.repository.VerificationTokenRepository;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import com.dailycodework.universalpetcare.utils.SystemUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService implements IVerificationTokenService{
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public String validateToken(String token) {
        Optional<VerificationToken> verificationToken = findByToken(token);
        if(verificationToken.isEmpty()){
            return FeedBackMessage.VERIFICATION_TOKEN_INVALID;
        }
        User user = verificationToken.get().getUser();
        if(user.isEnabled()){
            return FeedBackMessage.VERIFICATION_TOKEN_VERIFIED;
        }
        if(isTokenExpired(token)){
            return FeedBackMessage.VERIFICATION_TOKEN_EXPIRED;
        }
        user.setEnabled(true);
        userRepository.save(user);
        return FeedBackMessage.VERIFICATION_TOKEN_VALID;
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        var verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        Optional<VerificationToken> token = findByToken(oldToken);
        if (token.isPresent()){
            var verificationToken = token.get();
            verificationToken.setToken(UUID.randomUUID().toString());
            verificationToken.setExpirationDate(SystemUtils.getExpirationTime());
            verificationTokenRepository.save(verificationToken);
        }
        throw new IllegalArgumentException(FeedBackMessage.INVALID_TOKEN + oldToken);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    @Override
    public void deleteVerificationToken(Long tokenId) {
        verificationTokenRepository.deleteById(tokenId);
    }

    @Override
    public boolean isTokenExpired(String token) {
        Optional<VerificationToken> verificationToken = findByToken(token);
        if(verificationToken.isEmpty()){
            return true;
        }
        VerificationToken verifyToken = verificationToken.get();
        return verifyToken.getExpirationDate().getTime() <= Calendar.getInstance().getTime().getTime();
    }
}
