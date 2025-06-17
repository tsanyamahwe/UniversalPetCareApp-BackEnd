package com.dailycodework.universalpetcare.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private int stars;
    private String feedback;
    private Long veterinarianId;
    private String veterinarianName;
    private Long patientId;
    private String patientName;
    private byte[] veterinarianPhoto;
    private byte[] patientPhoto;
}
