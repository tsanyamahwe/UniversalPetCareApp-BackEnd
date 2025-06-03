package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.dto.EntityConverter;
import com.dailycodework.universalpetcare.dto.UserDTO;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.exception.UserAlreadyExistException;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.request.UserUpdateRequest;
import com.dailycodework.universalpetcare.response.APIResponse;
import com.dailycodework.universalpetcare.service.user.UserService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import com.dailycodework.universalpetcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RequestMapping(UrlMapping.USERS)
@RestController
public class UserController {
    private final UserService userService;
    private final EntityConverter<User, UserDTO> entityConverter;

    @PostMapping(UrlMapping.REGISTER_USER)
    public ResponseEntity<APIResponse> register(@RequestBody RegistrationRequest registrationRequest){
        try{
            User theUser = userService.register(registrationRequest);
            UserDTO registeredUser = entityConverter.mapEntityToDTO(theUser, UserDTO.class);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.SUCCESS, registeredUser));
        }catch (UserAlreadyExistException e){
            return ResponseEntity.status(CONFLICT).body(new APIResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.UPDATE_USER)
    public ResponseEntity<APIResponse> update(@PathVariable Long userId, @RequestBody UserUpdateRequest userUpdateRequest){
        try{
            User theUser = userService.update(userId, userUpdateRequest);
            UserDTO updatedUser = entityConverter.mapEntityToDTO(theUser, UserDTO.class);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.UPDATE_SUCCESS, updatedUser));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_USER_BY_ID)
    public ResponseEntity<APIResponse> findById(@PathVariable Long userId){
        try{
            User theUser = userService.findById(userId);
            UserDTO deletedUser = entityConverter.mapEntityToDTO(theUser, UserDTO.class);
            return ResponseEntity.status(FOUND).body(new APIResponse(FeedBackMessage.FOUND, deletedUser));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping(UrlMapping.DELETE_USER_BY_ID)
    public ResponseEntity<APIResponse> deleteById(@PathVariable Long userId){
        try{
            userService.delete(userId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.DELETE_SUCCESS, null));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_ALL_USERS)
    public ResponseEntity<APIResponse> getAllUsers(){
        List<UserDTO> theUsers = userService.getAllUsers();
        return ResponseEntity.status(FOUND).body(new APIResponse(FeedBackMessage.FOUND, theUsers));
    }
}
