package com.quizchamp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("admin-reset")
public class AdminPasswordResetRunner implements CommandLineRunner {

    private final UserAccountService userAccountService;

    public AdminPasswordResetRunner(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public void run(String... args) {
        String email = System.getenv("QUIZCHAMP_ADMIN_EMAIL");
        String password = System.getenv("QUIZCHAMP_ADMIN_PASSWORD");

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Set QUIZCHAMP_ADMIN_EMAIL and QUIZCHAMP_ADMIN_PASSWORD."
            );
        }

        userAccountService.resetAdminPassword(email, password);
        System.out.println("Admin password reset successfully.");
    }
}