package com.gem.service;

import com.gem.config.JwtUtil;
import com.gem.dto.AuthResponse;
import com.gem.dto.LoginRequest;
import com.gem.dto.RegisterRequest;
import com.gem.model.User;
import com.gem.model.User.Role;
import com.gem.model.User.UserType;
import com.gem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository         userRepo;
    private final PasswordEncoder        encoder;
    private final JwtUtil                jwtUtil;
    private final AuthenticationManager  authManager;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        UserDetails ud    = userDetailsService.loadUserByUsername(req.getEmail());
        String      token = jwtUtil.generateToken(ud);
        User        user  = userRepo.findByEmail(req.getEmail()).orElseThrow();
        return buildResponse(user, token);
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setPhone(req.getPhone());
        user.setNationality(req.getNationality());
        user.setSsn(req.getSsn());
        user.setPassportNumber(req.getPassportNumber());
        if (req.getUserType() != null) {
            try { user.setUserType(UserType.valueOf(req.getUserType().toUpperCase())); }
            catch (IllegalArgumentException ignored) {}
        }
        user.setRole(Role.USER);
        userRepo.save(user);

        UserDetails ud    = userDetailsService.loadUserByUsername(user.getEmail());
        String      token = jwtUtil.generateToken(ud);
        return buildResponse(user, token);
    }

    private AuthResponse buildResponse(User user, String token) {
        AuthResponse res = new AuthResponse();
        res.setToken(token);
        res.setType("Bearer");
        res.setId(user.getId());
        res.setFullName(user.getFullName());
        res.setEmail(user.getEmail());
        res.setRole(user.getRole().name());
        return res;
    }
}
