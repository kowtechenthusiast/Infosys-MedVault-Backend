// =======================
// PatientRepository.java
// =======================
package com.medibook.medibook_backend.repository;

import com.medibook.medibook_backend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEmail(String email);

    List<Patient> findByStatusIgnoreCase(String status);
}
