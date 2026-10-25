package com.quizchamp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

private final UserAccountService userAccountService;
private final AuthenticationManager authenticationManager;

public AuthController(
        UserAccountService userAccountService,
        AuthenticationManager authenticationManager) {

    this.userAccountService = userAccountService;
    this.authenticationManager = authenticationManager;
}

// STUDENT REGISTRATION
@PostMapping("/register")
public ResponseEntity<?> register(
        @RequestBody RegisterRequest request) {

    try {
        UserAccount account = userAccountService.registerStudent(
                request.fullName(),
                request.email(),
                request.password()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Student account created successfully.",
                        "id", account.getId(),
                        "fullName", account.getFullName(),
                        "email", account.getEmail(),
                        "role", account.getRole()
                ));

    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
    }
}

// STUDENT / ADMIN LOGIN
@PostMapping("/login")
public ResponseEntity<?> login(
        @RequestBody LoginRequest request,
        HttpServletRequest httpRequest,
        HttpServletResponse httpResponse) {

    try {
        if (request.email() == null
                || request.password() == null
                || request.email().isBlank()
                || request.password().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "Email and password are required."
                    ));
        }

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email().trim()
                                        .toLowerCase(Locale.ROOT),
                                request.password()
                        )
                );

        SecurityContext context =
                SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSessionSecurityContextRepository contextRepository =
                new HttpSessionSecurityContextRepository();

        contextRepository.saveContext(
                context,
                httpRequest,
                httpResponse
        );

        return ResponseEntity.ok(
                Map.of(
                        "message", "Login successful.",
                        "email", authentication.getName(),
                        "role", authentication.getAuthorities()
                                .stream()
                                .map(authority -> authority.getAuthority())
                                .toList()
                )
        );

    } catch (AuthenticationException e) {

        SecurityContextHolder.clearContext();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "error", "Invalid email or password."
                ));
    }
}

// CHECK WHETHER THE LOGIN SESSION PERSISTS
@org.springframework.web.bind.annotation.GetMapping("/session-check")
public ResponseEntity<?> checkSession(
        jakarta.servlet.http.HttpServletRequest request,
        org.springframework.security.core.Authentication authentication) {

    if (authentication == null
            || !authentication.isAuthenticated()
            || authentication instanceof
               org.springframework.security.authentication.AnonymousAuthenticationToken) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("loggedIn", false));
    }

    return ResponseEntity.ok(
            Map.of(
                    "loggedIn", true,
                    "email", authentication.getName(),
                    "sessionExists", request.getSession(false) != null
            )
    );
}
// REQUEST DATA FOR REGISTRATION
public record RegisterRequest(
        String fullName,
        String email,
        String password
) {}

// REQUEST DATA FOR LOGIN
public record LoginRequest(
        String email,
        String password
) {}


}
