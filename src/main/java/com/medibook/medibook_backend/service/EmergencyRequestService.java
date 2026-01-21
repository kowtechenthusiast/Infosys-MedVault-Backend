package com.medibook.medibook_backend.service;

import com.medibook.medibook_backend.dto.CreateEmergencyDTO;
import com.medibook.medibook_backend.dto.DoctorEmergencyDTO;
import com.medibook.medibook_backend.dto.EmergencyRequestDTO;
import com.medibook.medibook_backend.dto.PatientEmergencyDTO;
import com.medibook.medibook_backend.entity.EmergencyRequest;
import com.medibook.medibook_backend.entity.Doctor;
import com.medibook.medibook_backend.entity.Patient;
import com.medibook.medibook_backend.repository.EmergencyRequestRepository;
import com.medibook.medibook_backend.repository.DoctorRepository;
import com.medibook.medibook_backend.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmergencyRequestService {

    private final EmergencyRequestRepository repository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public EmergencyRequestService(EmergencyRequestRepository repository, DoctorRepository doctorRepository, PatientRepository patientRepository) {
        this.repository = repository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public List<EmergencyRequestDTO> getRequestsByStatuses(
            List<EmergencyRequest.EmergencyStatus> statuses
    ) {
        return repository
                .findByStatusInOrderByCreatedAtDesc(statuses)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmergencyRequestDTO> getRequestsByStatusesAndCity(
            List<EmergencyRequest.EmergencyStatus> statuses,
            String city
    ) {
        return repository
                .findByStatusInAndLocationOrderByCreatedAtDesc(statuses, city)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    @Transactional
    public void acceptRequest(Long id, Long doctorId) {
        EmergencyRequest req = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emergency Request not found"));
        Doctor doc = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        req.setStatus(EmergencyRequest.EmergencyStatus.ACCEPTED);
        req.setDoctor(doc);
        req.setAcceptedAt(LocalDateTime.now());
        repository.save(req);
    }

    @Transactional
    public void ignoreRequest(Long id) {
        EmergencyRequest req = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emergency Request not found"));
        req.setStatus(EmergencyRequest.EmergencyStatus.IGNORED);
        repository.save(req);
    }

    @Transactional
    public void createRequest(CreateEmergencyDTO dto) {
        Patient patient = patientRepository.findById(dto.patientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        EmergencyRequest request = new EmergencyRequest();
        request.setPatient(patient);
        request.setMessage(dto.message());
        request.setLocation(dto.location());
        request.setSeverityLevel(dto.severityLevel());
        request.setStatus(EmergencyRequest.EmergencyStatus.PENDING);

        // Ensure timestamp is set before saving
        request.setCreatedAt(LocalDateTime.now());

        repository.save(request);
    }

    public List<EmergencyRequestDTO> getPatientHistory(Long patientId) {
        return repository.findByPatientIdOrderByCreatedAtDesc(patientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /* ================= HELPER METHODS ================= */
    private EmergencyRequestDTO convertToDTO(EmergencyRequest req) {
        // Map Comprehensive Patient Details
        Patient p = req.getPatient();
        PatientEmergencyDTO patientDTO = new PatientEmergencyDTO(
                p.getId(),
                p.getUser() != null ? p.getUser().getName() : "Unknown",
                p.getUser() != null ? p.getUser().getEmail() : "N/A",
                p.getPhone(),
                p.getGender(),
                p.getDateOfBirth(),
                p.getBloodGroup(),
                p.getSugarLevel(),
                p.getBpSys(),
                p.getBpDia(),
                p.getSpo2(),
                p.getHeartRate(),
                p.getDiet(),
                p.getSmoking(),
                p.getAlcohol(),
                p.getSleepHours(),
                p.getAddress(),
                p.getCity(),
                p.getState(),
                p.getPincode()
        );

        // Map Comprehensive Doctor Details
        DoctorEmergencyDTO doctorDTO = null;
        if (req.getDoctor() != null) {
            Doctor d = req.getDoctor();
            doctorDTO = new DoctorEmergencyDTO(
                    d.getId(),
                    d.getUser() != null ? d.getUser().getName() : "Dr. Unknown",
                    d.getSpecialization(),
                    d.getQualification(),
                    d.getExperience(),
                    d.getPhone(),
                    d.getClinicHospitalName(),
                    d.getCity(),
                    d.getState(),
                    d.getAverageRating(),
                    d.getConsultationFee()
            );
        }

        return new EmergencyRequestDTO(
                req.getId(),
                patientDTO,
                doctorDTO,
                req.getSeverityLevel(),
                req.getLocation(),
                req.getMessage(),
                formatTimestamp(req.getCreatedAt()),
                req.getStatus()
        );
    }

    /**
     * Formats the timestamp for the frontend.
     * You can return a raw string or "Time Ago" logic.
     */
    private String formatTimestamp(LocalDateTime createdAt) {
        if (createdAt == null) return "Just now";

        long minutes = ChronoUnit.MINUTES.between(createdAt, LocalDateTime.now());

        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " mins ago";
        if (minutes < 1440) return (minutes / 60) + " hours ago";

        return createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }
}