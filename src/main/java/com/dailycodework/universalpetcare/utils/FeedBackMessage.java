package com.dailycodework.universalpetcare.utils;

import java.util.List;
import java.util.Map;

public class FeedBackMessage {
    public static final String CREATE_SUCCESS = "Resource created successfully";
    public static final String UPDATE_SUCCESS = "Resource updated successfully";
    public static final String RESOURCE_FOUND = "Resource found";
    public static final String DELETE_SUCCESS = "Resource deleted successfully";
    public static final String SENDER_RECIPIENT_NOT_FOUND = "No such Appointment - sender or recipient not found";
    public static final String ALREADY_APPROVED = "Sorry. This appointment can no longer be updated, it is already approved";
    public static final String NOT_FOUND = "Resource not found";
    public static final String SERVER_ERROR = "Server error";
    public static final String NO_FILE_PROVIDED = "No file provided for update";
    public static final String CAN_NOT_SELF_REVIEW = "Veterinarians can not review themselves";
    public static final String ALREADY_REVIEWED_THIS_VET = "You have already rated this veterinarian, you may edit/update your previous review";
    public static final String CAN_NOT_LEAVE_A_REVIEW = "Sorry, only patients with a completed appointment with this veterinarian can leave a review";
    public static final String VET_OR_PATIENT_NOT_FOUND = "Patient or Veterinarian not found";
    public static final String NO_VETS_AVAILABLE = "Sorry! Veterinarians of such specialization category are not available for the requested date and time";
    public static final String CANNOT_CANCEL_APPOINTMENT = "Error cancelling appointment";
    public static final String APPOINTMENT_ALREADY_APPROVED = "The appointment is approved already";
    public static final String RESOURCE_NOT_FOUND = "This resource is not available";
    public static final String CAN_NOT_DELETE = "Cannot delete pet. An appointment must have at least one pet associated with it.";
    public static final String CAN_NOT_BE_NULL = "Pet cannot be null";
    public static final String NAME_REQUIRED = "Pet name is required";
    public static final String TYPE_REQUIRED = "Pet type is required";
    public static final String BREED_REQUIRED = "Pet breed is required";
    public static final String COLOR_REQUIRED = "Pet color is required";
    public static final String AGE_REQUIREMENT = "Pet age must be a valid positive number";
    public static final String NOT_AVAILABLE = "No Appointment with the associated ID";
    public static final String SUCCESS_SUMMARY = "Appointment summary retrieved successfully";
    public static final String ERROR_SUMMARY = "Error retrieving summary: ";
    public static final String NO_VETERINARIANS = "No veterinarians registered yet";
    public static final String LOCKED = "User account locked successfully";
    public static final String UNLOCKED = "User account unlocked successfully";
}
