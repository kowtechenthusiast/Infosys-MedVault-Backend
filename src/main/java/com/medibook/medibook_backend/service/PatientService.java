package com.medibook.medibook_backend.service;

import com.medibook.medibook_backend.dto.PatientRegisterRequest;
import com.medibook.medibook_backend.entity.Patient;
import com.medibook.medibook_backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailService emailService;

    public PatientService(PatientRepository patientRepository, EmailService emailService) {
        this.patientRepository = patientRepository;
        this.emailService = emailService;
    }

    public Patient registerPatient(PatientRegisterRequest request) {
        Patient patient = new Patient();
        patient.setFullName(request.getFullName());
        patient.setEmail(request.getEmail());

        // Use garbage password initially (prevents login until approved & temp password
        // issued)
        patient.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

        patient.setPhone(request.getPhone());
        patient.setAge(request.getAge());
        patient.setStatus("PENDING");
        return patientRepository.save(patient);
    }

    public Patient login(String email, String rawPassword) {
        Optional<Patient> opt = patientRepository.findByEmail(email);
        if (opt.isEmpty()) {
            throw new RuntimeException("Patient not found with email: " + email);
        }

        Patient patient = opt.get();

        if (!"ACTIVE".equalsIgnoreCase(patient.getStatus()) && !"APPROVED".equalsIgnoreCase(patient.getStatus())) {
            if ("PENDING".equalsIgnoreCase(patient.getStatus())) {
                throw new RuntimeException("Your account is pending approval.");
            }
            if ("REJECTED".equalsIgnoreCase(patient.getStatus())) {
                throw new RuntimeException("Your account has been rejected.");
            }
        }

        if (!passwordEncoder.matches(rawPassword, patient.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return patient;
    }

    public List<Patient> getPendingPatients() {
        return patientRepository.findByStatusIgnoreCase("PENDING");
    }

    public List<Patient> getApprovedPatients() {
        return patientRepository.findByStatusIgnoreCase("ACTIVE");
    }

    public List<Patient> getRejectedPatients() {
        return patientRepository.findByStatusIgnoreCase("REJECTED");
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient approvePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        // 1. Set status ACTIVE
        patient.setStatus("ACTIVE");

        // 2. Generate temporary password
        String tempPassword = UUID.randomUUID().toString().substring(0, 8);

        // 3. Hash & store
        patient.setPassword(passwordEncoder.encode(tempPassword));

        // 4. Set mustChangePassword = true
        patient.setMustChangePassword(true);

        Patient saved = patientRepository.save(patient);

        // 5. Send Email 1
        emailService.sendApprovalEmail(patient.getEmail(), patient.getFullName(), tempPassword);

        return saved;
    }

    public Patient rejectPatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
        patient.setStatus("REJECTED");
        return patientRepository.save(patient);
    }

    // New method for changing password on first login
    public void changePasswordFirstLogin(String email, String currentPassword, String newPassword,
            String confirmPassword) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (!patient.isMustChangePassword()) {
            throw new RuntimeException("Password change not required or already completed.");
        }

        if (!passwordEncoder.matches(currentPassword, patient.getPassword())) {
            throw new RuntimeException("Invalid current password");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        patient.setPassword(passwordEncoder.encode(newPassword));
        patient.setMustChangePassword(false);
        patientRepository.save(patient);

        emailService.sendPasswordChangeConfirmationEmail(patient.getEmail());
    }
}
