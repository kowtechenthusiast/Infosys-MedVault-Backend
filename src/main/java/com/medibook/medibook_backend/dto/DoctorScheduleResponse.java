package com.medibook.medibook_backend.dto;

import java.time.LocalDate;
import java.util.List;

public class DoctorScheduleResponse {

    private Long doctorId;
    private LocalDate date;
    private boolean dayBlocked;
    private List<SlotResponse> slots;

    public DoctorScheduleResponse(
            Long doctorId,
            LocalDate date,
            boolean dayBlocked,
            List<SlotResponse> slots
    ) {
        this.doctorId = doctorId;
        this.date = date;
        this.dayBlocked = dayBlocked;
        this.slots = slots;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isDayBlocked() {
        return dayBlocked;
    }

    public List<SlotResponse> getSlots() {
        return slots;
    }
}
