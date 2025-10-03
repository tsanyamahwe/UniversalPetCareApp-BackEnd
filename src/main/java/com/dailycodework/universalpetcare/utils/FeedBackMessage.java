package com.dailycodework.universalpetcare.utils;

import java.util.List;
import java.util.Map;

public class FeedBackMessage {
    /*=================================================Start Admin API===========================================================*/

    /*=================================================End Admin API=============================================================*/

    /*=================================================Start Appointment API======================================================*/
    public static final String APPOINTMENTS_FOUND = "Appointments found";
    public static final String APPOINTMENT_CREATED = "Appointment created successfully";
    public static final String APPOINTMENT_FOUND_BY_ID = "Appointment found by ID";
    public static final String APPOINTMENT_FOUND_BY_NO = "Appointment found by Number";
    public static final String APPOINTMENT_DELETED = "Appointment deleted successfully";
    public static final String APPOINTMENT_UPDATED = "Appointment updated successfully";
    public static final String APPOINTMENT_CANCELLED = "Appointment cancelled successfully";
    public static final String APPOINTMENT_APPROVED = "Appointment approved successfully";
    public static final String APPOINTMENT_DECLINED = "Appointment declined";
    public static final String APPOINTMENT_SUMMARY = "Appointment summary retrieved successfully";
    public static final String APPOINTMENT_ERROR_SUMMARY = "Summary on error retrieving appointment details: ";
    public static final String SENDER_RECIPIENT_NOT_FOUND = "No such Appointment - sender or recipient not found";
    public static final String OPERATION_NOT_ALLOWED = "Operation not allowed";
    public static final String APPOINTMENT_NOT_FOUND = "The appointment can not be found, it is not available";
    public static final String CANNOT_CANCEL_APPOINTMENT = "Error: you can not cancel the appointment";
    /*=================================================End Appointment API=======================================================*/

    /*==============================================Start Authorization API======================================================*/
    public static final String SUCCESS_AUTH = "Authorization is Successful";
    public static final String AUTH_DISABLED = "Sorry, your account is disabled. Please contact the service desk";
    public static final String AUTH_FAILED = "Authentication Failed";
    public static final Object AUTH_REASON = "Invalid username or password";
    /*===============================================End Authorization API=======================================================*/

    /*================================================Start Patient API==========================================================*/
    public static final String PATIENTS_FOUND = "Patients found successfully";
    /*==================================================End Patient API==========================================================*/

    /*==================================================Start Pet API============================================================*/
    public static final String PET_FOUND = "The pet was found";
    public static final String PETS_ADDED = " And pet(s) were successfully added to the appointment";
    public static final String PET_DELETED = "The pet was deleted successfully";
    public static final String PET_UPDATED = "The pet(s) was updated successfully";
    public static final String PETS_FOUND_TYPES = "The pets were found by their types";
    public static final String PETS_FOUND_COLORS = "The pets were found by their colors";
    public static final String PETS_FOUND_BREED = "The pets were found by their breed";
    public static final String CANNOT_DELETE_PET = "Cannot delete pet. An appointment must have at least one pet associated with it.";
    public static final String PET_NOT_FOUND = "The pet can not be found";
    public static final String PET_CANNOT_BE_NULL = "Pet cannot be null";
    public static final String PET_NAME_REQUIRED = "Pet name is required";
    public static final String PET_TYPE_REQUIRED = "Pet type is required";
    public static final String PET_BREED_REQUIRED = "Pet breed is required";
    public static final String PET_COLOR_REQUIRED = "Pet color is required";
    public static final String PET_AGE_REQUIREMENT = "Pet age must be a valid positive number";
    /*====================================================End Pet API============================================================*/

    /*==================================================Start Photo API==========================================================*/
    public static final String PHOTO_UPLOADED = "Photo uploaded successfully";
    public static final String PHOTO_SERVER_ERROR1 = "Server error on photo upload";
    public static final String NO_FILE_PROVIDED = "No file provided for the photo update";
    public static final String PHOTO_UPDATED = "The photo was updated successfully";
    public static final String PHOTO_NOT_FOUND = "The photo was not found";
    public static final String PHOTO_DELETED = "The photo was deleted successfully";
    public static final String PHOTO_SERVER_ERROR2 = "Server error on photo update";
    public static final String PHOTO_SERVER_ERROR3 = "Server error on deleting the photo";
    /*===================================================End Photo API============================================================*/

    /*==================================================Start Review API==========================================================*/
    public static final String REVIEWS_NOT_FOUND = "The review(s) not found:";
    public static final String REVIEW_CREATED = "The review was created successfully";
    public static final String REVIEWS_FOUND = "The review(s) found";
    public static final String REVIEW_UPDATED = "Review updated successfully";
    public static final String REVIEW_DELETED = "The review was deleted successfully";
    public static final String CAN_NOT_SELF_REVIEW = "Veterinarians can not review themselves";
    public static final String ALREADY_REVIEWED_THIS_VET = "You have already rated this veterinarian, you may edit/update your previous review";
    public static final String CAN_NOT_LEAVE_A_REVIEW = "Sorry, only patients with a completed appointment with this veterinarian can leave a review";
    public static final String VET_OR_PATIENT_NOT_FOUND = "Patient or Veterinarian not found";
    /*===================================================End Review API===========================================================*/

    /*==================================================Start User API============================================================*/
    public static final String USER_CREATED = "The user was registered successfully. Go and verify this registration from your email address";
    public static final String USER_UPDATED = "The user entity was updated successfully";
    public static final String USER_FOUND = "The user entity was found";
    public static final String USERS_FOUND = "The users were found";
    public static final String USER_DELETED = "The user entity was deleted successfully";
    public static final String USER_PASSWORD_CHANGED = "The password was updated successfully";
    public static final String USER_LOCKED = "User account locked successfully";
    public static final String USER_UNLOCKED = "User account unlocked successfully";
    public static final String USER_NOT_FOUND = "The user entity was not found:";
    public static final String PASS_INFO = "Change password information";
    /*===================================================End User API=============================================================*/

    /*==========================================Start Verification Token API======================================================*/
    public static final String VERIFICATION_TOKEN_INVALID = "INVALID";
    public static final String VERIFICATION_TOKEN_VERIFIED = "VERIFIED";
    public static final String VERIFICATION_TOKEN_EXPIRED = "EXPIRED";
    public static final String VERIFICATION_TOKEN_VALID = "VALID";
    public static final String VERIFICATION_VALIDATION_ERROR= "Error";
    public static final String TOKEN_NOT_FOUND = "The token was not found";
    public static final String TOKEN_SAVED_SUCCESS = "Token saved successfully";
    public static final String NEW_TOKEN = "New token has been created: ";
    public static final String TOKEN_DELETED = "Token deleted successfully";
    public static final String INVALID_TOKEN = "Invalid verification token ";
    public static final String VERIFICATION_NOTICE = "Please enter the email associated with your account";
    public static final String PASSWORD_RESET = "Your password has been reset";
    public static final String VERIFICATION_UPDATE = "A link was sent to your email, please check to complete your password reset request";
    public static final String MISSING_AUTH = "Missing token or password";
    public static final String PASSWORD_REUSE_FORBIDDEN = "You can not repeat the same old password, choose another one!";
    public static final String INVALID_PASSWORD_FORMAT = "Your password should have at least 8 characters, 1 capital letter, 1 number, and 1 special character";
    public static final String NEW_VERIF_TOKEN_SEND = "The new verification token has been send to your email";
    /*===========================================End Verification Token API=======================================================*/

    /*================================================Start Veterinarian API==========================================================*/
    public static final String VETERINARIANS_FOUND = "Veterinarians were found";
    public static final String VETERINARIAN_FOUND = "Veterinarian found successfully";
    public static final String NO_VETS_AVAILABLE = "Sorry! Veterinarians of such specialization category are not available for the requested date and time";
    public static final String VET_SPEC_FOUND = "The veterinarian specializations found";
    public static final String SPECIALIZATION_ALREADY_EXISTS = "Specialization already exists";
    public static final String SPECIALIZATION_REQUIRED = "Specialization cannot be null or empty";
    public static final String VET_ID_REQUIRED = "Veterinarian ID cannot be null";
    public static final String VET_NOT_FOUND = "Veterinarian not found with id: ";
    public static final String VET_SPEC_ERROR = "Error updating specialization: ";
    /*==================================================End Veterinarian API==========================================================*/

    public static final String NOT_FOUND = "Resource not found";
    public static final String RESOURCE_NOT_FOUND = "This resource is not available";



}
