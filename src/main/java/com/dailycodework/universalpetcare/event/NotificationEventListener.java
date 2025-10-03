package com.dailycodework.universalpetcare.event;

import com.dailycodework.universalpetcare.email.EmailService;
import com.dailycodework.universalpetcare.event.listener.*;
import com.dailycodework.universalpetcare.model.Appointment;
import com.dailycodework.universalpetcare.model.User;
import com.dailycodework.universalpetcare.service.token.IVerificationTokenService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationEventListener implements ApplicationListener<ApplicationEvent> {
    private final EmailService emailService;
    private final IVerificationTokenService verificationTokenService;
    private static final Logger logger = LoggerFactory.getLogger(NotificationEventListener.class);

    @Value("${frontend.base.url}")
    private String frontEndBasedUrl;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        try{
            if (event instanceof RegistrationCompleteEvent registrationCompleteEvent) {
                handleSendRegistrationVerificationEmail(registrationCompleteEvent);
            }else if (event instanceof AppointmentBookedEvent appointmentBookedEvent) {
                handleAppointmentBookedNotification(appointmentBookedEvent);
            }else if (event instanceof AppointmentApprovedEvent appointmentApprovedEvent) {
                handleAppointmentApprovedNotification(appointmentApprovedEvent);
            }else if (event instanceof AppointmentDeclinedEvent appointmentDeclinedEvent) {
                handleAppointmentDeclinedNotification(appointmentDeclinedEvent);
            } else if (event instanceof PasswordResetEvent passwordResetEvent) {
            handlePasswordResetRequest(passwordResetEvent);
            }
        } catch (Exception e) {
            logger.error(event.getClass().getSimpleName());
        }
    }

    /*==============================Start User registration email verification==============================*/
    private void handleSendRegistrationVerificationEmail(RegistrationCompleteEvent registrationCompleteEvent) {
        try{
            User user = registrationCompleteEvent.getUser();
            String verificationToken = UUID.randomUUID().toString();//Generate a token for the user
            verificationTokenService.saveVerificationTokenForUser(verificationToken, user);//Save the token for the user
            String verificationUrl = frontEndBasedUrl + "/email-verification?token=" + verificationToken;//Build the verification URL
            sendRegistrationVerificationEmail(user, verificationUrl);
        }catch (Exception e){
            logger.error(registrationCompleteEvent.getUser().getEmail(), e.getMessage(), e);
        }
    }

    private void sendRegistrationVerificationEmail(User user, String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Verify Your Email";
        String senderName = "Universal Pet Care Notification Services";
        String mailContent = "<p> Hi, " + user.getFirstName() + ", </p>" +
                "<p>Thank you for registering with us." +
                " Please follow the link below to complete your registration.</p>" +
                "<a href=\"" + url + "\"> Verify your email</a>" +
                "<p>Thank you <br> Universal Pet Care Services";
        emailService.sendEmail(user.getEmail(), senderName, subject, mailContent);
    }
    /*====================================End User registration email verification==========================*/

    /*===============================Start New Appointment booked notifications=============================*/
    private void handleAppointmentBookedNotification(AppointmentBookedEvent appointmentBookedEvent) throws MessagingException, UnsupportedEncodingException {
        Appointment appointment = appointmentBookedEvent.getAppointment();
        User veterinarian = appointment.getVeterinarian();
        sendAppointmentBookedNotification(veterinarian, frontEndBasedUrl);
    }

    private void sendAppointmentBookedNotification(User user, String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "New Appointment Notification";
        String senderName = "Universal Pet Care Notification Services";
        String mailContent = "<p> Hi, " + user.getFirstName() + ", </p>" +
                "<p>You have a new appointment schedule:</p>" +
                "<a href=\"" + url + "\"> Please check the clinic portal to view appointment details.</a><br/>" +
                "<p> Best Regards.<br> Universal Pet Care Services";
        emailService.sendEmail(user.getEmail(), senderName, subject, mailContent);
        /*===============================End New Appointment booked notifications=============================*/
    }

    /*===============================Start Approve Appointment notifications==================================*/
    private void handleAppointmentApprovedNotification(AppointmentApprovedEvent appointmentApprovedEvent) throws MessagingException, UnsupportedEncodingException {
        Appointment appointment = appointmentApprovedEvent.getAppointment();
        User patient = appointment.getPatient();
        senderAppointmentApprovedNotification(patient, frontEndBasedUrl);
    }

    private void senderAppointmentApprovedNotification(User user, String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Appointment Approved";
        String senderName = "Universal Pet Care Notification Services";
        String mailContent = "<p> Hi, " + user.getFirstName() + ", </p>" +
                "<p>Your appointment has been approved:</p>" +
                "<a href=\"" + url + "\"> Please check the clinic portal to view appointment details and the veterinarian information</a><br/>" +
                "<p> Best Regards.<br> Universal Pet Care Services";
        emailService.sendEmail(user.getEmail(), senderName, subject, mailContent);
    }
    /*===============================End Approve Appointment notifications====================================*/

    /*===============================Start Decline Appointment notifications==================================*/
    private void handleAppointmentDeclinedNotification(AppointmentDeclinedEvent appointmentDeclinedEvent) throws MessagingException, UnsupportedEncodingException {
        Appointment appointment = appointmentDeclinedEvent.getAppointment();
        User patient = appointment.getPatient();
        sendAppointmentDeclinedNotification(patient, frontEndBasedUrl);
    }

    private void sendAppointmentDeclinedNotification(User user, String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Appointment Not Approved";
        String senderName = "Universal Pet Care Notification Services";
        String mailContent = "<p> Hi, " + user.getFirstName() + ", </p>" +
                "<p>We are sorry, your appointment was not approved at this moment, <br/>" +
                "Please kindly make a reschedule for another date. Thank you.</p>" +
                "<a href=\"" + url + "\"> Please check the clinic portal to view appointment details.</a><br/>" +
                "<p> Best Regards.<br> Universal Pet Care Services";
        emailService.sendEmail(user.getEmail(), senderName, subject, mailContent);
    }
    /*===============================End Decline Appointment notifications==================================*/

    /*=================================Start Password Reset notifications====================================*/
    private void handlePasswordResetRequest(PasswordResetEvent passwordResetEvent){
        User user = passwordResetEvent.getUser();
        String token = passwordResetEvent.getToken();//UUID.randomUUID().toString();
        //verificationTokenService.saveVerificationTokenForUser(token, user);
        String resetUrl = frontEndBasedUrl + "/reset-password?token=" + token;
        try {
            sendPasswordResetEmail(user, resetUrl);
        }catch (MessagingException | UnsupportedEncodingException e){
            throw  new RuntimeException("Failed to send password reset email", e);
        }
    }

    private void sendPasswordResetEmail(User user, String resetUrl) throws MessagingException, UnsupportedEncodingException{
        String subject = "Password Reset Request";
        String senderName = "Universal Pet Care";
        String mailContent ="<p>Hi, " + user.getFirstName() + ",</p>" +
                "<p>You have requested to reset your password. Please click the link below to proceed:</p>"+
                "<a href=\"" + resetUrl + "\">Reset Password</a><br>" +
                "<p>If you did not request this, please ignore this email.</p>" +
                "<p>Best Regards.<br> Universal Pet Care</p>";
        emailService.sendEmail(user.getEmail(), senderName, subject, mailContent);
    }
    /*==================================End Password Reset notifications=====================================*/
}
