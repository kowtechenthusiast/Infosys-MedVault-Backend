package com.medibook.medibook_backend.repository;

import com.medibook.medibook_backend.entity.AdminOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminOtpRepository extends JpaRepository<AdminOtp, Long> {

    AdminOtp findTopByOrderByCreatedAtDesc();
    Optional<AdminOtp> findTopByOtpCodeAndUsedIsFalseOrderByCreatedAtDesc(String otpCode);
}
