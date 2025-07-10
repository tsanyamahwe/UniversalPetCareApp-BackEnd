package com.dailycodework.universalpetcare.service.password;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.ChangePasswordRequest;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChangePasswordService implements IChangePasswordService{
    private final UserRepository userRepository;

    @Override
    public void changePassword(Long userId, ChangePasswordRequest changePasswordRequest){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.NOT_FOUND));
        if(Objects.equals(changePasswordRequest.getCurrentPassword(), "") || Objects.equals(changePasswordRequest.getNewPassword(), "")){
            throw new IllegalArgumentException("All fields are required");
        }
        if(!Objects.equals(changePasswordRequest.getCurrentPassword(), user.getPassword())){
            throw new IllegalArgumentException("Current Password does not match");
        }
        if(!Objects.equals(changePasswordRequest.getNewPassword(), changePasswordRequest.getConfirmNewPassword())){
            throw new IllegalArgumentException("Password confirmation mis-match");
        }
        user.setPassword(changePasswordRequest.getNewPassword());
        userRepository.save(user);
    }
}
