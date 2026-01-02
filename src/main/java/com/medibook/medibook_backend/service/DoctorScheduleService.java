
package com.medibook.medibook_backend.service;

import com.medibook.medibook_backend.entity.*;
import com.medibook.medibook_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;

@Service
public class DoctorScheduleService {

    private final DoctorRepository doctorRepo;
    private final DoctorAvailabilityRepository slotRepo;
    private final DoctorDayOffRepository dayOffRepo;

    public DoctorScheduleService(DoctorRepository doctorRepo, DoctorAvailabilityRepository slotRepo, DoctorDayOffRepository dayOffRepo) {
        this.doctorRepo = doctorRepo;
        this.slotRepo = slotRepo;
        this.dayOffRepo = dayOffRepo;
    }

    @Transactional
    public void generateSlots(Long doctorId, LocalDate date,
                              LocalTime start, LocalTime end, int duration) {

        if (dayOffRepo.findByDoctor_IdAndDate(doctorId, date).isPresent())
            throw new RuntimeException("Day is blocked");

        Doctor doctor = doctorRepo.findById(doctorId).orElseThrow();

        LocalTime t = start;
        while (t.plusMinutes(duration).compareTo(end) <= 0) {

            LocalTime slotEnd = t.plusMinutes(duration);

            boolean overlap = slotRepo
                    .existsByDoctor_IdAndSlotDateAndStartTimeLessThanAndEndTimeGreaterThan(
                            doctorId, date, slotEnd, t);

            if (!overlap) {
                DoctorAvailability slot = new DoctorAvailability();
                slot.setDoctor(doctor);
                slot.setSlotDate(date);
                slot.setStartTime(t);
                slot.setEndTime(slotEnd);
                slotRepo.save(slot);
            }

            t = slotEnd;
        }
    }

    @Transactional
    public void blockDay(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepo.findById(doctorId).orElseThrow();
        dayOffRepo.save(new DoctorDayOff(doctor, date));

        slotRepo.findByDoctor_IdAndSlotDateOrderByStartTime(doctorId, date)
                .stream()
                .filter(s -> s.getStatus() == DoctorAvailability.SlotStatus.OPEN)
                .forEach(s -> s.setStatus(DoctorAvailability.SlotStatus.BLOCKED));
    }

    @Transactional
    public void unblockDay(Long doctorId, LocalDate date) {
        dayOffRepo.findByDoctor_IdAndDate(doctorId, date)
                .ifPresent(dayOffRepo::delete);

        slotRepo.findByDoctor_IdAndSlotDateOrderByStartTime(doctorId, date)
                .stream()
                .filter(s -> s.getStatus() == DoctorAvailability.SlotStatus.BLOCKED)
                .forEach(s -> s.setStatus(DoctorAvailability.SlotStatus.OPEN));
    }
}
