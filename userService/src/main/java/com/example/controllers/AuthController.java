package com.example.controllers;

import com.example.models.LoginRequest;
import com.example.models.User;
import com.example.services.UserService;
import com.example.services.loginStrategies.LoginStrategy;
import com.example.services.loginStrategies.PhoneLoginStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public User register(@RequestBody User user){
        return userService.register(user);


    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String identifier = request.getIdentifier();
        String password = request.getPassword();
        String loginType = request.getType();  // either "username" or "phone"
        LoginStrategy strategy;
        Map<String,String> token = userService.verify(identifier, password, loginType);
        return token ==null?
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials") :
                ResponseEntity.ok(token);
    }

    @PostMapping("/validateToken")
    public ResponseEntity<?>  validateToken(@RequestHeader("Authorization") String token) {
        try {
            Map<String, Object> claims = userService.validateToken(token);
            return ResponseEntity.ok(claims);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
           Map<String,String> token= userService.refreshToken(request.get("refreshToken"));
        return token ==null?
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials") :
                ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader String userId) {
        try {
            userService.logout(userId);
            return ResponseEntity.ok("Logged out successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


}
