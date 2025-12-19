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
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, DoctorAvailabilityRepository availabilityRepository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.availabilityRepository = availabilityRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // -------------------------------
    // PATIENT BOOKING
    // -------------------------------
    @Transactional
    public void bookAppointment(Long patientId, Long doctorId, Long slotId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // 🔐 Lock slot to prevent double booking
        DoctorAvailability slot = availabilityRepository
                .findByIdForUpdate(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        // Check slot status
        if (slot.getStatus() != DoctorAvailability.SlotStatus.OPEN) {
            throw new RuntimeException("Slot already booked or blocked");
        }

        // Mark slot as booked
        slot.setStatus(DoctorAvailability.SlotStatus.BOOKED);

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSlot(slot);

        // Snapshot (VERY IMPORTANT)
        appointment.setAppointmentDate(slot.getSlotDate());
        appointment.setAppointmentTime(slot.getStartTime());

        appointment.setStatus(Appointment.Status.REQUESTED);

        appointmentRepository.save(appointment);
    }


    // -------------------------------
    // DOCTOR CONFIRM
    // -------------------------------
    @Transactional
    public void confirmAppointment(Long doctorId, Long appointmentId) {

        Appointment appointment = appointmentRepository
                .findByIdAndDoctorId(appointmentId, doctorId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(Appointment.Status.CONFIRMED);
    }

    // -------------------------------
    // DOCTOR REJECT
    // -------------------------------
    @Transactional
    public void rejectAppointment(Long doctorId, Long appointmentId) {

        Appointment appointment = appointmentRepository
                .findByIdAndDoctorId(appointmentId, doctorId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Change appointment state
        appointment.setStatus(Appointment.Status.REJECTED);

        // Re-open slot ONLY if it was booked by this appointment
        DoctorAvailability slot = appointment.getSlot();

        if (slot.getStatus() == DoctorAvailability.SlotStatus.BOOKED) {
            slot.setStatus(DoctorAvailability.SlotStatus.OPEN);
        }
    }


    public List<Appointment> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }
}
