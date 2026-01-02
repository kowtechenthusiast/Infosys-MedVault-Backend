package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.CompletePatientProfileRequest;
import com.medibook.medibook_backend.dto.SetPasswordRequest;
import com.medibook.medibook_backend.repository.DoctorAvailabilityRepository;
import com.medibook.medibook_backend.service.*;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
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
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam("userId") Long userId,

            // Personal
            @RequestParam(value = "dateOfBirth", required = false) String dateOfBirth,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "bloodGroup", required = false) String bloodGroup,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "pincode", required = false) String pincode,

            // Lifestyle
            @RequestParam(value = "sleepHours", required = false) String sleepHours,
            @RequestParam(value = "diet", required = false) String diet,
            @RequestParam(value = "smoking", required = false) String smoking,
            @RequestParam(value = "alcohol", required = false) String alcohol,

            // Health
            @RequestParam(value = "sugarLevel", required = false) String sugarLevel,
            @RequestParam(value = "bpSys", required = false) String bpSys,
            @RequestParam(value = "bpDia", required = false) String bpDia,
            @RequestParam(value = "spo2", required = false) String spo2,
            @RequestParam(value = "heartRate", required = false) String heartRate,

            // File
            @RequestPart(value = "idProof", required = false) MultipartFile idProof
    ) {
        try {
            String idProofPath = null;

            /* ================= FILE ================= */
            if (idProof != null && !idProof.isEmpty()) {
                String contentType = idProof.getContentType();
                if (contentType == null ||
                        (!contentType.equals("application/pdf") && !contentType.startsWith("image/"))) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("success", false, "message", "Invalid format. Upload PDF or image."));
                }
                idProofPath = fileStorageService.savePatientIdProof(idProof);
            }

            CompletePatientProfileRequest request = new CompletePatientProfileRequest();
            request.setUserId(userId);

            /* ================= PERSONAL ================= */
            if (dateOfBirth != null && !dateOfBirth.isBlank())
                request.setDateOfBirth(LocalDate.parse(dateOfBirth));

            if (gender != null && !gender.isBlank()) request.setGender(gender);
            if (bloodGroup != null && !bloodGroup.isBlank()) request.setBloodGroup(bloodGroup);
            if (phone != null && !phone.isBlank()) request.setPhone(phone);
            if (address != null && !address.isBlank()) request.setAddress(address);
            if (city != null && !city.isBlank()) request.setCity(city);
            if (state != null && !state.isBlank()) request.setState(state);
            if (country != null && !country.isBlank()) request.setCountry(country);
            if (pincode != null && !pincode.isBlank()) request.setPincode(pincode);

            /* ================= LIFESTYLE ================= */
            if (sleepHours != null && !sleepHours.isBlank())
                request.setSleepHours(Integer.valueOf(sleepHours));

            if (diet != null && !diet.isBlank()) request.setDiet(diet);
            if (smoking != null && !smoking.isBlank()) request.setSmoking(smoking);
            if (alcohol != null && !alcohol.isBlank()) request.setAlcohol(alcohol);

            /* ================= HEALTH ================= */
            if (sugarLevel != null && !sugarLevel.isBlank())
                request.setSugarLevel(Integer.valueOf(sugarLevel));

            if (bpSys != null && !bpSys.isBlank())
                request.setBpSys(Integer.valueOf(bpSys));

            if (bpDia != null && !bpDia.isBlank())
                request.setBpDia(Integer.valueOf(bpDia));

            if (spo2 != null && !spo2.isBlank())
                request.setSpo2(Integer.valueOf(spo2));

            if (heartRate != null && !heartRate.isBlank())
                request.setHeartRate(Integer.valueOf(heartRate));

            /* ================= ID PROOF ================= */
            if (idProofPath != null)
                request.setIdProofPath(idProofPath);

            return ResponseEntity.ok(authService.completePatientProfile(request));

        } catch (Exception e) {
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
//        @PostMapping("/appointments")
//        public ResponseEntity<?> book(
//                @RequestParam Long patientId,
//                @RequestParam Long doctorId,
//                @RequestParam Long slotId
//        ) {
//            appointmentService.bookAppointment(patientId, doctorId, slotId);
//            return ResponseEntity.ok("Appointment requested");
//        }

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
