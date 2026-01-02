package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.DoctorSlotDTO;
import com.medibook.medibook_backend.entity.DoctorAvailability;
import com.medibook.medibook_backend.repository.DoctorAvailabilityRepository;
import com.medibook.medibook_backend.repository.DoctorDayOffRepository;
import com.medibook.medibook_backend.service.DoctorScheduleService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/doctor/schedule")
public class DoctorScheduleController {

    private final DoctorScheduleService service;
    private final DoctorAvailabilityRepository slotRepo;
    private final DoctorDayOffRepository dayOffRepo;

    public DoctorScheduleController(
            DoctorScheduleService service,
            DoctorAvailabilityRepository slotRepo,
            DoctorDayOffRepository dayOffRepo
    ) {
        this.service = service;
        this.slotRepo = slotRepo;
        this.dayOffRepo = dayOffRepo;
    }

    /* ================= GET DAY SCHEDULE ================= */
    @GetMapping
    public Map<String, Object> getSchedule(
            @RequestParam Long doctorId,
            @RequestParam String date
    ) {
        LocalDate d = LocalDate.parse(date);

        List<DoctorSlotDTO> slots = slotRepo
                .findByDoctor_IdAndSlotDateOrderByStartTime(doctorId, d)
                .stream()
                .map(slot -> new DoctorSlotDTO(
                        slot.getId(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        slot.getStatus(),
                        slot.getAppointment() != null
                                ? slot.getAppointment().getId()
                                : null
                ))
                .toList();

        return Map.of(
                "slots", slots,
                "dayBlocked",
                dayOffRepo.findByDoctor_IdAndDate(doctorId, d).isPresent()
        );
    }


    /* ================= GENERATE SLOTS ================= */
    @PostMapping("/generate")
    public void generate(@RequestBody Map<String, String> body) {
        service.generateSlots(
                Long.parseLong(body.get("doctorId")),
                LocalDate.parse(body.get("date")),
                LocalTime.parse(body.get("startTime")),
                LocalTime.parse(body.get("endTime")),
                Integer.parseInt(body.get("durationMinutes"))
        );
    }

    /* ================= BLOCK DAY ================= */
    @PutMapping("/block-day")
    public void blockDay(@RequestBody Map<String, String> body) {
        service.blockDay(
                Long.parseLong(body.get("doctorId")),
                LocalDate.parse(body.get("date"))
        );
    }

    /* ================= UNBLOCK DAY ================= */
    @PutMapping("/unblock-day")
    public void unblockDay(@RequestBody Map<String, String> body) {
        service.unblockDay(
                Long.parseLong(body.get("doctorId")),
                LocalDate.parse(body.get("date"))
        );
    }

    /* ================= BLOCK / UNBLOCK SLOT ================= */
    @PutMapping("/slot/{slotId}/toggle")
    public void toggleSlot(@PathVariable Long slotId) {

        DoctorAvailability slot = slotRepo.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (slot.getStatus() == DoctorAvailability.SlotStatus.BOOKED) {
            throw new RuntimeException("Booked slot cannot be blocked");
        }

        slot.setStatus(
                slot.getStatus() == DoctorAvailability.SlotStatus.BLOCKED
                        ? DoctorAvailability.SlotStatus.OPEN
                        : DoctorAvailability.SlotStatus.BLOCKED
        );

        slotRepo.save(slot);
    }
}
