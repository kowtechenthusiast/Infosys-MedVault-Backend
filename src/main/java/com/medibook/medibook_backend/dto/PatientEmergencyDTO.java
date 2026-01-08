package com.medibook.medibook_backend.dto;

import java.time.LocalDate;

public record PatientEmergencyDTO(
        Long id,
        String name,
        String email,
        String phone,
        String gender,
        LocalDate dob,
        String bloodGroup,
        // Health Metrics
        Integer sugarLevel,
        Integer bpSys,
        Integer bpDia,
        Integer spo2,
        Integer heartRate,
        // Lifestyle
        String diet,
        String smoking,
        String alcohol,
        Integer sleepHours,
        // Location
        String address,
        String city,
        String state,
        String pincode
) {}