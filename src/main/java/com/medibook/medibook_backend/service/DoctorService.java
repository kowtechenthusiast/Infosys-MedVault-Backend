package com.medibook.medibook_backend.service;

import com.medibook.medibook_backend.entity.Doctor;
import com.medibook.medibook_backend.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailService emailService;

    @Value("${medibook.upload.dir}")
    private String uploadDir;

    public DoctorService(DoctorRepository doctorRepository, EmailService emailService) {
        this.doctorRepository = doctorRepository;
        this.emailService = emailService;
    }

    public Doctor registerDoctorWithFile(Doctor doctor, MultipartFile licenseFile) throws IOException {
        if (licenseFile != null && !licenseFile.isEmpty()) {
            Path uploadPath = Paths.get(this.uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalName = licenseFile.getOriginalFilename();
            String ext = (originalName != null && originalName.contains("."))
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";
            String uniqueName = UUID.randomUUID().toString() + ext;
            Path filePath = uploadPath.resolve(uniqueName);
            licenseFile.transferTo(filePath.toFile());
            doctor.setLicenseDocumentPath(filePath.toString());
        }

        // Use garbage password initially (prevents login until approved & temp password
        // issued)
        doctor.setPassword(this.passwordEncoder.encode(UUID.randomUUID().toString()));
        doctor.setStatus("PENDING");
        return doctorRepository.save(doctor);
    }

    public Doctor login(String email, String rawPassword) {
        Optional<Doctor> opt = doctorRepository.findByEmail(email);
        if (opt.isEmpty()) {
            throw new RuntimeException("Doctor not found with email: " + email);
        }
        Doctor doctor = opt.get();
        if (!"ACTIVE".equalsIgnoreCase(doctor.getStatus()) && !"APPROVED".equalsIgnoreCase(doctor.getStatus())) {
            if ("PENDING".equalsIgnoreCase(doctor.getStatus())) {
                throw new RuntimeException("Your account is pending approval.");
            }
            if ("REJECTED".equalsIgnoreCase(doctor.getStatus())) {
                throw new RuntimeException("Your account has been rejected.");
            }
        }
        if (!passwordEncoder.matches(rawPassword, doctor.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return doctor;
    }

    public List<Doctor> getPendingDoctors() {
        return doctorRepository.findByStatusIgnoreCase("PENDING");
    }

    public List<Doctor> getApprovedDoctors() {
        return doctorRepository.findByStatusIgnoreCase("ACTIVE");
    }

    public List<Doctor> getRejectedDoctors() {
        return doctorRepository.findByStatusIgnoreCase("REJECTED");
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor approveDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));

        doctor.setStatus("ACTIVE"); /** Status set to ACTIVE upon approval */

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        doctor.setPassword(passwordEncoder.encode(tempPassword));
        doctor.setMustChangePassword(true);
        Doctor saved = doctorRepository.save(doctor);

        emailService.sendApprovalEmail(doctor.getEmail(), doctor.getFullName(), tempPassword);
        return saved;
    }

    public Doctor rejectDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new RuntimeException("Doctor not found"));
        doctor.setStatus("REJECTED");
        return doctorRepository.save(doctor);
    }

    public void changePasswordFirstLogin(String email, String currentPassword, String newPassword,
            String confirmPassword) {
        Doctor doctor = doctorRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Doctor not found"));
        if (!doctor.isMustChangePassword()) {
            throw new RuntimeException("Password change not required or already completed.");
        }
        if (!passwordEncoder.matches(currentPassword, doctor.getPassword())) {
            throw new RuntimeException("Invalid current password");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New password and confirm password do not match");
        }
        doctor.setPassword(passwordEncoder.encode(newPassword));
        doctor.setMustChangePassword(false);
        doctorRepository.save(doctor);
        emailService.sendPasswordChangeConfirmationEmail(doctor.getEmail());
    }
}
