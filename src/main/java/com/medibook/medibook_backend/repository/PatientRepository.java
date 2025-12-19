package com.medibook.medibook_backend.repository;

import com.medibook.medibook_backend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("SELECT p FROM Patient p WHERE p.user.id = :userId")
    Optional<Patient> findByUserId(Long userId);

    boolean existsByUserId(Long userId);   // ✅ ADD THIS

    @Query("""
        SELECT p
        FROM Patient p
        JOIN p.user u
        WHERE u.status = PENDING
        AND p.dateOfBirth IS NOT NULL
        AND p.gender IS NOT NULL
        AND p.bloodGroup IS NOT NULL
        AND p.phone IS NOT NULL
        AND p.address IS NOT NULL
        AND p.city IS NOT NULL
        AND p.state IS NOT NULL
        AND p.pincode IS NOT NULL
    """)
    List<Patient> findAllPendingPatientsWithBasicInfo();
}
