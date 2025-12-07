package com.medibook.medibook_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String adminEmail;

    public void sendApprovalEmail(String toEmail, String name, String tempPassword) {
        String subject = "MediBook Account Approved";
        String message = "Hello " + name + ",\n\n"
                + "Your MediBook account has been approved by the admin.\n"
                + "Here is your temporary password: " + tempPassword + "\n\n"
                + "Regards,\n"
                + "MediBook Admin";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(adminEmail); // always admin
        mailMessage.setTo(toEmail); // patient/doctor email
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }

    public void sendPasswordChangeConfirmationEmail(String toEmail) {
        String subject = "Password Reset";
        String message = "Hello " + toEmail + ",\n\n" +
                "Your password has been successfully reset.\n" +
                "If you did not perform this action, please contact our support immediately.\n\n" +
                "Best Regards,\n" +
                "MediBook Team";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(adminEmail);
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }
}
