
package com.quizchamp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("admin-setup")
public class AdminSetupRunner implements CommandLineRunner {

    private final UserAccountRepository repository;
    private final UserAccountService userAccountService;

    public AdminSetupRunner(
            UserAccountRepository repository,
            UserAccountService userAccountService) {
        this.repository = repository;
        this.userAccountService = userAccountService;
    }

    @Override
    public void run(String... args) {
        String email = System.getenv("QUIZCHAMP_ADMIN_EMAIL");
        String password = System.getenv("QUIZCHAMP_ADMIN_PASSWORD");
        String fullName = System.getenv("QUIZCHAMP_ADMIN_NAME");

        if (email == null || email.isBlank()
                || password == null || password.isBlank()
                || fullName == null || fullName.isBlank()) {
            throw new IllegalStateException(
                    "Set QUIZCHAMP_ADMIN_EMAIL, QUIZCHAMP_ADMIN_PASSWORD, "
                    + "and QUIZCHAMP_ADMIN_NAME before running admin setup."
            );
        }

        if (repository.existsByRole("ADMIN")) {
            System.out.println(
                    "Admin setup stopped: an ADMIN account already exists."
            );
            return;
        }

        userAccountService.createAdmin(fullName, email, password);

        System.out.println("Admin account created successfully.");
        System.out.println("Email: " + email);
        System.out.println("Do not share your admin password.");
    }
}
