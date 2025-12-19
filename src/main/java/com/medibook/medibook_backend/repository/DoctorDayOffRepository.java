package com.medibook.medibook_backend.repository;

import com.medibook.medibook_backend.entity.DoctorDayOff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DoctorDayOffRepository
        extends JpaRepository<DoctorDayOff, Long> {

    Optional<DoctorDayOff> findByDoctor_IdAndOffDate(Long doctorId, LocalDate date);

    void deleteByDoctor_IdAndOffDate(Long doctorId, LocalDate date);

    boolean existsByDoctor_IdAndOffDate(Long doctorId, LocalDate date);

}
