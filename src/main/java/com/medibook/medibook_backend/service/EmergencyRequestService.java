package com.medibook.medibook_backend.service;

import com.medibook.medibook_backend.dto.EmergencyRequestDTO;
import com.medibook.medibook_backend.entity.EmergencyRequest;
import com.medibook.medibook_backend.entity.Doctor;
import com.medibook.medibook_backend.repository.EmergencyRequestRepository;
import com.medibook.medibook_backend.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmergencyRequestService {

    private final EmergencyRequestRepository repository;
    private final DoctorRepository doctorRepository;

    public EmergencyRequestService(EmergencyRequestRepository repository, DoctorRepository doctorRepository) {
        this.repository = repository;
        this.doctorRepository = doctorRepository;
    }

    public List<EmergencyRequestDTO> getRequestsByStatus(EmergencyRequest.EmergencyStatus status) {
        return repository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void acceptRequest(Long id, Long doctorId) {
        EmergencyRequest req = repository.findById(id).orElseThrow();
        Doctor doc = doctorRepository.findById(doctorId).orElseThrow();

        req.setStatus(EmergencyRequest.EmergencyStatus.ACCEPTED);
        req.setDoctor(doc);
        req.setAcceptedAt(LocalDateTime.now());
        repository.save(req);
    }

    @Transactional
    public void ignoreRequest(Long id) {
        EmergencyRequest req = repository.findById(id).orElseThrow();
        req.setStatus(EmergencyRequest.EmergencyStatus.IGNORED);
        repository.save(req);
    }

    private EmergencyRequestDTO convertToDTO(EmergencyRequest req) {
        return new EmergencyRequestDTO(
                req.getId(),
                req.getPatient().getId(),
                req.getPatient().getUser().getName(),
                req.getContactNumber(),
                req.getMessage(),
                "Just now", // In production, use a time-ago library
                req.getStatus(),
                req.getDoctor() != null ? req.getDoctor().getUser().getName() : null
        );
    }
}