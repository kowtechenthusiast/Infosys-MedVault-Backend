package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.ChangePasswordRequest;
import com.medibook.medibook_backend.service.DoctorService;
import com.medibook.medibook_backend.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final PatientService patientService;
    private final DoctorService doctorService;

    public AuthController(PatientService patientService, DoctorService doctorService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @PostMapping("/change-password-first-login")
    public ResponseEntity<?> changePasswordFirstLogin(@RequestBody ChangePasswordRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String role = auth.getAuthorities().stream().findFirst().orElseThrow().getAuthority();

        try {
            if ("ROLE_PATIENT".equals(role)) {
                patientService.changePasswordFirstLogin(email, request.getCurrentPassword(), request.getNewPassword(),
                        request.getConfirmPassword());
            } else if ("ROLE_DOCTOR".equals(role)) {
                doctorService.changePasswordFirstLogin(email, request.getCurrentPassword(), request.getNewPassword(),
                        request.getConfirmPassword());
            } else {
                return ResponseEntity.status(403).body(java.util.Map.of("error", "Invalid role for this operation."));
            }
            return ResponseEntity.ok(java.util.Map.of("message", "Password updated successfully."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
