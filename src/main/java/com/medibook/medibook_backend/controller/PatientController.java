package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.LoginRequest;
import com.medibook.medibook_backend.dto.PatientRegisterRequest;
import com.medibook.medibook_backend.entity.Patient;
import com.medibook.medibook_backend.security.JwtService;
import com.medibook.medibook_backend.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patient")
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;
    private final JwtService jwtService;

    public PatientController(PatientService patientService, JwtService jwtService) {
        this.patientService = patientService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody PatientRegisterRequest request) {
        System.out.println(">>> Register API called with email: " + request.getEmail());
        Patient saved = patientService.registerPatient(request);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginPatient(@RequestBody LoginRequest request) {
        try {
            Patient patient = patientService.login(request.getEmail(), request.getPassword());

            // Check if first login password change is required
            if (patient.isMustChangePassword()) {
                return ResponseEntity.ok(
                        Map.of(
                                "status", "FIRST_LOGIN_PASSWORD_RESET_REQUIRED",
                                "message", "You must change your password.",
                                "mustChangePassword", true,
                                "token", jwtService.generateToken(patient.getEmail(), "PATIENT"),
                                "role", "PATIENT",
                                "patientId", patient.getId()));
            }

            String token = jwtService.generateToken(patient.getEmail(), "PATIENT");
            return ResponseEntity.ok(
                    Map.of(
                            "message", "Login successful",
                            "token", token,
                            "role", "PATIENT",
                            "patientId", patient.getId(),
                            "mustChangePassword", false));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Patient>> getPendingPatients() {
        return ResponseEntity.ok(patientService.getPendingPatients());
    }

    @GetMapping("/approved")
    public ResponseEntity<List<Patient>> getApprovedPatients() {
        return ResponseEntity.ok(patientService.getApprovedPatients());
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<Patient>> getRejectedPatients() {
        return ResponseEntity.ok(patientService.getRejectedPatients());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approvePatient(@PathVariable Long id) {
        try {
            patientService.approvePatient(id);
            return ResponseEntity.ok(Map.of("message", "Patient approved with id = " + id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectPatient(@PathVariable Long id) {
        try {
            patientService.rejectPatient(id);
            return ResponseEntity.ok(Map.of("message", "Patient rejected with id = " + id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
