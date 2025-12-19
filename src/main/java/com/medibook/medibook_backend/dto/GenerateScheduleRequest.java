package com.medibook.medibook_backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class GenerateScheduleRequest {

    public Long doctorId;
    public LocalDate date;
    public LocalTime startTime;
    public LocalTime endTime;
    public Integer durationMinutes;
}
