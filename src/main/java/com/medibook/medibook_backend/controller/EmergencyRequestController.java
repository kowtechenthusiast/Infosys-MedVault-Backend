package com.medibook.medibook_backend.controller;

import com.medibook.medibook_backend.dto.CreateEmergencyDTO;
import com.medibook.medibook_backend.dto.EmergencyRequestDTO;
import com.medibook.medibook_backend.entity.EmergencyRequest;
import com.medibook.medibook_backend.service.EmergencyRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergencies")
public class EmergencyRequestController {

    private final EmergencyRequestService service;

    public EmergencyRequestController(EmergencyRequestService service) {
        this.service = service;
    }

    // Updated to support optional filtering by severity

    @GetMapping
    public List<EmergencyRequestDTO> getRequests(
            @RequestParam List<EmergencyRequest.EmergencyStatus> status,
            @RequestParam(required = false) EmergencyRequest.SeverityLevel severity,
            @RequestParam(required = false) String city
    ) {

        if (city != null) {
            return service.getRequestsByStatusesAndCity(status, city);
        }

        return service.getRequestsByStatuses(status);
    }



    @PatchMapping("/{id}/accept")
    public ResponseEntity<Void> accept(@PathVariable Long id, @RequestParam Long doctorId) {
        service.acceptRequest(id, doctorId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/ignore")
    public ResponseEntity<Void> ignore(@PathVariable Long id) {
        service.ignoreRequest(id);
        return ResponseEntity.ok().build();
    }

    // Add these to EmergencyRequestController.java

    @PostMapping
    public ResponseEntity<Void> createRequest(@RequestBody CreateEmergencyDTO dto) {
        service.createRequest(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/patient/{patientId}")
    public List<EmergencyRequestDTO> getPatientHistory(@PathVariable Long patientId) {
        return service.getPatientHistory(patientId);
    }
}