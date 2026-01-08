package com.medibook.medibook_backend.repository;

import com.medibook.medibook_backend.entity.EmergencyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Long> {

    // Find requests by status (e.g., all PENDING requests)
    List<EmergencyRequest> findByStatusInOrderByCreatedAtDesc(
            List<EmergencyRequest.EmergencyStatus> statuses
    );

    List<EmergencyRequest> findByStatusInAndLocationOrderByCreatedAtDesc(
            List<EmergencyRequest.EmergencyStatus> statuses,
            String location
    );

    // Optional: Find all emergencies in a city regardless of status (for history)
    List<EmergencyRequest> findByLocationOrderByCreatedAtDesc(String location);


    // NEW: Find history for a specific patient
    List<EmergencyRequest> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}
