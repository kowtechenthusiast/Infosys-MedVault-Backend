package com.medibook.medibook_backend.service;
import com.medibook.medibook_backend.dto.*;
import com.medibook.medibook_backend.entity.*;
import com.medibook.medibook_backend.repository.*;
import com.medibook.medibook_backend.security.JwtService;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Service
public class DoctorSlotService {

    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository availabilityRepository;

    public DoctorSlotService(DoctorRepository doctorRepository, DoctorAvailabilityRepository availabilityRepository) {
        this.doctorRepository = doctorRepository;
        this.availabilityRepository = availabilityRepository;
    }

    @Transactional
    public void generateSlots(
            Long doctorId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            int durationMinutes
    ) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Prevent past date slots
        if (date.isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot create slots for past dates");
        }

        LocalTime current = startTime;

        while (current.plusMinutes(durationMinutes).compareTo(endTime) <= 0) {
            DoctorAvailability slot = new DoctorAvailability();
            slot.setDoctor(doctor);
            slot.setSlotDate(date);
            slot.setStartTime(current);
            slot.setEndTime(current.plusMinutes(durationMinutes));
//            slot.setAvailable(true);

            try {
                availabilityRepository.save(slot);
            } catch (Exception ignored) {
                // Duplicate slot ignored due to unique constraint
            }

            current = current.plusMinutes(durationMinutes);
        }
    }

//    public List<DoctorAvailability> getSlots(Long doctorId, LocalDate date) {
//        return availabilityRepository.findByDoctorIdAndSlotDate(doctorId, date);
//    }
}
