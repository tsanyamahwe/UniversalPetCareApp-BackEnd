package com.dailycodework.universalpetcare.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String gender;
    //@Column(name = "mobile")
    private String phoneNumber;
    private String specialization;
}
