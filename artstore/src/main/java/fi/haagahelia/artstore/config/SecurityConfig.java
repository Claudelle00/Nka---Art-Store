package fi.haagahelia.artstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/home", "/CSS/**", "/css/**",
                    "/signup", "/saveuser"
                ).permitAll()
                .requestMatchers("/profile").authenticated()
                .requestMatchers("/add", "/save", "/edit/**", "/delete/**")
                    .hasAuthority("ADMIN")
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll())
            .logout(logout -> logout
                .logoutSuccessUrl("/home")
                .permitAll());

        return http.build();
    }
    @Bean
PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
}