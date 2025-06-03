package com.dailycodework.universalpetcare.service.user;

import com.dailycodework.universalpetcare.dto.UserDTO;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.request.UserUpdateRequest;

import java.util.List;

public interface IUserService {
    User register(RegistrationRequest registrationRequest);

    User update(Long userId, UserUpdateRequest userUpdateRequest);

    User findById(Long userId);

    void delete(Long userId);

    List<UserDTO> getAllUsers();
}
