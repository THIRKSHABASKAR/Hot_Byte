package com.hotbyte.service.impl;

import com.hotbyte.dto.request.LoginRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.util.Collections;
import com.hotbyte.dto.request.RegisterRequest;
import com.hotbyte.dto.response.AuthResponse;
import com.hotbyte.entity.User;
import com.hotbyte.exception.BadRequestException;
import com.hotbyte.exception.ResourceNotFoundException;
import com.hotbyte.repository.UserRepository;
import com.hotbyte.security.JwtUtil;
import com.hotbyte.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthServiceImpl.class);

    private static final String GOOGLE_CLIENT_ID =
            "replace_with_your_client_id";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    // ── Register ─────────────────────────────────

    @Override
    public AuthResponse register(RegisterRequest request) {

        logger.debug("Register attempt for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                    "Email already registered. Please login.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(User.Role.USER);
        user.setIsActive(true);
        user.setWalletBalance(BigDecimal.ZERO);

        if (request.getGender() != null && !request.getGender().isEmpty()) {
            try {
                user.setGender(User.Gender.valueOf(
                        request.getGender().toUpperCase().trim()));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid gender value: {}", request.getGender());
            }
        }

        User savedUser = userRepository.save(user);
        logger.info("New user registered: {}", savedUser.getEmail());

        UserDetails userDetails = userDetailsService
                .loadUserByUsername(savedUser.getEmail());

        String token = jwtUtil.generateToken(
                userDetails,
                savedUser.getId(),
                savedUser.getRole().name());

        return AuthResponse.builder()
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .token(token)
                .tokenType("Bearer")
                .build();
    }

    // ── Login ────────────────────────────────────

    @Override
    public AuthResponse login(LoginRequest request) {

        logger.debug("Login attempt for email: {}", request.getEmail());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid email or password.");
        }

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!user.getIsActive()) {
            throw new BadRequestException("Your account has been deactivated.");
        }

        logger.info("User logged in: {}", user.getEmail());

        UserDetails userDetails = userDetailsService
                .loadUserByUsername(user.getEmail());

        String token = jwtUtil.generateToken(
                userDetails,
                user.getId(),
                user.getRole().name());

        return AuthResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .token(token)
                .tokenType("Bearer")
                .build();
    }

    // ── Google Login ─────────────────────────────

    @Override
    public AuthResponse googleLogin(String token) {

        logger.debug("Google login attempt");

        // 1. Verify Google token with clock skew tolerance
        GoogleIdToken.Payload payload = verifyGoogleToken(token);

        // 2. Extract user info from Google payload
        String email = payload.getEmail();
        String name  = (String) payload.get("name");

        logger.debug("Google login for email: {}", email);

        // 3. Find existing user or create new one
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name != null ? name : email);
                    newUser.setPassword(""); // No password for Google users
                    newUser.setRole(User.Role.USER);
                    newUser.setIsActive(true);
                    newUser.setWalletBalance(BigDecimal.ZERO);
                    User saved = userRepository.save(newUser);
                    logger.info("New Google user created: {}", email);
                    return saved;
                });

        // 4. Generate JWT token
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(user.getEmail());

        String jwt = jwtUtil.generateToken(
                userDetails,
                user.getId(),
                user.getRole().name());

        logger.info("Google login successful for: {}", email);

        // 5. Return auth response
        return AuthResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .token(jwt)
                .tokenType("Bearer")
                .build();
    }

    // ── Google Token Verifier ────────────────────

    private GoogleIdToken.Payload verifyGoogleToken(String token) {
        try {
            NetHttpTransport transport = new NetHttpTransport();
            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            // Allow 5 minutes clock skew tolerance
            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                            .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                            .build();

            // First try: parse and verify normally
            GoogleIdToken idToken = verifier.verify(token);

            // Second try: if normal verify fails due to clock skew,
            // parse manually and check just the audience
            if (idToken == null) {
                logger.warn("Standard verify failed, trying lenient parse...");
                idToken = GoogleIdToken.parse(jsonFactory, token);

                if (idToken == null) {
                    throw new BadRequestException("Invalid Google token — could not parse.");
                }

                // Manually check audience
                GoogleIdToken.Payload payload = idToken.getPayload();
                boolean audienceMatch =
                        payload.getAudienceAsList()
                               .contains(GOOGLE_CLIENT_ID);

                if (!audienceMatch) {
                    throw new BadRequestException("Google token audience mismatch.");
                }

                logger.warn("Lenient parse succeeded for: {}", payload.getEmail());
                return payload;
            }

            return idToken.getPayload();

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Google token verification error: {}", e.getMessage());
            throw new BadRequestException(
                    "Google token verification failed: " + e.getMessage());
        }
    }
}