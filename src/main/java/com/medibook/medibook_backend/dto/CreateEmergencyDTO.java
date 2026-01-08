package com.medibook.medibook_backend.dto;

import com.medibook.medibook_backend.entity.EmergencyRequest.SeverityLevel;

public record CreateEmergencyDTO(
        Long patientId,
        String message,
        String location,
        SeverityLevel severityLevel
) {}