package com.medibook.medibook_backend.dto;

public record DoctorEmergencyDTO(
        Long id,
        String name,
        String specialization,
        String qualification,
        Integer experience,
        String phone,
        String clinicName,
        String city,
        String state,
        Double averageRating,
        Integer consultationFee
) {}