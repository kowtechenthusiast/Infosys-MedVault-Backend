package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.*;
import com.medibook.medibook_backend.service.DoctorScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/doctor/schedule")
public class DoctorScheduleController {

    private final DoctorScheduleService service;

    public DoctorScheduleController(DoctorScheduleService service) {
        this.service = service;
    }

    /* ================= GET ================= */
    @GetMapping
    public DoctorScheduleResponse getSchedule(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return service.getSchedule(doctorId, date);
    }


    /* ================= GENERATE ================= */
    @PostMapping("/generate")
    public void generate(@RequestBody GenerateScheduleRequest req) {
        service.generateSlots(req);
    }

    /* ================= BLOCK DAY ================= */
    @PutMapping("/block-day")
    public void blockDay(@RequestBody Map<String, Object> body) {
        service.blockDay(
                Long.valueOf(body.get("doctorId").toString()),
                LocalDate.parse(body.get("date").toString())
        );
    }

    /* ================= UNBLOCK DAY ================= */
    @PutMapping("/unblock-day")
    public void unblockDay(@RequestBody Map<String, Object> body) {
        service.unblockDay(
                Long.valueOf(body.get("doctorId").toString()),
                LocalDate.parse(body.get("date").toString())
        );
    }


}
