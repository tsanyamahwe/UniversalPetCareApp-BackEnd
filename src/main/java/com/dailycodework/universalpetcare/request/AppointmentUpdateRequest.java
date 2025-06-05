package com.dailycodework.universalpetcare.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentUpdateRequest {
    private String reason;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
}

