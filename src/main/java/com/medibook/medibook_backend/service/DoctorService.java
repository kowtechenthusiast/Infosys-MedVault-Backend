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




}
