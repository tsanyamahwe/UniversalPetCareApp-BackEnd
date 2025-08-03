package com.dailycodework.universalpetcare.service.user;

import com.dailycodework.universalpetcare.dto.AppointmentDTO;
import com.dailycodework.universalpetcare.dto.EntityConverter;
import com.dailycodework.universalpetcare.dto.ReviewDTO;
import com.dailycodework.universalpetcare.dto.UserDTO;
import com.dailycodework.universalpetcare.exception.ResourceNotFoundException;
import com.dailycodework.universalpetcare.factory.UserFactory;
import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.model.Review;
import com.dailycodework.universalpetcare.model.User;
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
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public User register(RegistrationRequest registrationRequest){
        return userFactory.createUser(registrationRequest);
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
        return userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException(FeedBackMessage.NOT_FOUND));
    }

    @Override
    @Transactional
//    public void delete(Long userId){
//        userRepository.findById(userId)
//                .ifPresentOrElse(userToDelete ->{
//                    List<Review> reviews = new ArrayList<>(reviewRepository.findAllByUserId(userId));
//                    reviewRepository.deleteAll(reviews);
//                    List<Appointment> appointments = new ArrayList<>(appointmentRepository.findAllAppointmentsByUserId(userId));
//                    for(Appointment appointment: appointments) {
//                        appointmentRepository.delete(appointment);
//                    }
//                    userRepository.deleteById(userId);
//                    }, ()-> {throw new ResourceNotFoundException(FeedBackMessage.NOT_FOUND);});
//    }
    public void delete(Long userId){
        userRepository.findById(userId)
                .ifPresentOrElse(userToDelete -> {
                    //Delete Reviews first
                    List<Review> reviews = new ArrayList<>(reviewRepository.findAllByUserId(userId));
                    reviewRepository.deleteAll(reviews);

                    //Delete each appointment by ID
                    List<AppointmentDTO> appointments = appointmentService.getUserAppointments(userId);
                    for (AppointmentDTO appointment : appointments) {
                        //force loading of pets and clear the relationship
                        appointmentService.deleteAppointmentById(appointment.getId());
                    }

                    //Finally delete the user
                    userRepository.deleteById(userId);
                }, () -> {
                    throw new ResourceNotFoundException(FeedBackMessage.NOT_FOUND);
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
        System.out.println("=================== The user is ================ "+ user);
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
}
