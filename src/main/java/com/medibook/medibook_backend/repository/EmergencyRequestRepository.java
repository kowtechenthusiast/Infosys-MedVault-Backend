package com.medibook.medibook_backend.repository;

import com.medibook.medibook_backend.entity.EmergencyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Long> {
    List<EmergencyRequest> findByStatusOrderByCreatedAtDesc(EmergencyRequest.EmergencyStatus status);
}