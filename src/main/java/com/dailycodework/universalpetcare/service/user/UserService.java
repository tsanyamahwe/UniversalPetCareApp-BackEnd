package com.dailycodework.universalpetcare.service.user;

import com.dailycodework.universalpetcare.dto.*;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.factory.UserFactory;
import com.dailycodework.universalpetcare.model.Review;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.request.ChangePasswordRequest;
import com.dailycodework.universalpetcare.service.password.ChangePasswordService;
import com.dailycodework.universalpetcare.utils.PasswordValidator;
import com.dailycodework.universalpetcare.repository.AppointmentRepository;
import com.dailycodework.universalpetcare.repository.ReviewRepository;
import com.dailycodework.universalpetcare.repository.UserRepository;
import com.dailycodework.universalpetcare.repository.VeterinarianRepository;
import com.dailycodework.universalpetcare.request.RegistrationRequest;
import com.dailycodework.universalpetcare.request.UserUpdateRequest;
import com.dailycodework.universalpetcare.service.appointment.AppointmentService;
import com.dailycodework.universalpetcare.service.photo.PhotoService;
import com.dailycodework.universalpetcare.service.review.ReviewService;
import com.dailycodework.universalpetcare.utils.FeedBackMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final VeterinarianRepository veterinarianRepository;
    private final EntityConverter<User, UserDTO> entityConverter;
    private final AppointmentService appointmentService;
    private final PhotoService photoService;
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;
    private final PasswordValidator passwordValidator;
    private final ChangePasswordService changePasswordService;

    @Override
    public User register(RegistrationRequest registrationRequest){
        PasswordValidator.ValidationResult validationResult = passwordValidator.validatePassword(registrationRequest.getPassword());
        if(!validationResult.isValid()){
            throw new IllegalArgumentException(validationResult.getMessage());
        }
        User user = userFactory.createUser(registrationRequest);
        user.updatePasswordChangeInfo();
        return userRepository.save(user);
    }

    @Override
    public void changeUserPassword(Long userId, String currentPassword, String newPassword, String confirmNewPassword){
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword(currentPassword);
        changePasswordRequest.setNewPassword(newPassword);
        changePasswordRequest.setConfirmNewPassword(confirmNewPassword);

        changePasswordService.changePassword(userId, changePasswordRequest);
    }

    @Override
    public boolean canUserChangePassword(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND));
        return user.canChangePassword();
    }

    @Override
    public long getDaysUntilPasswordChangeAllowed(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND));
        return user.getDaysUntilPasswordChangeAllowed();
    }

    @Override
    public PasswordChangeInfoDTO getPasswordChangeInfo(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND));
        return PasswordChangeInfoDTO.builder()
                .canChangePassword(user.canChangePassword())
                .daysUntilAllowed(user.getDaysUntilPasswordChangeAllowed())
                .lastPasswordChange(user.getLastPasswordChange())
                .passwordChangeCount(user.getPasswordChangeCount())
                .build();
    }

    @Override
    public User update(Long userId, UserUpdateRequest userUpdateRequest){
        User user = findById(userId);
        user.setFirstName(userUpdateRequest.getFirstName());
        user.setLastName(userUpdateRequest.getLastName());
        user.setGender(userUpdateRequest.getGender());
        user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        user.setSpecialization(userUpdateRequest.getSpecialization());
        return userRepository.save(user);
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public void delete(Long userId){
        userRepository.findById(userId)
                .ifPresentOrElse(userToDelete -> {
                    //Delete Reviews first
                    List<Review> reviews = new ArrayList<>(reviewRepository.findAllByUserId(userId));
                    reviewRepository.deleteAll(reviews);
                    //Delete each appointment by ID
                    List<AppointmentDTO> appointments = appointmentService.getUserAppointments(userId);
                    for (AppointmentDTO appointment : appointments) {
                        appointmentService.deleteAppointmentById(appointment.getId());//force loading of pets and clear the relationship
                    }
                    userRepository.deleteById(userId);//Finally delete the user
                }, () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.USER_NOT_FOUND);
        });
    }

    @Override
    public List<UserDTO> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> entityConverter.mapEntityToDTO(user,UserDTO.class)).collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserWithDetails(Long userId) throws SQLException {
        //1. get the User
        User user = findById(userId);
        System.out.println("====== The user is ====== "+ user);
        //2. convert the User to a UserDTO
        UserDTO userDTO = entityConverter.mapEntityToDTO(user, UserDTO.class);
        userDTO.setTotalReviewer(reviewRepository.countByVeterinarianId(userId));
        //3. get user appointments (users(patient and vet))
        setUserAppointment(userDTO);
        setUserPhoto(userDTO, user);
        //4. get user reviews(user(patient and vet))
        setUserReviews(userDTO, userId);
        return userDTO;
    }

    private void setUserAppointment(UserDTO userDTO){
        List<AppointmentDTO> appointmentDTOS = appointmentService.getUserAppointments(userDTO.getId());
        userDTO.setAppointments(appointmentDTOS);
    }

    private void setUserPhoto(UserDTO userDTO, User user) throws SQLException {
        if(user.getPhoto() != null){
            userDTO.setPhotoId(user.getPhoto().getId());
            userDTO.setPhoto(photoService.getPhotoData(user.getPhoto().getId()));
        }
    }

    @SneakyThrows
    private void setUserReviews(UserDTO userDTO, Long userId){
        Page<Review> pageReview = reviewService.findAllReviewsByUserId(userId, 0, Integer.MAX_VALUE);
        List<ReviewDTO> reviewDTO = pageReview.getContent().stream().map(this::mapReviewToDTO).toList();
        if(!reviewDTO.isEmpty()){
            double averageRating = reviewService.getAverageRatingForVet(userId);
            userDTO.setAverageRating(averageRating);
        }
        userDTO.setReviews(reviewDTO);
    }

    private ReviewDTO mapReviewToDTO(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setId(review.getId());
        reviewDTO.setStars(review.getStars());
        reviewDTO.setFeedback(review.getFeedback());
        mapVeterinarianInfo(reviewDTO, review);
        mapPatientInfo(reviewDTO, review);
        return reviewDTO;
    }

    private void mapVeterinarianInfo(ReviewDTO reviewDTO, Review review) {
        if(review.getVeterinarian() != null){
            reviewDTO.setVeterinarianId(review.getVeterinarian().getId());
            reviewDTO.setVeterinarianName(review.getVeterinarian().getFirstName()+ " " +review.getVeterinarian().getLastName());
            //set the photo
            setVeterinarianPhoto(reviewDTO, review);
        }
    }

    private void mapPatientInfo(ReviewDTO reviewDTO, Review review) {
        if(review.getPatient() != null) {
            reviewDTO.setPatientId(review.getPatient().getId());
            reviewDTO.setPatientName(review.getPatient().getFirstName() + " " + review.getPatient().getLastName());
            //set photo
            setPatientPhoto(reviewDTO, review);
        }
    }

    private void setPatientPhoto(ReviewDTO reviewDTO, Review review) {
        if(review.getPatient().getPhoto() != null){
            try {
                reviewDTO.setPatientPhoto(photoService.getPhotoData(review.getPatient().getPhoto().getId()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            reviewDTO.setPatientPhoto(null);
        }
    }

    private void setVeterinarianPhoto(ReviewDTO reviewDTO, Review review) {
        if(review.getVeterinarian().getPhoto() != null){
            try {
                reviewDTO.setVeterinarianPhoto(photoService.getPhotoData(review.getVeterinarian().getPhoto().getId()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            reviewDTO.setVeterinarianPhoto(null);
        }
    }

    @Override
    public long countVeterinarians(){
        return userRepository.countByUserType("VET");
    }

    @Override
    public long countPatients(){
        return userRepository.countByUserType("PATIENT");
    }

    @Override
    public long countAllUsers(){
        return userRepository.count();
    }

    @Override
    public Map<String, Map<String, Long>> aggregateUsersByMonthAndType(){
        List<User> users = userRepository.findAll();
        return users.stream().collect(Collectors.groupingBy(user -> Month.of(user.getCreatedAt().getMonthValue())
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH), Collectors.groupingBy(User::getUserType, Collectors.counting())
        ));
    }

    @Override
    public Map<String, Map<String, Long>> aggregatesUsersByEnabledStatusAndType(){
        List<User> users = userRepository.findAll();
        return users.stream().collect(Collectors.groupingBy(user -> user.isEnabled() ? "Enabled" : "Non-Enabled",
                Collectors.groupingBy(User::getUserType, Collectors.counting())));
    }

    @Override
    public void lockUserAccount(Long userId){
        userRepository.updateUserEnabledStatus(userId, false);
    }

    @Override
    public void unLockUserAccount(Long userId){
        userRepository.updateUserEnabledStatus(userId, true);
    }
}
