package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.*;
import com.medibook.medibook_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /auth/generate-otp
     * Generate OTP for registration (ADMIN, PATIENT, DOCTOR)
     */
    @PostMapping("/generate-otp")
    public ResponseEntity<Map<String, Object>> generateOtp(@Valid @RequestBody GenerateOtpRequest request) {
        try {
            Map<String, Object> response = authService.generateOtp(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Email already in use")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("success", false, "message", e.getMessage()));
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }


    /**
     * POST /auth/register-admin
     * Register new Admin (Directly)
     */
    @PostMapping("/register-admin")
    public ResponseEntity<Map<String, Object>> registerAdmin(@Valid @RequestBody AdminRegisterRequest request) {
        try {
            Map<String, Object> response = authService.registerAdmin(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Email already in use")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("success", false, "message", e.getMessage()));
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * POST /auth/verify-otp
     * Verify OTP
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        try {
            Map<String, Object> response = authService.verifyOtp(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * POST /auth/login
     * Login for all roles
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {

        System.out.println("➡️ /login API called");
        System.out.println("Email received: " + request.getEmail());

        try {
            Map<String, Object> response = authService.login(request);

            System.out.println("✅ Login API success for email: " + request.getEmail());
            System.out.println("Role: " + response.get("role"));
            System.out.println("User ID: " + response.get("userId"));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            System.out.println("❌ Login API failed for email: " + request.getEmail());
            System.out.println("Error message: " + e.getMessage());

            if (e.getMessage() != null && e.getMessage().contains("not verified")) {
                System.out.println("⛔ Account not verified");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "success", false,
                                "message", e.getMessage()
                        ));
            }

            System.out.println("⛔ Unauthorized login attempt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }


    /**
     * PUT /profile/set-password
     * set password
     *
     */
    @PutMapping("/set-password")
    public ResponseEntity<Map<String, Object>> setPassword(
            @Valid @RequestBody SetPasswordRequest request) {
        try {
            return ResponseEntity.ok(authService.setPassword(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
