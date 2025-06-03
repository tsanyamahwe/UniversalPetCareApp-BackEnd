package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.dto.EntityConverter;
import com.dailycodework.universalpetcare.dto.UserDTO;
import com.dailycodework.universalpetcare.exception.UserAlreadyExistException;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.response.APIResponse;
import com.dailycodework.universalpetcare.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;
    private final EntityConverter<User, UserDTO> entityConverter;

    @PostMapping
    public ResponseEntity<APIResponse> addUser(@RequestBody RegistrationRequest registrationRequest){
        try{
            User theUser = userService.add(registrationRequest);
            UserDTO registeredUser = entityConverter.mapEntityToDTO(theUser, UserDTO.class);
            return ResponseEntity.ok(new APIResponse("User registered successfully", registeredUser));
        }catch (UserAlreadyExistException e){
            return ResponseEntity.ok(new APIResponse(e.getMessage(), null));
        }
    }
}
