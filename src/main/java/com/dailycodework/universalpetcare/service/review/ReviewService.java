package com.dailycodework.universalpetcare.service.review;

import com.dailycodework.universalpetcare.enums.AppointmentStatus;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.exception.AlreadyExistException;
import com.dailycodework.universalpetcare.model.Review;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.AppointmentRepository;
import com.dailycodework.universalpetcare.repository.ReviewRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.request.ReviewUpdateRequest;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService implements IReviewService{
    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Override
    public Review saveReview(Review review, Long reviewerId, Long veterinarianId) {
        //1. Check if the reviewer is same as the doctor being reviewed
        if(veterinarianId.equals(reviewerId)){
            throw new IllegalArgumentException(FeedBackMessage.CAN_NOT_SELF_REVIEW);
        }
        //2. Check if the reviewer has previously submitted a review for this doctor
//        Optional<Review> existingReview = reviewRepository.findByVeterinarianIdAndPatientId(veterinarianId, reviewerId);
//        if(existingReview.isPresent()){
//            throw new AlreadyExistException(FeedBackMessage.ALREADY_REVIEWED_THIS_VET);
//        }
        //3. Check if the reviewer has gotten a completed appointment with this doctor
//        boolean hadCompletedAppointments = appointmentRepository.existsByVeterinarianIdAndPatientIdAndStatus(veterinarianId, reviewerId, AppointmentStatus.COMPLETED);
//        if(!hadCompletedAppointments){
//            throw new IllegalStateException(FeedBackMessage.CAN_NOT_LEAVE_A_REVIEW);
//        }
        //4. Get the reviewer (patient) from the database; Get the veterinarian from the database
        User patient = userRepository.findById(reviewerId).orElseThrow(()-> new ResourceNotFoundException(FeedBackMessage.VET_OR_PATIENT_NOT_FOUND));
        User veterinarian = userRepository.findById(veterinarianId).orElseThrow(()-> new ResourceNotFoundException(FeedBackMessage.VET_OR_PATIENT_NOT_FOUND));
        //5.Set both to the review
        review.setVeterinarian(veterinarian);
        review.setPatient(patient);
        //6. Save the review
        return reviewRepository.save(review);
    }

    @Transactional
    @Override
    public double getAverageRatingForVet(Long veterinarianId) {
        List<Review> reviews = reviewRepository.findByVeterinarianId(veterinarianId);
        return reviews.isEmpty() ? 0.0 : reviews.stream().mapToDouble(Review :: getStars).average().orElse(0.0);
    }

    @Override
    public Review updateReview(Long reviewerId, ReviewUpdateRequest reviewUpdateRequest) {
        return reviewRepository.findById(reviewerId)
                .map(existingReview ->{
                    existingReview.setStars(reviewUpdateRequest.getStars());
                    existingReview.setFeedback(reviewUpdateRequest.getFeedback());
                    return reviewRepository.save(existingReview);
                }).orElseThrow(()-> new ResourceNotFoundException(FeedBackMessage.NOT_FOUND));
    }

    @Override
    public Page<Review> findAllReviewsByUserId(Long userId, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return reviewRepository.findAllByUserId(userId, pageRequest);
    }

    @Override
    public void deleteReview(Long reviewerId) {
        reviewRepository.findById(reviewerId).ifPresentOrElse(Review::removeRelationship, () -> {throw new ResourceNotFoundException(FeedBackMessage.NOT_FOUND);});
        reviewRepository.deleteById(reviewerId);
    }
}
