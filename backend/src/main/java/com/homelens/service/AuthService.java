package com.homelens.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.homelens.dto.AuthResponse;
import com.homelens.dto.LoginRequest;
import com.homelens.dto.RegisterRequest;
import com.homelens.dto.UserProfileResponse;
import com.homelens.model.Role;
import com.homelens.model.User;
import com.homelens.repository.UserRepository;
import com.homelens.security.CustomUserDetails;
import com.homelens.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered: " + request.getEmail());
        }

        Role assignedRole = request.getRole() != null ? request.getRole() : Role.ROLE_BUYER;

        User user = User.builder()
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .phone(request.getPhone())
                .role(assignedRole)
                .build();

        User savedUser = userRepository.save(user);
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String token = jwtService.generateToken(
                userDetails,
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getRole().name()
        );

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole()
        );
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().toLowerCase().trim(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(
                userDetails,
                user.getId(),
                user.getFullName(),
                user.getRole().name()
        );

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        );
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
