package com.medibook.medibook_backend.service;

import com.medibook.medibook_backend.dto.*;
import com.medibook.medibook_backend.entity.*;
import com.medibook.medibook_backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class DoctorScheduleService {

    private final DoctorAvailabilityRepository slotRepo;
    private final DoctorDayOffRepository dayOffRepo;
    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;

    public DoctorScheduleService(
            DoctorAvailabilityRepository slotRepo,
            DoctorDayOffRepository dayOffRepo,
            AppointmentRepository appointmentRepo,
            DoctorRepository doctorRepo
    ) {
        this.slotRepo = slotRepo;
        this.dayOffRepo = dayOffRepo;
        this.appointmentRepo = appointmentRepo;
        this.doctorRepo = doctorRepo;
    }

    /* ================= GET SCHEDULE ================= */
    @Transactional(readOnly = true)
    public DoctorScheduleResponse getSchedule(Long doctorId, LocalDate date) {

        List<DoctorAvailability> slots =
                slotRepo.findByDoctor_IdAndSlotDateOrderByStartTime(doctorId, date);

        List<SlotResponse> slotResponses = slots.stream()
                .map(s -> new SlotResponse(
                        s.getId(),
                        s.getSlotDate(),
                        s.getStartTime(),
                        s.getEndTime(),
                        s.getDurationMinutes(),
                        s.getStatus()
                ))
                .toList();

        boolean dayBlocked = slotResponses.stream()
                .noneMatch(s -> s.getStatus() == DoctorAvailability.SlotStatus.OPEN);

        return new DoctorScheduleResponse(
                doctorId,
                date,
                dayBlocked,
                slotResponses
        );
    }




    /* ================= GENERATE SLOTS ================= */
    @Transactional
    public void generateSlots(GenerateScheduleRequest req) {

        // 🚫 Do not generate slots on blocked day
        if (dayOffRepo.existsByDoctor_IdAndOffDate(req.doctorId, req.date)) {
            throw new IllegalStateException("Cannot generate slots on a blocked day");
        }

        Doctor doctor = doctorRepo.findById(req.doctorId).orElseThrow();

        // ✅ DB-level delete
        slotRepo.deleteOpenSlots(req.doctorId, req.date);
        slotRepo.flush();

        LocalTime time = req.startTime;

        while (time.plusMinutes(req.durationMinutes).compareTo(req.endTime) <= 0) {

            DoctorAvailability slot = new DoctorAvailability();
            slot.setDoctor(doctor);
            slot.setSlotDate(req.date);
            slot.setStartTime(time);
            slot.setEndTime(time.plusMinutes(req.durationMinutes));
            slot.setDurationMinutes(req.durationMinutes);
            slot.setStatus(DoctorAvailability.SlotStatus.OPEN);

            slotRepo.save(slot);
            time = time.plusMinutes(req.durationMinutes);
        }
    }


    /* ================= BLOCK DAY ================= */
    @Transactional
    public void blockDay(Long doctorId, LocalDate date) {

        Doctor doctor = doctorRepo.findById(doctorId).orElseThrow();

        // ✅ Prevent duplicate day-off insert
        if (!dayOffRepo.existsByDoctor_IdAndOffDate(doctorId, date)) {
            dayOffRepo.save(new DoctorDayOff(doctor, date));
        }

        slotRepo.findByDoctor_IdAndSlotDateOrderByStartTime(doctorId, date)
                .stream()
                .filter(s -> s.getStatus() == DoctorAvailability.SlotStatus.OPEN)
                .forEach(s -> s.setStatus(DoctorAvailability.SlotStatus.BLOCKED));
    }

    /* ================= UNBLOCK DAY ================= */
    @Transactional
    public void unblockDay(Long doctorId, LocalDate date) {

        dayOffRepo.deleteByDoctor_IdAndOffDate(doctorId, date);

        slotRepo.findByDoctor_IdAndSlotDateOrderByStartTime(doctorId, date)
                .stream()
                .filter(s -> s.getStatus() == DoctorAvailability.SlotStatus.BLOCKED)
                .forEach(s -> s.setStatus(DoctorAvailability.SlotStatus.OPEN));
    }

}
