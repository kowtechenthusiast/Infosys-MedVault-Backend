package com.medibook.medibook_backend.repository;

import com.medibook.medibook_backend.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Doctor dashboard (date-wise)
    List<Appointment> findByDoctorIdAndAppointmentDate(
            Long doctorId, LocalDate date);

    // Doctor owns appointment
    Optional<Appointment> findByIdAndDoctorId(
            Long appointmentId, Long doctorId);

    // Doctor dashboard (all)
    List<Appointment> findByDoctorId(Long doctorId);

    // Patient dashboard
    List<Appointment> findByPatientId(Long patientId);
}
