package com.medibook.medibook_backend.dto;

import com.medibook.medibook_backend.entity.EmergencyRequest.EmergencyStatus;
import java.time.LocalDateTime;

public record EmergencyRequestDTO(
        Long id,
        Long patientId,
        String patientName,
        String phone,
        String message,
        String timestamp, // Formatted for frontend (e.g., "2 mins ago")
        EmergencyStatus status,
        String acceptedByDoctorName
) {}