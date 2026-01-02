package com.medibook.medibook_backend.service;

import com.medibook.medibook_backend.entity.*;
import com.medibook.medibook_backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorRatingRepository ratingRepo;


    public DoctorService(PatientRepository patientRepository, DoctorRepository doctorRepository,DoctorRatingRepository ratingRepo) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.ratingRepo = ratingRepo;
    }

    @Transactional
    public void rateDoctor(
            Long doctorId,
            Long patientId,
            Integer rating
    ) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        if (ratingRepo.existsByDoctor_IdAndPatient_Id(doctorId, patientId)) {
            throw new RuntimeException("You have already rated this doctor");
        }

        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        Patient patient = patientRepository.findById(patientId).orElseThrow();

        DoctorRating dr = new DoctorRating();
        dr.setDoctor(doctor);
        dr.setPatient(patient);
        dr.setRating(rating);

        ratingRepo.save(dr);

        // ✅ UPDATED: delegate rating logic to entity
        doctor.addRating(rating);

        doctorRepository.save(doctor);
    }


}
