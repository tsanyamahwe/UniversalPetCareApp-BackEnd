package com.dailycodework.universalpetcare.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private String phoneNumber;
    private String email;
    private String userType;
    private boolean isEnabled;
    private String specialization;
    private String bio;
    private LocalDate createdAt;
    private List<AppointmentDTO> appointments;
    private List<ReviewDTO> reviews;
    private Long photoId;
    private byte[] photo;
    private double averageRating;
    private Set<String> roles;
    private Long totalReviewer;
}
