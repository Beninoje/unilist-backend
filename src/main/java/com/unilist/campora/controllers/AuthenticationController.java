package com.unilist.campora.controllers;

import com.unilist.campora.dto.LoginUserDto;
import com.unilist.campora.dto.RegisterUserDto;
import com.unilist.campora.dto.VerifyUserDto;
import com.unilist.campora.model.Listing;
import com.unilist.campora.model.User;
import com.unilist.campora.repository.UserRepository;
import com.unilist.campora.responses.LoginResponse;
import com.unilist.campora.responses.RegisterResponse;
import com.unilist.campora.services.AuthenticationService;
import com.unilist.campora.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
