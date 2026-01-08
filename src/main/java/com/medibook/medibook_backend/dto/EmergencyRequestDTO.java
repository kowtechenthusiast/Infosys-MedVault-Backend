package com.medibook.medibook_backend.dto;

import com.medibook.medibook_backend.entity.EmergencyRequest.EmergencyStatus;
import com.medibook.medibook_backend.entity.EmergencyRequest.SeverityLevel;

public record EmergencyRequestDTO(
        Long id,
        PatientEmergencyDTO patient,
        DoctorEmergencyDTO doctor,
        SeverityLevel severityLevel,
        String location,
        String message,
        String timestamp,
        EmergencyStatus status
) {}