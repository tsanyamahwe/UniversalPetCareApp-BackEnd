package com.dailycodework.universalpetcare.controller;

import com.dailycodework.universalpetcare.dto.ReviewDTO;
import com.dailycodework.universalpetcare.exception.AlreadyExistException;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.model.Review;
import com.dailycodework.universalpetcare.request.ReviewUpdateRequest;
import com.dailycodework.universalpetcare.response.APIResponse;
import com.dailycodework.universalpetcare.service.review.IReviewService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import com.dailycodework.universalpetcare.utils.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@CrossOrigin("http://localhost:5173")
@RequiredArgsConstructor
@RestController
@RequestMapping(UrlMapping.REVIEWS)
public class ReviewController {
    private final IReviewService reviewService;
    private final ModelMapper modelMapper;

    @PostMapping(UrlMapping.SUBMIT_REVIEW)
    public ResponseEntity<APIResponse> saveReview(@RequestBody Review review, @RequestParam Long reviewerId,@RequestParam Long veterinarianId){
        try {
            Review savedReviewed = reviewService.saveReview(review, reviewerId, veterinarianId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.CREATE_SUCCESS, savedReviewed));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(NOT_ACCEPTABLE).body(new APIResponse(e.getMessage(), null));
        }catch (AlreadyExistException e){
            return ResponseEntity.status(CONFLICT).body(new APIResponse(e.getMessage(), null));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_USER_REVIEWS)
    public ResponseEntity<APIResponse> getReviewsByUserId(@PathVariable Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int pageSize){
        try{
            Page<Review> reviewPage = reviewService.findAllReviewsByUserId(userId,page, pageSize);
            Page<ReviewDTO> reviewDTO = reviewPage.map((element) -> modelMapper.map(element, ReviewDTO.class));
            return ResponseEntity.status(FOUND).body(new APIResponse(FeedBackMessage.RESOURCE_FOUND, reviewDTO));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(FeedBackMessage.NOT_FOUND, null));
        }catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @PutMapping(UrlMapping.UPDATE_REVIEW)
    public ResponseEntity<APIResponse> updateReview(@PathVariable Long reviewId, @RequestBody ReviewUpdateRequest reviewUpdateRequest){
        try{
            Review updatedReview = reviewService.updateReview(reviewId, reviewUpdateRequest);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.UPDATE_SUCCESS, updatedReview));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(FeedBackMessage.NOT_FOUND, null));
        }catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping(UrlMapping.DELETE_REVIEW)
    public ResponseEntity<APIResponse> deleteReview(@PathVariable Long reviewId){
        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.DELETE_SUCCESS, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new APIResponse(e.getMessage(), null));
        }catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }

    @GetMapping(UrlMapping.GET_AVG_REVIEWS)
    public ResponseEntity<APIResponse> getAverageRatingForVet(@PathVariable Long veterinarianId){
        try {
            double averageRating = reviewService.getAverageRatingForVet(veterinarianId);
            return ResponseEntity.ok(new APIResponse(FeedBackMessage.RESOURCE_FOUND, averageRating));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new APIResponse(e.getMessage(), null));
        }
    }
}
