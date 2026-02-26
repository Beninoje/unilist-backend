package com.unilist.campora.services;

import com.resend.core.exception.ResendException;
import com.unilist.campora.dto.LoginUserDto;
import com.unilist.campora.dto.RegisterUserDto;
import com.unilist.campora.dto.VerifyUserDto;
import com.unilist.campora.model.User;
import com.unilist.campora.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public User signUp(RegisterUserDto input) {
        String encodedPassword = passwordEncoder.encode(input.getPassword());
        User user = new User(input.getEmail(), encodedPassword, input.getFirstName(), input.getLastName(), null);
        user.setPassword(encodedPassword);
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
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
        String subject = "Account verification";
        String verificationCode = "VERIFICATION CODE: " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try{
            emailService.sendVerificationEmail(user.getEmail(), subject,htmlMessage);
        }catch (ResendException e){
            e.printStackTrace();
        }
    }
    private String generateVerificationCode(){
        Random random = new Random();
        int code = random.nextInt(900000);
        return String.valueOf(code);
    }
}
