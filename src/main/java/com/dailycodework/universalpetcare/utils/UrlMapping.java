package com.dailycodework.universalpetcare.utils;

public class UrlMapping {
    /*============= Start End User API================*/
    public static final String API = "/api/v1";
    public static final String USERS = API+"/users";
    public static final String REGISTER_USER = "/register";
    public static final String UPDATE_USER = "/update/{userId}";
    public static final String GET_USER_BY_ID = "/user/{userId}";
    public static final String DELETE_USER_BY_ID = "/delete/{userId}";
    public static final String GET_ALL_USERS = "/all-users";
    public static final String COUNT_ALL_USERS = "/count/users";
    public static final String COUNT_ALL_VETS = "/count/veterinarians";
    public static final String COUNT_ALL_PATIENTS = "/count/patients";
    public static final String AGGREGATE_USERS = "/aggregated-users";
    public static final String AGGREGATE_STATUS = "/accounts/aggregated-by-status";
    /*============= End End User API ==================*/

    /*============= Start Appointment API =============*/
    public static final String APPOINTMENTS = API+"/appointments";
    public static final String ALL_APPOINTMENTS = "/all";
    public static final String BOOK_APPOINTMENT = "/book-appointment";
    public static final String GET_APPOINTMENT_BY_ID = "/appointment/{id}/fetchappointment";
    public static final String GET_APPOINTMENT_BY_NO = "/appointment/{appointmentNo}/appointment";
    public static final String DELETE_APPOINTMENT = "/appointment/{id}/delete";
    public static final String UPDATE_APPOINTMENT = "/appointment/{id}/update";
    public static final String CANCEL_APPOINTMENT = "/appointment/{id}/cancel";
    public static final String APPROVE_APPOINTMENT = "/appointment/{id}/approve";
    public static final String DECLINE_APPOINTMENT = "/appointment/{id}/decline";
    public static final String COUNT_APPOINTMENTS = "/count/appointments";
    public static final String APPOINTMENT_SUMMARY = "/summary/appointments-summary";
    /*================ End Appointment API ===============*/

    /*================ Start Pet API =====================*/
    public static final String PETS = API+"/pets";
    public static final String SAVE_PETS_FOR_APPOINTMENTS = "/save-pets";
    public static final String GET_PET_BY_ID = "/pet/{id}";
    public static final String DELETE_PET_BY_ID = "/pet/{id}/delete";
    public static final String PET_UPDATE = "/pet/{id}/update";
    public static final String GET_PET_TYPES = "/get-pet-types";
    public static final String GET_PET_COLORS = "/get-pet-colors";
    public static final String GET_PET_BREEDS = "/get-pet-breeds";
    public static final String ADD_PET_TO_APPOINTMENT = "/save-pet-for-appointment/{appointmentId}";
    /*=============== End Pet API =======================*/

    /*================== Start Photo API ================*/
    public static final String PHOTOS = API+"/photos";
    public static final String UPLOAD_PHOTO = "/photo/upload";
    public static final String UPDATE_PHOTO = "/photo/{photoId}/update";
    public static final String DELETE_PHOTO = "/photo/{photoId}/user/{userId}/delete";
    public static final String GET_PHOTO_BY_ID = "/photo/{photoId}";
    /*================= End Photo API =====================*/

    /*===================== Start Review API ==================*/
    public static final String REVIEWS = API+"/reviews";
    public static final String SUBMIT_REVIEW = "/submit-review";
    public static final String GET_USER_REVIEWS = "/user/{userId}/reviews";
    public static final String UPDATE_REVIEW = "/review/{reviewId}/update";
    public static final String DELETE_REVIEW = "/review/{reviewId}/delete";
    public static final String GET_AVG_REVIEWS = "/vet/{veterinarianId}/get-average-rating";
    /*====================== End Review API ===================*/

    /*==================== Start Veterinarians API =================*/
    public static final String VETERINARIANS = API+"/veterinarians";
    public static final String GET_ALL_VETERINARIANS = "/get-all-veterinarians";
    public static final String SEARCH_VETERINARIAN_FOR_APPOINTMENT = "/search-veterinarian";
    public static final String GET_VET_SPECIALIZATIONS = "/get-vet-specializations";
    public static final String VETS_AGGREGATE_BY_SPECIALIZATION = "/aggregate-vets-by-specialization";
    /*====================== End Veterinarians API ===================*/

    /*==================== Start Change Password API ====================*/
    public static final String CHANGE_PASSWORD = "/user/{userId}/change-password";
    /*===================== End Change Password API ====================*/
}
