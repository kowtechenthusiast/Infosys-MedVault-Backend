package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.CompleteDoctorProfileRequest;
import com.medibook.medibook_backend.service.AppointmentService;
import com.medibook.medibook_backend.service.AuthService;
import com.medibook.medibook_backend.service.DoctorSlotService;
import com.medibook.medibook_backend.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile/doctor")
public class DoctorController {

    private final AuthService authService;
    private final FileStorageService fileStorageService;
    private final DoctorSlotService slotService;
    private final AppointmentService appointmentService;

    public DoctorController(AuthService authService, FileStorageService fileStorageService, DoctorSlotService slotService, AppointmentService appointmentService) {
        this.authService = authService;
        this.fileStorageService = fileStorageService;
        this.slotService = slotService;
        this.appointmentService = appointmentService;
    }


        /**
         * PUT /api/profile/doctor
         * Update doctor profile details
         */
        @PutMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<Map<String, Object>> updateProfile(
                @RequestParam("userId") Long userId,
                @RequestParam("dateOfBirth") String dateOfBirth,
                @RequestParam("gender") String gender,
                @RequestParam("medicalRegistrationNumber") String medicalRegistrationNumber,
                @RequestParam("licensingAuthority") String licensingAuthority,
                @RequestParam("specialization") String specialization,
                @RequestParam("qualification") String qualification,
                @RequestParam("experience") Integer experience,
                @RequestParam("phone") String phone,
                @RequestParam("clinicHospitalName") String clinicHospitalName,
                @RequestParam("city") String city,
                @RequestParam("state") String state,
                @RequestParam("country") String country,
                @RequestParam("pincode") String pincode,
                @RequestParam("consultationFee") Integer consultationFee,
                @RequestPart(value = "medicalLicense", required = false) MultipartFile medicalLicense
        ) {

            try {
                String medicalLicensePath = null;

                if (medicalLicense != null && !medicalLicense.isEmpty()) {
                    String contentType = medicalLicense.getContentType();
                    if (contentType == null ||
                            (!contentType.equals("application/pdf") && !contentType.startsWith("image/"))) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("success", false,
                                        "message", "Invalid medical license format. Upload PDF or image."));
                    }

                    medicalLicensePath = fileStorageService.saveDoctorCertificate(medicalLicense);
                }

                CompleteDoctorProfileRequest request = new CompleteDoctorProfileRequest();
                request.setUserId(userId);
                request.setDateOfBirth(LocalDate.parse(dateOfBirth));
                request.setGender(gender);
                request.setMedicalRegistrationNumber(medicalRegistrationNumber);
                request.setLicensingAuthority(licensingAuthority);
                request.setSpecialization(specialization);
                request.setQualification(qualification);
                request.setExperience(experience);
                request.setPhone(phone);
                request.setClinicHospitalName(clinicHospitalName);
                request.setCity(city);
                request.setState(state);
                request.setCountry(country);
                request.setPincode(pincode);
                request.setConsultationFee(consultationFee);
                request.setMedicalLicensePath(medicalLicensePath);

                Map<String, Object> response = authService.completeDoctorProfile(request);
                return ResponseEntity.ok(response);

            } catch (Exception e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", e.getMessage()));
            }
        }
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getDoctorProfile(@PathVariable Long userId) {

        System.out.println("---- Called GET /doctor/" + userId);

        try {
            Map<String, Object> response = authService.getDoctorProfile(userId);
            System.out.println("Response: " + response);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.out.println("ERROR in getDoctorProfile: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/getPending")
    public ResponseEntity<?> getAllPendingDoctors() {

        try {
            List<Map<String, Object>> result = authService.getPendingDoctors();

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "count", result.size(),
                            "doctors", result
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/getDoctors")
    public ResponseEntity<?> getAllDoctors() {

        try {
            List<Map<String, Object>> result = authService.geDoctorsList();

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "count", result.size(),
                            "doctors", result
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }


    // -------------------------------
    // CREATE SLOTS
    // -------------------------------
    @PostMapping("/slots")
    public ResponseEntity<?> createSlots(
            @RequestParam LocalDate date,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            @RequestParam int durationMinutes,
            @RequestParam Long doctorId // OR extract from JWT
    ) {
        slotService.generateSlots(
                doctorId, date, startTime, endTime, durationMinutes);

        return ResponseEntity.ok("Slots created");
    }

    // -------------------------------
    // VIEW SLOTS
    // -------------------------------
//    @GetMapping("/slots")
//    public ResponseEntity<?> viewSlots(
//            @RequestParam Long doctorId,
//            @RequestParam LocalDate date
//    ) {
//        return ResponseEntity.ok(slotService.getSlots(doctorId, date));
//    }

    // -------------------------------
    // VIEW APPOINTMENTS
    // -------------------------------
//    @GetMapping("/appointments")
//    public ResponseEntity<?> appointments(@RequestParam Long doctorId) {
//        return ResponseEntity.ok(
//                appointmentService.getDoctorAppointments(doctorId));
//    }

    // -------------------------------
    // CONFIRM
    // -------------------------------
//    @PostMapping("/appointments/{id}/confirm")
//    public ResponseEntity<?> confirm(
//            @PathVariable Long id,
//            @RequestParam Long doctorId
//    ) {
//        appointmentService.confirmAppointment(doctorId, id);
//        return ResponseEntity.ok("Appointment confirmed");
//    }
//
//    // -------------------------------
//    // REJECT
//    // -------------------------------
//    @PostMapping("/appointments/{id}/reject")
//    public ResponseEntity<?> reject(
//            @PathVariable Long id,
//            @RequestParam Long doctorId
//    ) {
//        appointmentService.rejectAppointment(doctorId, id);
//        return ResponseEntity.ok("Appointment rejected");
//    }
}
