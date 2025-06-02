package com.dailycodework.universalpetcare.service.user;

import com.dailycodework.universalpetcare.factory.UserFactory;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserFactory userFactory;

    public User add(RegistrationRequest registrationRequest){
        return userFactory.createUser(registrationRequest);
    }
}
