package com.gem.service;

import com.gem.model.AdminLog;
import com.gem.repository.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminLogService {

    private final AdminLogRepository logRepo;
    private final com.gem.repository.UserRepository userRepo;

    public void log(String action, String detail) {
        String email    = "unknown";
        String fullName = "Unknown Admin";
        try {
            email = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            fullName = userRepo.findByEmail(email)
                    .map(u -> u.getFullName())
                    .orElse(email);
        } catch (Exception ignored) {}

        logRepo.save(AdminLog.builder()
                .adminEmail(email)
                .adminName(fullName)
                .action(action)
                .detail(detail)
                .build());
    }
}