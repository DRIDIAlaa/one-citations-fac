import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF if not needed
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/random-image").permitAll() // Allow access to /random-image
                        .anyRequest().authenticated() // Secure other endpoints
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt()); // Configure OAuth2 JWT if needed
        return http.build();
    }
}
