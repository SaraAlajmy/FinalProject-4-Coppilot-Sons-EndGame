package com.example.controllers;

import com.example.models.User;
import com.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<User> getUserById(@RequestHeader UUID userId) {
        try {
            User user = userService.getUserById(userId);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    @GetMapping("/getAll")
//    public ResponseEntity<List<User>> getAllUsers() {
//        try {
//            return ResponseEntity.ok(userService.getAllUsers());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }

    // TODO: when adding get userbyid endpoint remember it returns null if user not found

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestHeader UUID userId, @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(userId, user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestHeader UUID userId, @RequestHeader("Authorization") String token) {
        try {
            userService.deleteUser(userId, token);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user: " + e.getMessage());
        }
    }
    //delete all users
//    @DeleteMapping("/deleteAll")
//    public String deleteAllUsers() {
//        try {
//            userService.deleteAllUsers();
//            return "All users deleted successfully";
//        } catch (Exception e) {
//            return "Error deleting users: " + e.getMessage();
//        }
//    }

    @GetMapping("/areBlocking/{firstUser}/{secondUser}")
    public ResponseEntity<Boolean> areBlocking(@PathVariable UUID firstUser, @PathVariable UUID secondUser){
        try{
            boolean areBlocking = userService.areBlocking(firstUser, secondUser);
            return ResponseEntity.ok(areBlocking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @GetMapping("/getUserEmail")
    public ResponseEntity<String> getUserEmail(@RequestHeader UUID userId){
        try{
            String email = userService.getUserEmail(userId);
            return ResponseEntity.ok(email);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error getting user email: " + e.getMessage());
        }
    }


    @PostMapping("/seed")
    public ResponseEntity<List<User>> seedUsers() {
        try {
            List<User> seededUsers = userService.seedUsers();
            return ResponseEntity.ok(seededUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
  
    @GetMapping("/bulk-get-ids-by-usernames")
    public ResponseEntity<?> getUsersIdsByUsernames(@RequestParam List<String> usernames){
        try{
            var result = userService.getUsersIdsByUsernames(usernames);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error getting user usernames: " + e.getMessage());
        }
    }

}
