package com.unilist.unilist.controllers;

import com.unilist.unilist.dto.LoginUserDto;
import com.unilist.unilist.dto.RegisterUserDto;
import com.unilist.unilist.dto.VerifyUserDto;
import com.unilist.unilist.model.Listing;
import com.unilist.unilist.model.User;
import com.unilist.unilist.repository.UserRepository;
import com.unilist.unilist.responses.LoginResponse;
import com.unilist.unilist.responses.RegisterResponse;
import com.unilist.unilist.services.AuthenticationService;
import com.unilist.unilist.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/auth")
@RestController

public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
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
            LoginResponse loginResponse = new LoginResponse(
                    user.getId(),
                    token,
                    jwtService.getJwtExpiration(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getListings(),
                    user.getFavourites().stream().map(Listing::getId).toList()
                    );
            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }
    @PostMapping("/verify")
    public ResponseEntity<?> verify (@RequestBody VerifyUserDto verifyUserDto) {
        try{
            User user = authenticationService.verifyUser(verifyUserDto);
            String token = jwtService.generateToken(user);
            RegisterResponse registerResponse = new RegisterResponse(
                    token,
                    jwtService.getJwtExpiration(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName()
            );
            return ResponseEntity.ok(registerResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resend (@RequestBody String email) {
        try{
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Account resent!");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
