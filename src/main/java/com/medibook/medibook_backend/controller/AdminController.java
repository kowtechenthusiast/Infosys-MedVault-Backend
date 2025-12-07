package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.AdminLoginRequest;
import com.medibook.medibook_backend.entity.AdminOtp;
import com.medibook.medibook_backend.repository.AdminOtpRepository;
import com.medibook.medibook_backend.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final JavaMailSender mailSender;
    private final AdminOtpRepository adminOtpRepository;
    private final JwtService jwtService;

    @Value("${medibook.admin.email}")
    private String adminEmail;

    @Value("${medibook.admin.password}")
    private String adminPassword;

    public AdminController(JavaMailSender mailSender,
            AdminOtpRepository adminOtpRepository,
            JwtService jwtService) {
        this.mailSender = mailSender;
        this.adminOtpRepository = adminOtpRepository;
        this.jwtService = jwtService;
    }

    // ===================== STEP 1: EMAIL + PASSWORD =====================

    /**
     * Called from FIRST screen:
     * Admin Email + Password + [Continue]
     *
     * POST /api/admin/login
     * Body: { "email": "...", "password": "..." }
     */
    @PostMapping("/login")
    public ResponseEntity<?> startAdminLogin(@RequestBody AdminLoginRequest request) {

        // 1) Validate credentials (simple: from application.properties)
        if (!adminEmail.equalsIgnoreCase(request.getEmail())
                || !adminPassword.equals(request.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid admin email or password"));
        }

        // 2) If valid -> generate & send OTP
        generateAndSendOtp();
        return ResponseEntity.ok(Map.of("message", "OTP sent to admin email"));
    }

    // ===================== STEP 2: VERIFY OTP =====================

    /**
     * Called from SECOND screen (5-digit OTP).
     * Example: POST /api/admin/verify-otp?otp=12345
     */
    @PostMapping("/verify-otp")
    @Transactional
    public ResponseEntity<?> verifyOtp(@RequestParam String otp) {

        AdminOtp adminOtp = adminOtpRepository
                .findTopByOtpCodeAndUsedIsFalseOrderByCreatedAtDesc(otp)
                .orElse(null);

        if (adminOtp == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid or already used OTP"));
        }

        // mark as used
        adminOtp.setUsed(true);
        adminOtpRepository.save(adminOtp);

        // issue JWT for admin
        String token = jwtService.generateToken(adminEmail, "ADMIN");

        return ResponseEntity.ok(
                Map.of(
                        "message", "Admin login successful",
                        "token", token,
                        "role", "ADMIN"));
    }

    // ===================== (OPTIONAL) RAW SEND-OTP =====================
    // You can still keep this for Postman testing if you want,
    // but your frontend should call /login first, not this directly.

    @PostMapping("/send-otp")
    @Transactional
    public ResponseEntity<?> sendOtpDirect() {
        generateAndSendOtp();
        return ResponseEntity.ok(Map.of("message", "OTP sent to admin email"));
    }

    // ===================== HELPER =====================

    private void generateAndSendOtp() {

        // create random 5-digit code
        Random random = new Random();
        int code = 10000 + random.nextInt(90000);
        String otpCode = String.valueOf(code);

        // save OTP in DB
        AdminOtp otp = new AdminOtp();
        otp.setOtpCode(otpCode);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setUsed(false);
        adminOtpRepository.save(otp);

        // email it
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("chmounyasri@gmail.com");
        message.setSubject("Your MediBook Admin OTP");
        message.setText("Your 5-digit admin OTP is: " + otpCode);
        mailSender.send(message);
    }
}
