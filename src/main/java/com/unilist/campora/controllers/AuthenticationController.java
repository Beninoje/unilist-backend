package com.unilist.campora.controllers;

import com.unilist.campora.dto.*;
import com.unilist.campora.model.Listing;
import com.unilist.campora.model.RefreshToken;
import com.unilist.campora.model.User;
import com.unilist.campora.records.auth.ForgotPasswordRequest;
import com.unilist.campora.records.auth.ResendRequest;
import com.unilist.campora.records.auth.ResetPasswordRequest;
import com.unilist.campora.records.auth.VerifyResetCodeRequest;
import com.unilist.campora.repository.RefreshTokenRepository;
import com.unilist.campora.repository.UserRepository;
import com.unilist.campora.responses.LoginResponse;
import com.unilist.campora.responses.RegisterResponse;
import com.unilist.campora.services.AuthenticationService;
import com.unilist.campora.services.JwtService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/auth")
@RestController

public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> register (@RequestBody RegisterUserDto registerUserDto) {
        Map<String, String> error = new HashMap<>();
        if(userRepository.findByEmail(registerUserDto.getEmail()).isPresent()){
            error.put("email", "That email is already taken");
            return ResponseEntity.badRequest().body(error);
        }
        if(registerUserDto.getPassword().length() < 12){
            error.put("password", "Password must be greater that 8 characters");
            return ResponseEntity.badRequest().body(error);
        }

        User user = authenticationService.signUp(registerUserDto);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (@RequestBody LoginUserDto loginUserDto) {
        try {
            User user = authenticationService.authenticate(loginUserDto);
            String token = jwtService.generateToken(user);
            UUID refreshToken = UUID.randomUUID();

            RefreshToken rtExists = refreshTokenRepository.findByUser(user).orElse(null);
            if(rtExists != null){
                rtExists.setToken(refreshToken);
                rtExists.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
                rtExists.setRevoked(false);
                refreshTokenRepository.save(rtExists);
            }else {
                refreshTokenRepository.save(RefreshToken.builder()
                        .user(user)
                        .token(refreshToken)
                        .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                        .revoked(false)
                        .build()

                );
            }
            LoginResponse loginResponse = new LoginResponse(
                        user.getId(),
                        token,
                        jwtService.getJwtExpiration(),
                        refreshToken,
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getFavourites().stream().map(Listing::getId).toList(),
                        user.getLatitude(),
                        user.getLongitude(),
                        user.isOnboardingComplete()
            );
            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshDto req){
        /**
         * TODO
         * get the refreshToken from req and fetch it from DB
         * if token is found then generate new access token and refresh token
         * if token IS NOT found then generate a new access/refresh token for the user
         */
        UUID refreshToken;
        try {
            refreshToken = UUID.fromString(String.valueOf(req.getRefreshToken()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token format");
        }
        RefreshToken tokenStored = refreshTokenRepository.findByToken(req.getRefreshToken())
                .orElseThrow(()-> new RuntimeException("Invalid refresh token"));

        if(tokenStored.isRevoked() || tokenStored.getExpiresAt().isBefore(Instant.now())){
            return ResponseEntity.status(401).body("Refresh token expired or revoked");
        }

        UUID newRefreshToken = UUID.randomUUID();
        tokenStored.setToken(newRefreshToken);
        tokenStored.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        tokenStored.setRevoked(false);
        refreshTokenRepository.save(tokenStored);

        String newAccessToken = jwtService.generateToken(tokenStored.getUser());

        return ResponseEntity.ok(Map.of(
                "accessToken",newAccessToken,
                "refreshToken",newRefreshToken
        ));



    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify (@RequestBody VerifyUserDto verifyUserDto) {
        try{
            User user = authenticationService.verifyUser(verifyUserDto);
            String token = jwtService.generateToken(user);
            UUID refreshToken = UUID.randomUUID();
            refreshTokenRepository.save(RefreshToken.builder()
                    .user(user)
                    .token(refreshToken)
                    .expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
                    .revoked(false)
                    .build()

            );
            RegisterResponse registerResponse = new RegisterResponse(
                    token,
                    jwtService.getJwtExpiration(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    refreshToken,
                    user.isOnboardingComplete(),
                    user.getListings(),
                    user.getFavourites().stream().map(Listing::getId).toList(),
                    user.getLatitude(),
                    user.getLongitude()
            );
            return ResponseEntity.ok(registerResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutDto req){
        UUID refreshToken;
        try {
            refreshToken = UUID.fromString(String.valueOf(req.getRefreshToken()));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token format");
        }
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resend (@RequestBody ResendRequest req) {
        try{
            authenticationService.resendVerificationCode(req.email());
            return ResponseEntity.ok("Account resent!");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        User user = userRepository.findByEmail(req.email()).orElseThrow(() -> new RuntimeException("User does not exist!"));
        String otpCode = authenticationService.generateVerificationCode();
        user.setVerificationCode(otpCode);
        user.setVerificationExpiresAt(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        authenticationService.sendVerificationCodeEmail(user);
        return ResponseEntity.ok("Verification code sent!");
    }
    @PostMapping("/verify-reset-code")
    public ResponseEntity<?> verifyResetCode(@RequestBody VerifyUserDto verifyUserDto) {
        authenticationService.verifyUser(verifyUserDto);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {
        User user = userRepository.findByEmail(req.email()).orElseThrow(() -> new RuntimeException("User does not exist!"));
        if(req.newPassword() != null && !req.newPassword().isEmpty() && req.newPassword().length() >= 12) {
            user.setPassword(passwordEncoder.encode(req.newPassword()));
            userRepository.save(user);
        }else{
            return ResponseEntity.badRequest().body("Password cannot be empty");
        }
        return ResponseEntity.ok().build();
    }


}
