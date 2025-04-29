//package com.example.gateway.clients;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import java.util.Map;
//
//@FeignClient(name = "auth-service", url = "http://localhost:8080")
//public interface AuthClient {
//    @PostMapping("/auth/validateToken")
//    ResponseEntity<Map<String, Object>> validateToken(@RequestBody String token);
//}