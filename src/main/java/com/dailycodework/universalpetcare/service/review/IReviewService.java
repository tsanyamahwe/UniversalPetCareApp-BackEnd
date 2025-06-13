package com.dailycodework.universalpetcare.service.review;

import com.dailycodework.universalpetcare.model.Review;
import org.springframework.data.domain.Page;

public interface IReviewService {
    void saveReview(Review review, Long reviewerId, Long veterinarianId);
    double getAverageRatingForVet(Long veterinarianId);
    void updateReview(Long reviewerId, Review review);
    Page<Review> findAllReviewsByUserId(Long userId, int page, int pageSize);
}
