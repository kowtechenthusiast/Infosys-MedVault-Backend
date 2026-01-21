package com.medibook.medibook_backend.dto;

import com.medibook.medibook_backend.entity.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record DoctorReviewDTO(
        Integer rating,
        String review,

        // appointment details
        Long appointmentId,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        Appointment.Status appointmentStatus,
        String appointmentReason,

        // patient details (safe subset)
        Long patientId,
        String patientName,
        String patientGender,
        String patientCity,

        // audit
        LocalDateTime createdAt
) {}
