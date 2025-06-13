package com.dailycodework.universalpetcare.service.review;

import com.dailycodework.universalpetcare.enums.AppointmentStatus;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.exception.UserAlreadyExistException;
import com.dailycodework.universalpetcare.model.Review;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.repository.AppointmentRepository;
import com.dailycodework.universalpetcare.repository.ReviewRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
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
    public void saveReview(Review review, Long reviewerId, Long veterinarianId) {
        //1. Check if the reviewer is same as the doctor being reviewed
        if(veterinarianId.equals(reviewerId)){
            throw new IllegalArgumentException("Veterinarians can not review themselves");
        }
        //2. Check if the reviewer has previously submitted a review for this doctor
        Optional<Review> existingReview = reviewRepository.findByVeterinarianIdAndPatientId(veterinarianId, reviewerId);
        if(existingReview.isPresent()){
            throw new UserAlreadyExistException("You have already rated this veterinarian, you may edit/update your previous review");
        }
        //3. Check if the reviewer has gotten a completed appointment with this doctor
        boolean hadCompletedAppointments = appointmentRepository.existByVeterinarianIdAndPatientIdAndStatus(veterinarianId, reviewerId, AppointmentStatus.COMPLETED);
        if(!hadCompletedAppointments){
            throw new IllegalStateException("Sorry, only patients with a completed appointment within this veterinarian can leave a review");
        }
        //4. Get the reviewer (patient) from the database; Get the veterinarian from the database
        User patient = userRepository.findById(reviewerId).orElseThrow(()-> new ResourceNotFoundException(FeedBackMessage.NOT_FOUND));
        User veterinarian = userRepository.findById(veterinarianId).orElseThrow(()-> new ResourceNotFoundException(FeedBackMessage.NOT_FOUND));
        //5.Set both to the review
        review.setVeterinarian(veterinarian);
        review.setPatient(patient);
        //6. Save the review
        reviewRepository.save(review);
    }

    @Transactional
    @Override
    public double getAverageRatingForVet(Long veterinarianId) {
        List<Review> reviews = reviewRepository.findByVeterinarianId(veterinarianId);
        return reviews.isEmpty() ? 0 : reviews.stream().mapToInt(Review :: getStars).average().orElse(0.0);
    }

    @Override
    public void updateReview(Long reviewerId, Review review) {
        reviewRepository.findById(reviewerId)
                .ifPresentOrElse(existingReview ->{
                    existingReview.setStars(review.getStars());
                    existingReview.setFeedback(review.getFeedback());
                    reviewRepository.save(existingReview);
                }, () -> {throw new ResourceNotFoundException(FeedBackMessage.NOT_FOUND);
                });
    }

    @Override
    public Page<Review> findAllReviewsByUserId(Long userId, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        return reviewRepository.findAllByUserId(userId, pageRequest);
    }
}
