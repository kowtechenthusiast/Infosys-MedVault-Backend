package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.CompleteDoctorProfileRequest;
import com.medibook.medibook_backend.entity.DoctorRating;
import com.medibook.medibook_backend.repository.DoctorRatingRepository;
import com.medibook.medibook_backend.service.*;
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
    private final DoctorRatingRepository ratingRepo;
    private final DoctorService ratingService;


    public DoctorController(AuthService authService, FileStorageService fileStorageService, DoctorSlotService slotService, AppointmentService appointmentService, DoctorRatingRepository ratingRepo, DoctorService ratingService) {
        this.authService = authService;
        this.fileStorageService = fileStorageService;
        this.slotService = slotService;
        this.appointmentService = appointmentService;
        this.ratingRepo = ratingRepo;
        this.ratingService = ratingService;
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

            System.out.println("🔹 updateProfile API called");
            System.out.println("User ID: " + userId);

            try {
                String medicalLicensePath = null;

                if (medicalLicense != null && !medicalLicense.isEmpty()) {
                    System.out.println("Medical license received: " + medicalLicense.getOriginalFilename());
                    System.out.println("Content type: " + medicalLicense.getContentType());

                    String contentType = medicalLicense.getContentType();
                    if (contentType == null ||
                            (!contentType.equals("application/pdf") && !contentType.startsWith("image/"))) {

                        System.out.println("❌ Invalid medical license format");

                        return ResponseEntity.badRequest()
                                .body(Map.of(
                                        "success", false,
                                        "message", "Invalid medical license format. Upload PDF or image."
                                ));
                    }

                    medicalLicensePath = fileStorageService.saveDoctorCertificate(medicalLicense);
                    System.out.println("✅ Medical license saved at: " + medicalLicensePath);
                } else {
                    System.out.println("ℹ️ No medical license uploaded");
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

                System.out.println("📤 Sending profile completion request to service");

                Map<String, Object> response = authService.completeDoctorProfile(request);

                System.out.println("✅ Doctor profile updated successfully");

                return ResponseEntity.ok(response);

            } catch (Exception e) {
                System.out.println("❌ Error in updateProfile: " + e.getMessage());
                e.printStackTrace();

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", e.getMessage()
                        ));
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

    @GetMapping("/doctor/{doctorId}/my-rating")
    public Integer getMyRating(
            @PathVariable Long doctorId,
            @RequestParam Long patientId
    ) {
        return ratingRepo
                .findByDoctor_IdAndPatient_Id(doctorId, patientId)
                .map(DoctorRating::getRating)
                .orElse(null);
    }



        /* ======================================================
           RATE A DOCTOR (ONCE)
           ====================================================== */
    @PostMapping("/patient/doctor-rating")
    public void rateDoctor(@RequestBody Map<String, Object> body) {

        Long doctorId = Long.valueOf(body.get("doctorId").toString());
        Long patientId = Long.valueOf(body.get("patientId").toString());
        Integer rating = Integer.valueOf(body.get("rating").toString());

        ratingService.rateDoctor(doctorId, patientId, rating);
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
