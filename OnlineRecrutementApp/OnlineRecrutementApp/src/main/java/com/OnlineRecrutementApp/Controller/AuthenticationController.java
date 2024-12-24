package com.OnlineRecrutementApp.Controller;

import com.OnlineRecrutementApp.Entity.AppUsers;
import com.OnlineRecrutementApp.Repository.AppUserRepository;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(("/api/auth"))
public class AuthenticationController {
    private final AppUserRepository appUserRepository;

    public AuthenticationController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);;

    @PostMapping("/signup")
    public AppUsers createUser(@RequestBody AppUsers user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return appUserRepository.save(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AppUsers loginRequest) {
        try {
            // Fetch user details
            AppUsers user = appUserRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Manually validate password
            boolean isPasswordValid = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
            if (!isPasswordValid) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid email or password"));
            }

            // Proceed with Spring Security authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String token = Jwts.builder()
                    .setSubject(user.getEmail())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day validity
                    .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                    .compact();

            // Prepare response map with the token
            Map<String, String> response = new HashMap<>();
            response.put("token", "Bearer " + token);  // Add "Bearer" prefix to the token

            // Respond with the token as JSON
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid email or password"));
        }
    }


}
