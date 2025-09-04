package com.dailycodework.universalpetcare.email;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;
import java.util.EnumMap;
import java.util.Properties;

@Configuration
public class EmailService {
    private JavaMailSender javaMailSender;

    @PostConstruct
    private void init (){
        javaMailSender = createMailSender();
    }

    public void sendEmail(String receiverName, String senderName, String subject, String mailContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(mimeMessage);
        messageHelper.setFrom(EmailProperties.DEFAULT_USERNAME, senderName);
        messageHelper.setTo(receiverName);
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(mimeMessage);
    }

    private JavaMailSender createMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(EmailProperties.DEFAULT_HOST);
        mailSender.setPort(EmailProperties.DEFAULT_PORT);
        mailSender.setUsername(EmailProperties.DEFAULT_USERNAME);
        mailSender.setPassword(EmailProperties.DEFAULT_PASSWORD);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", EmailProperties.DEFAULT_AUTH);
        props.put("mail.smtp.starttls.enable", EmailProperties.DEFAULT_STARTTLS);
        return mailSender;
    }
}
