package com.dailycodework.universalpetcare.service.user;

import com.dailycodework.universalpetcare.dto.EntityConverter;
import com.dailycodework.universalpetcare.dto.UserDTO;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.factory.UserFactory;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.repository.VeterinarianRepository;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final VeterinarianRepository veterinarianRepository;
    private final EntityConverter<User, UserDTO> entityConverter;

    @Override
    public User register(RegistrationRequest registrationRequest){
        return userFactory.createUser(registrationRequest);
    }

    @Override
    public User update(Long userId, UserUpdateRequest userUpdateRequest){
        User user = findById(userId);
        user.setFirstName(userUpdateRequest.getFirstName());
        user.setLastName(userUpdateRequest.getLastName());
        user.setGender(userUpdateRequest.getGender());
        user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        user.setSpecialization(userUpdateRequest.getSpecialization());
        return userRepository.save(user);
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found!"));
    }

    @Override
    public void delete(Long userId){
        userRepository.findById(userId).ifPresentOrElse(userRepository::delete, ()->{throw new ResourceNotFoundException("User not found");});
    }

    @Override
    public List<UserDTO> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> entityConverter.mapEntityToDTO(user,UserDTO.class)).collect(Collectors.toList());
    }
}
