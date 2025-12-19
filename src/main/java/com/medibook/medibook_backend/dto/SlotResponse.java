package com.medibook.medibook_backend.dto;

import com.medibook.medibook_backend.entity.DoctorAvailability;
import java.time.LocalDate;
import java.time.LocalTime;

public class SlotResponse {

    private Long id;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer durationMinutes;
    private DoctorAvailability.SlotStatus status;

    public SlotResponse(
            Long id,
            LocalDate slotDate,
            LocalTime startTime,
            LocalTime endTime,
            Integer durationMinutes,
            DoctorAvailability.SlotStatus status
    ) {
        this.id = id;
        this.slotDate = slotDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.status = status;
    }

    /* GETTERS */

    public Long getId() {
        return id;
    }

    public LocalDate getSlotDate() {
        return slotDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public DoctorAvailability.SlotStatus getStatus() {
        return status;
    }
}
