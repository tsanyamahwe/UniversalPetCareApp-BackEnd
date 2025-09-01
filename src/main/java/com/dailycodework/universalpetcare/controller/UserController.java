package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.dto.EntityConverter;
import com.dailycodework.universalpetcare.dto.UserDTO;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.exception.AlreadyExistException;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.request.ChangePasswordRequest;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.request.UserUpdateRequest;
import com.dailycodework.universalpetcare.response.APIResponse;
import com.dailycodework.universalpetcare.service.password.IChangePasswordService;
import com.dailycodework.universalpetcare.service.user.UserService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import com.dailycodework.universalpetcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@CrossOrigin("http://localhost:5173")
@RequiredArgsConstructor
@RequestMapping(UrlMapping.USERS)
@RestController
public class UserController {
    private final UserService userService;
    private final EntityConverter<User, UserDTO> entityConverter;
    private final IChangePasswordService changePasswordService;

    @PostMapping(UrlMapping.REGISTER_USER)
    public ResponseEntity<APIResponse> register(@RequestBody RegistrationRequest registrationRequest){
        try{
            User theUser = userService.register(registrationRequest);
            UserDTO registeredUser = entityConverter.mapEntityToDTO(theUser, UserDTO.class);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.CREATE_SUCCESS, registeredUser));
        }catch (AlreadyExistException e){
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
            UserDTO userDTO = userService.getUserWithDetails(userId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, userDTO));
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
        return ResponseEntity.status(FOUND).body(new APIResponse(FeedBackMessage.RESOURCE_FOUND, theUsers));
    }

    @PutMapping(UrlMapping.CHANGE_PASSWORD)
    public ResponseEntity<APIResponse> changePassword(@PathVariable Long userId, @RequestBody ChangePasswordRequest changePasswordRequest){
        try{
            changePasswordService.changePassword(userId, changePasswordRequest);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.CREATE_SUCCESS, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new APIResponse(e.getMessage(), null));
        }catch(ResourceNotFoundException e){
            return  ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.COUNT_ALL_VETS)
    public long countVeterinarians(){
        return userService.countVeterinarians();
    }

    @GetMapping(UrlMapping.COUNT_ALL_PATIENTS)
    public long countPatients(){
        return userService.countPatients();
    }

    @GetMapping(UrlMapping.COUNT_ALL_USERS)
    public long countUsers(){
        return userService.countAllUsers();
    }

    @GetMapping(UrlMapping.AGGREGATE_USERS)
    public ResponseEntity<APIResponse> aggregateUsersByMonthAndType(){
        try {
            Map<String, Map<String, Long>> aggregatedUsers = userService.aggregateUsersByMonthAndType();
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, aggregatedUsers));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.AGGREGATE_STATUS)
    public ResponseEntity<APIResponse> aggregateUsersByEnabledStatusAndType(){
        try{
            Map<String, Map<String, Long>> aggregatedData = userService.aggregatesUsersByEnabledStatusAndType();
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, aggregatedData));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.LOCK_USER_ACCOUNT)
    public ResponseEntity<APIResponse> lockUserAccount(@PathVariable Long userId){
        try {
            userService.lockUserAccount(userId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.LOCKED, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.UNLOCK_USER_ACCOUNT)
    public ResponseEntity<APIResponse> unLockUserAccount(@PathVariable Long userId){
        try {
            userService.unLockUserAccount(userId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.UNLOCKED, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }
}
