package com.example.empowher.controller;

import com.example.empowher.model.User;
import com.example.empowher.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class LoginController {
    @Autowired
    private LoginService ls;

    @GetMapping("/")
    public String home() {
        return "App is running!";
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        Optional<User> user= ls.login(credentials);
        if (user.isPresent()) {
            User u = user.get();

            Map<String, Object> response = new HashMap<>();
            response.put("id", u.getId());
            response.put("email", u.getEmail());
            response.put("name", u.getName());
            response.put("role", u.getRole());
            //return ResponseEntity.ok(user.get());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body("Invalid email or password");
    }
}

