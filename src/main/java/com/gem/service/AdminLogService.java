package com.gem.service;

import com.gem.model.AdminLog;
import com.gem.model.User;
import com.gem.repository.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminLogService {

    private final AdminLogRepository logRepo;
    private final com.gem.repository.UserRepository userRepo;

    /** Log an action with no target user */
    public void log(String action, String detail) {
        log(action, detail, null);
    }

    /** Log an action that targets a specific user */
    public void log(String action, String detail, User targetUser) {
        String email    = "unknown";
        String fullName = "Unknown Admin";
        try {
            email = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            String resolvedEmail = email;
            fullName = userRepo.findByEmail(resolvedEmail)
                    .map(User::getFullName)
                    .orElse(resolvedEmail);
        } catch (Exception ignored) {}

        logRepo.save(AdminLog.builder()
                .adminEmail(email)
                .adminName(fullName)
                .action(action)
                .detail(detail)
                .targetUser(targetUser)
                .build());
    }
}