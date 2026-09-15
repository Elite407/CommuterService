package com.elite.rideplatform.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/passenger/login")
    public ResponseEntity<AuthResponse> authenticatePassenger(@RequestBody AuthRequest request) {
        return authenticateAndGenerateToken(request, "ROLE_PASSENGER");
    }

    @PostMapping("/partner/login")
    public ResponseEntity<AuthResponse> authenticatePartner(@RequestBody AuthRequest request) {
        return authenticateAndGenerateToken(request, "ROLE_PARTNER");
    }

    private ResponseEntity<AuthResponse> authenticateAndGenerateToken(AuthRequest request, String role) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        // Ensure the authenticated user has the requested role
        boolean hasRole = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
                
        if (!hasRole) {
            return ResponseEntity.status(403).build();
        }

        final String jwt = jwtUtil.generateToken(userDetails, role);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
