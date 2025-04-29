package com.example.controllers;

import com.example.models.User;
import com.example.services.UserService;
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
    public String login(@RequestBody User user) {
        return userService.verify(user,false);

    }
    @PostMapping("/refreshToken")
    public String refreshToken(@RequestBody User user) {
        return userService.verify(user,true);

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


}
