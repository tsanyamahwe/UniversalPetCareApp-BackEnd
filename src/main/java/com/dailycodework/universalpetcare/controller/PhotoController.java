package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Photo;
import com.dailycodework.universalpetcare.repository.PhotoRepository;
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
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping(UrlMapping.PHOTOS)
@RequiredArgsConstructor
public class PhotoController {
    private final IPhotoService photoService;
    private final PhotoRepository photoRepository;

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
    public ResponseEntity<APIResponse> deletePhoto(@PathVariable Long photoId, @PathVariable Long userId){
        try {
            photoService.deletePhoto(photoId, userId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(FeedBackMessage.NOT_FOUND, null));
        }catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(FeedBackMessage.SERVER_ERROR, null));
        }
    }

    @GetMapping(UrlMapping.GET_PHOTO_BY_ID)
//    public ResponseEntity<APIResponse> getPhotoById(@PathVariable("photoId") Long photoId){
//        try {
//            Photo thePhoto = photoService.getPhotoById(photoId);
//            return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, thePhoto));
//        } catch (ResourceNotFoundException e) {
//            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(FeedBackMessage.NOT_FOUND, null));
//        }catch (Exception e){
//            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(FeedBackMessage.SERVER_ERROR, null));
//        }
//    }
    public ResponseEntity<Map<String, Object>> getPhotoById(@PathVariable Long photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.NOT_FOUND));

        // Manually create response to avoid serialization issues
        Map<String, Object> response = new HashMap<>();
        response.put("id", photo.getId());
        response.put("fileName", photo.getFileName());
        //response.put("uploadDate", photo.getUploadDate());
        //response.put("userId", photo.getUser().getId());
        // DON'T include imageData here

        return ResponseEntity.ok(response);
    }
}
