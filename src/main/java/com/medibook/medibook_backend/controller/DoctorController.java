package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.LoginRequest;
import com.medibook.medibook_backend.entity.Doctor;
import com.medibook.medibook_backend.security.JwtService;
import com.medibook.medibook_backend.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/doctor")
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService doctorService;
    private final JwtService jwtService;

    public DoctorController(DoctorService doctorService, JwtService jwtService) {
        this.doctorService = doctorService;
        this.jwtService = jwtService;
    }

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<?> registerDoctor(@RequestParam String fullName,
            @RequestParam String gender,
            @RequestParam Integer yearsOfExperience,
            @RequestParam String specialization,
            @RequestParam String contactPhone,
            @RequestParam String email,
            @RequestPart("licenseFile") MultipartFile licenseFile) {
        try {
            Doctor doctor = new Doctor();
            doctor.setFullName(fullName);
            doctor.setGender(gender);
            doctor.setYearsOfExperience(yearsOfExperience);
            doctor.setSpecialization(specialization);
            doctor.setContactPhone(contactPhone);
            doctor.setEmail(email);

            // Password is NOT set here; it is handled in Service with a random hash
            Doctor saved = doctorService.registerDoctorWithFile(doctor, licenseFile);

            return ResponseEntity.ok(Map.of(
                    "message", "Doctor registered. Wait for admin approval.",
                    "id", saved.getId()));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "File upload failed: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error while registering doctor: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginDoctor(@RequestBody LoginRequest request) {
        try {
            Doctor doctor = doctorService.login(request.getEmail(), request.getPassword());

            if (doctor.isMustChangePassword()) {
                return ResponseEntity.ok(
                        Map.of(
                                "status", "FIRST_LOGIN_PASSWORD_RESET_REQUIRED",
                                "message", "You must change your password.",
                                "mustChangePassword", true,
                                "token", jwtService.generateToken(doctor.getEmail(), "DOCTOR"),
                                "role", "DOCTOR",
                                "doctorId", doctor.getId()));
            }

            String token = jwtService.generateToken(doctor.getEmail(), "DOCTOR");
            return ResponseEntity.ok(
                    Map.of("message", "Login successful",
                            "token", token,
                            "role", "DOCTOR",
                            "doctorId", doctor.getId(),
                            "status", doctor.getStatus(),
                            "mustChangePassword", false));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Doctor>> getPendingDoctors() {
        return ResponseEntity.ok(doctorService.getPendingDoctors());
    }

    @GetMapping("/approved")
    public ResponseEntity<List<Doctor>> getApprovedDoctors() {
        return ResponseEntity.ok(doctorService.getApprovedDoctors());
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<Doctor>> getRejectedDoctors() {
        return ResponseEntity.ok(doctorService.getRejectedDoctors());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveDoctor(@PathVariable Long id) {
        try {
            doctorService.approveDoctor(id);
            return ResponseEntity.ok(Map.of("message", "Doctor approved with id = " + id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectDoctor(@PathVariable Long id) {
        try {
            doctorService.rejectDoctor(id);
            return ResponseEntity.ok(Map.of("message", "Doctor rejected with id = " + id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
