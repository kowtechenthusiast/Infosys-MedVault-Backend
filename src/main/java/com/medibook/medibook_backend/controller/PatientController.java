package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.CompletePatientProfileRequest;
import com.medibook.medibook_backend.dto.SetPasswordRequest;
import com.medibook.medibook_backend.service.AuthService;
import com.medibook.medibook_backend.service.FileStorageService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/profile/patient")
public class PatientController {

    private final AuthService authService;
    private final FileStorageService fileStorageService;

    public PatientController(AuthService authService, FileStorageService fileStorageService) {
        this.authService = authService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * PUT /profile/patient
     * Complete patient profile after OTP verification with file upload
     */
    @PutMapping(
            value = "/complete-profile",
            consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
            produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestBody CompletePatientProfileRequest request) {

        try {
            // Call service to update selected fields
            Map<String, Object> response = authService.completePatientProfile(request);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }





    /**
     * GET /profile/{userId}
     * get user
     *
     */


    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getPatientProfile(@PathVariable Long userId) {

        System.out.println("---- Called GET /patient/" + userId);

        try {
            Map<String, Object> response = authService.getPatientProfile(userId);
            System.out.println("Response: " + response);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.out.println("ERROR in getPatientProfile: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }



}
