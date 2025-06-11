package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Photo;
import com.dailycodework.universalpetcare.response.APIResponse;
import com.dailycodework.universalpetcare.service.photo.IPhotoService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import com.dailycodework.universalpetcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping(UrlMapping.PHOTOS)
@RequiredArgsConstructor
public class PhotoController {
    private final IPhotoService photoService;

    @PostMapping(UrlMapping.UPLOAD_PHOTO)
    public ResponseEntity<APIResponse> uploadPhoto(@RequestParam("file")MultipartFile file, @RequestParam("userId") Long userId) throws SQLException, IOException {
        try{
            Photo thePhoto = photoService.savePhoto(file, userId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.CREATE_SUCCESS, null));
        }catch (IOException | SQLException e){
           return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(FeedBackMessage.SERVER_ERROR, null));
        }catch (Exception e){
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.UPDATE_PHOTO)
    public ResponseEntity<APIResponse> updatePhoto(@PathVariable Long photoId, @RequestParam(value = "file", required = false) MultipartFile file) throws SQLException {
        try {
            if(file == null || file.isEmpty()){
                return ResponseEntity.badRequest().body(new APIResponse(FeedBackMessage.NO_FILE_PROVIDED, null));
            }
            Photo photo = photoService.getPhotoById(photoId);
            if(photo != null) {
                Photo updatedPhoto = photoService.updatePhoto(photo.getId(), file);
                return ResponseEntity.ok(new APIResponse(FeedBackMessage.UPDATE_SUCCESS, updatedPhoto.getId()));
            }
        }catch (ResourceNotFoundException | IOException e){
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(FeedBackMessage.NOT_FOUND, null));
        }
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(FeedBackMessage.SERVER_ERROR, null));
    }

    @DeleteMapping(UrlMapping.DELETE_PHOTO)
    public ResponseEntity<APIResponse> deletePhoto(@PathVariable Long photoId){
        try {
            photoService.deletePhoto(photoId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(FeedBackMessage.NOT_FOUND, null));
        }catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(FeedBackMessage.SERVER_ERROR, null));
        }
    }

    @GetMapping(UrlMapping.GET_PHOTO_BY_ID)
    public ResponseEntity<APIResponse> getPhotoById(@PathVariable Long photoId){
        try {
            Photo thePhoto = photoService.getPhotoById(photoId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, thePhoto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(FeedBackMessage.NOT_FOUND, null));
        }catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(FeedBackMessage.SERVER_ERROR, null));
        }
    }
}
