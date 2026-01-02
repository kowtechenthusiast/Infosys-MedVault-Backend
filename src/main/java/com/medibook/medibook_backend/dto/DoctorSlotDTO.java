package com.medibook.medibook_backend.dto;

import com.medibook.medibook_backend.entity.DoctorAvailability.SlotStatus;
import java.time.LocalTime;

public record DoctorSlotDTO(
        Long id,
        LocalTime startTime,
        LocalTime endTime,
        SlotStatus status,
        Long appointmentId
) {}
