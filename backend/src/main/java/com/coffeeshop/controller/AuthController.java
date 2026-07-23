package com.coffeeshop.controller;

import com.coffeeshop.dto.LoginRequest;
import com.coffeeshop.dto.LoginResponse;
import com.coffeeshop.model.User;
import com.coffeeshop.repository.UserRepository;
import com.coffeeshop.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "Endpoints for user login and token generation")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "Login to get JWT token")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
        
        User user = userOpt.get();
        // In a real application, you should use password encoders (like BCrypt).
        // Since the schema just specifies password_hash but the current implementation 
        // might be using plaintext or a simple hash, we compare them directly for now.
        if (!user.getPasswordHash().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new LoginResponse(token, user.getRole().name(), user.getName()));
    }
}
