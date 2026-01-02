package com.medibook.medibook_backend.repository;

import com.medibook.medibook_backend.entity.DoctorAvailability;
import com.medibook.medibook_backend.entity.DoctorAvailability.SlotStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DoctorAvailabilityRepository
        extends JpaRepository<DoctorAvailability, Long> {


    // 🔐 CRITICAL: Lock slot during booking
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from DoctorAvailability s where s.id = :id")
    Optional<DoctorAvailability> findByIdForUpdate(Long id);

    // Find OPEN slot only
    Optional<DoctorAvailability> findByIdAndStatus(
            Long id, SlotStatus status);

    void deleteByDoctorIdAndSlotDate(Long doctorId, LocalDate date);

    @Modifying
    @Query("""
        DELETE FROM DoctorAvailability d
        WHERE d.doctor.id = :doctorId
          AND d.slotDate = :date
          AND d.status = 'OPEN'
    """)
    void deleteOpenSlots(Long doctorId, LocalDate date);

    List<DoctorAvailability> findByDoctor_IdAndSlotDateOrderByStartTime(Long doctorId, LocalDate date);

    boolean existsByDoctor_IdAndSlotDateAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId,
            LocalDate date,
            LocalTime end,
            LocalTime start
    );

    List<DoctorAvailability> findByDoctor_IdAndSlotDateAndStatusOrderByStartTime(
            Long doctorId,
            LocalDate slotDate,
            DoctorAvailability.SlotStatus status
    );

}

