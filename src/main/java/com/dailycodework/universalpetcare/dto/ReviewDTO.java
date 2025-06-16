package com.dailycodework.universalpetcare.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private int stars;
    private String feedback;
}
