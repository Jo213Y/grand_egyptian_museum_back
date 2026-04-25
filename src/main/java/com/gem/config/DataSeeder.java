package com.gem.config;

import com.gem.model.User;
import com.gem.model.User.Role;
import com.gem.repository.HallRepository;
import com.gem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final HallRepository  hallRepo;
    private final UserRepository  userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate    jdbc;

    @Override
    public void run(String... args) {
        seedRoles();
        seedAdmins();
        log.info("✅ Connected to museum_project DB — found {} halls", hallRepo.count());
    }

    private void seedRoles() {
        jdbc.update("INSERT IGNORE INTO roles (role_id, role_name) VALUES (1, 'USER')");
        jdbc.update("INSERT IGNORE INTO roles (role_id, role_name) VALUES (2, 'ADMIN')");
        jdbc.update("INSERT IGNORE INTO roles (role_id, role_name) VALUES (3, 'BLOCK')");
        log.info("✅ Roles seeded");
    }

    private void seedAdmins() {
        seedAdmin("Joe",     "joe@gmail.com",     "Joe@123");
        seedAdmin("Mohamed", "mohamed@gmail.com", "Mohamed@123");
        seedAdmin("Hend",    "hend@gmail.com",    "Hend@123");
        seedAdmin("Omnia",   "omnia@gmail.com",   "Omnia@123");
        seedAdmin("Rana",    "rana@gmail.com",    "Rana@123");
    }

    private void seedAdmin(String name, String email, String password) {
        if (!userRepo.existsByEmail(email)) {
            User admin = new User();
            admin.setFullName(name);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);
            userRepo.save(admin);
            log.info("✅ Admin created: {}", email);
        }
    }
}
