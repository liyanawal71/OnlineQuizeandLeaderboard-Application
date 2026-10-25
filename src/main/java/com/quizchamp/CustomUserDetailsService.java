
package com.quizchamp;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository repository;

    public CustomUserDetailsService(UserAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        UserAccount account = repository.findByEmail(
                email.trim().toLowerCase(java.util.Locale.ROOT)
        ).orElseThrow(() ->
                new UsernameNotFoundException("Invalid email or password.")
        );

        return User.withUsername(account.getEmail())
                .password(account.getPasswordHash())
                .roles(account.getRole())
                .build();
    }
}
