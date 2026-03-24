package com.unilist.campora.services;

import com.resend.core.exception.ResendException;
import com.unilist.campora.dto.LoginUserDto;
import com.unilist.campora.dto.RegisterUserDto;
import com.unilist.campora.dto.VerifyUserDto;
import com.unilist.campora.model.RefreshToken;
import com.unilist.campora.model.User;
import com.unilist.campora.repository.RefreshTokenRepository;
import com.unilist.campora.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TemplateEngine templateEngine;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService, RefreshTokenRepository refreshTokenRepository, TemplateEngine templateEngine) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.templateEngine = templateEngine;
    }

    public User signUp(RegisterUserDto input) {
        String encodedPassword = passwordEncoder.encode(input.getPassword());
        User user = new User(input.getEmail(), encodedPassword, input.getFirstName(), input.getLastName(), null);
        user.setPassword(encodedPassword);
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        user.setOnboardingComplete(false);
        sendVerificationCodeEmail(user);
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input){
        try {
            User user = userRepository.findByEmail(input.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if(!user.isEnabled()){
                throw new RuntimeException("Account is not verified, please verify your account");
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );

            return user;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    public User verifyUser(VerifyUserDto input){
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(user.getVerificationExpiresAt().isBefore(LocalDateTime.now())){
                throw new RuntimeException("Verification code expired");
            }
            if(user.getVerificationCode().equals(input.getVerificationCode())){
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationExpiresAt(null);

                userRepository.save(user);
            } else{
                throw new RuntimeException("Invalid verification code");
            }
            return user;
        } else{
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationExpiresAt(LocalDateTime.now().plusMinutes(15));
            sendVerificationCodeEmail(user);
            userRepository.save(user);
        }else{
            throw new RuntimeException("User not found");
        }
    }

    public void sendVerificationCodeEmail(User user){
        Context context = new Context();
        String subject = "Account verification";
        String verificationCode = user.getVerificationCode();
        context.setVariable("otpCode", verificationCode);
        String htmlMessage = templateEngine.process("opt-email",context);

        try{
            emailService.sendVerificationEmail(user.getEmail(), subject,htmlMessage);
        }catch (ResendException e){
            e.printStackTrace();
        }
    }

    public String generateVerificationCode(){
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
