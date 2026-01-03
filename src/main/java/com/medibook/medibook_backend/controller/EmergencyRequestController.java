package com.medibook.medibook_backend.controller;

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

    @GetMapping
    public List<EmergencyRequestDTO> getRequests(@RequestParam EmergencyRequest.EmergencyStatus status) {
        return service.getRequestsByStatus(status);
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
}