package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.AppointmentResponseDTO;
import com.medibook.medibook_backend.dto.BookingRequest;
import com.medibook.medibook_backend.dto.UpcomingAppointmentDTO;
import com.medibook.medibook_backend.entity.Appointment;
import com.medibook.medibook_backend.entity.DoctorAvailability;
import com.medibook.medibook_backend.repository.AppointmentRepository;
import com.medibook.medibook_backend.service.AppointmentService;
import com.medibook.medibook_backend.repository.DoctorAvailabilityRepository;
import com.medibook.medibook_backend.repository.DoctorDayOffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patient/booking")
public class AppointmentBookingController {


    private final AppointmentService appointmentService;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorDayOffRepository doctorDayOffRepository;
    private final AppointmentRepository appointmentRepository;

    public AppointmentBookingController(AppointmentService appointmentService, DoctorAvailabilityRepository availabilityRepository, DoctorDayOffRepository doctorDayOffRepository, AppointmentRepository appointmentRepository) {
        this.appointmentService = appointmentService;
        this.availabilityRepository = availabilityRepository;
        this.doctorDayOffRepository = doctorDayOffRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /* ======================================================
       GET AVAILABLE SLOTS FOR A DOCTOR ON A DATE
       ====================================================== */
    @GetMapping("/slots")
    public List<DoctorAvailability> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam String date
    ) {
        LocalDate slotDate = LocalDate.parse(date);

        // If doctor has blocked the entire day → no slots
        if (doctorDayOffRepository
                .findByDoctor_IdAndDate(doctorId, slotDate)
                .isPresent()) {
            return List.of();
        }

        return availabilityRepository
                .findByDoctor_IdAndSlotDateAndStatusOrderByStartTime(
                        doctorId,
                        slotDate,
                        DoctorAvailability.SlotStatus.OPEN
                );
    }

    @GetMapping("/appointment/{id}")
    public Appointment getAppointmentDetails(@PathVariable Long id) {
        return appointmentRepository.findWithPatientById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }



    /* ======================================================
       BOOK APPOINTMENT (PATIENT)
       ====================================================== */
    @PostMapping
    public Appointment bookAppointment(@RequestBody BookingRequest request) {

        // Now these checks are valid and necessary
        if (request.getPatientId() == null ||
                request.getDoctorId() == null ||
                request.getSlotId() == null) {
            throw new RuntimeException("Missing booking details: IDs cannot be null");
        }

        return appointmentService.bookAppointment(
                request.getPatientId(),
                request.getDoctorId(),
                request.getSlotId(),
                request.getReason() // This is now correctly handled as a String
        );
    }

    @GetMapping("/upcoming")
    public List<UpcomingAppointmentDTO> getUpcomingAppointments(
            @RequestParam("userId") Long userId
    ) {
        System.out.println("[Controller] /upcoming called");
        System.out.println("[Controller] userId = " + userId);

        List<UpcomingAppointmentDTO> result =
                appointmentService.getUpcomingAppointments(userId);

        System.out.println("[Controller] upcoming appointments count = " + result.size());
        return result;
    }



    @GetMapping("/getAllAppointment")
    public List<AppointmentResponseDTO> getAllAppointment(
            @RequestParam("userId") Long userId
    ) {
        return appointmentService.getDoctorAppointments(userId);
    }

    @GetMapping("/getPendingAppointment")
    public List<AppointmentResponseDTO> getPendingAppointment(
            @RequestParam("userId") Long userId
    ) {
        return appointmentService.getPendingDoctorAppointments(userId);
    }



    @PutMapping("/{appointmentId}/reschedule")
    public void rescheduleAppointment(
            @PathVariable Long appointmentId,
            @RequestBody Map<String, Object> body
    ) {
        Long slotId = Long.valueOf(body.get("slotId").toString());
        LocalDate newDate = LocalDate.parse(body.get("newDate").toString());
        Long userId = Long.valueOf(body.get("userId").toString()); // ✅ FIX

        appointmentService.rescheduleAppointment(
                appointmentId,
                userId,
                slotId,
                newDate
        );
    }
    @PutMapping("/approve/{appointmentId}")
    public void approveAppointment(
            @PathVariable Long appointmentId,
            @RequestParam Long doctorId
    ) {
        appointmentService.approveAppointment(doctorId, appointmentId);
    }

    /* ================= REJECT ================= */
    @PutMapping("/reject/{appointmentId}")
    public void rejectAppointment(
            @PathVariable Long appointmentId,
            @RequestParam Long doctorId,
            @RequestBody(required = false) Map<String, String> body
    ) {
        appointmentService.rejectAppointment(
                doctorId,
                appointmentId,
                body != null ? body.get("reason") : null
        );
    }
}
