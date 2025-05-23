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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        try {
            return ResponseEntity.ok(userService.register(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try{
            String identifier = request.getIdentifier();
            String password = request.getPassword();
            String loginType = request.getType();  // either "username" or "phone"
            LoginStrategy strategy;
            Map<String,String> token = userService.verify(identifier, password, loginType);
            return token ==null?
                    ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials") :
                    ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
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
        try{
           Map<String,String> token= userService.refreshToken(request.get("refreshToken"));
        return token ==null?
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials") :
                ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader UUID userId, @RequestHeader("Authorization") String token){
        try {
            userService.logout(userId, token);
            return ResponseEntity.ok("Logged out successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try{
            String identifier = request.get("identifier");
            String loginType = request.get("type");  // either "username" or "phone"
            try {
                userService.forgotPassword(identifier, loginType);
                return ResponseEntity.ok("Password reset link sent successfully");
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            String newPassword = request.get("newPassword");
            try {
                userService.resetPassword(token, newPassword);
                return ResponseEntity.ok("Password reset successfully");
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping
    public String hello(@RequestHeader UUID userId){

        return userId.toString();
    }
    @GetMapping("/allUsers")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

}
