package org.example.server.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    /*
    This is where we configure the security required for our endpoints and set up our app to serve as
    an OAuth2 Resource Server, using JWT validation.
    */
        return http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/public").permitAll()
                        .requestMatchers("/user").authenticated()
                        .requestMatchers("/upload").authenticated()
                )
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)  // ADD THIS LINE
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(withDefaults())
                )
                .build();
    }
}