package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.CompletePatientProfileRequest;
import com.medibook.medibook_backend.dto.SetPasswordRequest;
import com.medibook.medibook_backend.repository.DoctorAvailabilityRepository;
import com.medibook.medibook_backend.service.*;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile/patient")
public class PatientController {

    private final AuthService authService;
    private final FileStorageService fileStorageService;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final AppointmentService appointmentService;

    public PatientController(AuthService authService, FileStorageService fileStorageService, DoctorAvailabilityRepository availabilityRepository, AppointmentService appointmentService) {
        this.authService = authService;
        this.fileStorageService = fileStorageService;
        this.availabilityRepository = availabilityRepository;
        this.appointmentService = appointmentService;
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

    @GetMapping("/getPending")
    public ResponseEntity<?> getAllPendingPatients() {

        try {
            List<Map<String, Object>> result = authService.getPendingPatients();

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "count", result.size(),
                            "patients", result
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

        // -------------------------------
        // VIEW AVAILABLE SLOTS
        // -------------------------------
//        @GetMapping("/doctors/{doctorId}/slots")
//        public ResponseEntity<?> availableSlots(
//                @PathVariable Long doctorId,
//                @RequestParam LocalDate date
//        ) {
//            return ResponseEntity.ok(
//                    availabilityRepository
//                            .findByDoctor_IdAndSlotDateAndAvailableTrue(doctorId, date)
//            );
//        }

        // -------------------------------
        // BOOK APPOINTMENT
        // -------------------------------
        @PostMapping("/appointments")
        public ResponseEntity<?> book(
                @RequestParam Long patientId,
                @RequestParam Long doctorId,
                @RequestParam Long slotId
        ) {
            appointmentService.bookAppointment(patientId, doctorId, slotId);
            return ResponseEntity.ok("Appointment requested");
        }

        // -------------------------------
        // VIEW MY APPOINTMENTS
        // -------------------------------
        @GetMapping("/appointments")
        public ResponseEntity<?> myAppointments(
                @RequestParam Long patientId
        ) {
            return ResponseEntity.ok(
                    appointmentService.getPatientAppointments(patientId)
            );
        }
}
