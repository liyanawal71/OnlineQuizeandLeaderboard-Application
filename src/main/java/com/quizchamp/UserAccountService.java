
package com.quizchamp;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserAccountService {

    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(
            UserAccountRepository repository,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccount registerStudent(
            String fullName, String email, String password) {

        if (fullName == null || fullName.isBlank()
                || fullName.trim().length() > 100) {
            throw new IllegalArgumentException("Enter a valid full name.");
        }

        String normalizedEmail = normalizeEmail(email);

        if (password == null || password.length() < 8
                || password.length() > 72) {
            throw new IllegalArgumentException(
                    "Password must contain 8–72 characters.");
        }

        if (repository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists.");
        }

        UserAccount account = new UserAccount();
        account.setFullName(fullName.trim());
        account.setEmail(normalizedEmail);
        account.setPasswordHash(passwordEncoder.encode(password));

        // Public registration can ONLY create student accounts.
        account.setRole("STUDENT");

        return repository.save(account);
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()
                || email.length() > 190
                || !email.trim().matches(
                    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }
    @Transactional
public UserAccount createAdmin(String fullName, String email, String password) {

    if (repository.existsByRole("ADMIN")) {
        throw new IllegalStateException(
                "An administrator account already exists."
        );
    }

    String normalizedEmail = normalizeEmail(email);

    if (repository.existsByEmail(normalizedEmail)) {
        throw new IllegalArgumentException(
                "An account with this email already exists."
        );
    }

    if (fullName == null || fullName.isBlank()
            || fullName.trim().length() > 100) {
        throw new IllegalArgumentException("Enter a valid full name.");
    }

    if (password == null || password.length() < 12 || password.length() > 72) {
        throw new IllegalArgumentException(
                "Admin password must contain 12–72 characters."
        );
    }

    UserAccount account = new UserAccount();
    account.setFullName(fullName.trim());
    account.setEmail(normalizedEmail);
    account.setPasswordHash(passwordEncoder.encode(password));
    account.setRole("ADMIN");

    return repository.save(account);
}
    @Transactional
    public void resetAdminPassword(String email, String newPassword) {
        String normalizedEmail = normalizeEmail(email);

        UserAccount account = repository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Admin account not found."));

        if (!"ADMIN".equals(account.getRole())) {
            throw new IllegalArgumentException(
                    "The specified account is not an administrator.");
        }

        if (newPassword == null
                || newPassword.length() < 12
                || newPassword.length() > 72) {
            throw new IllegalArgumentException(
                    "Password must contain 12–72 characters.");
        }

        account.setPasswordHash(passwordEncoder.encode(newPassword));
        repository.save(account);
    }
}
