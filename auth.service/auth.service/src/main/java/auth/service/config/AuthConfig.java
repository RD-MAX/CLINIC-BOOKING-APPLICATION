package auth.service.config;

import auth.service.filter.JwtFilter;
import auth.service.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class AuthConfig {

    @Autowired
    private JwtFilter filter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        String[] publicEndpoints = {
                "/api/v1/auth/**"   // ✅ FIX: allow ALL auth endpoints (login/signup/register)
        };

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(publicEndpoints)
                            .permitAll()
                            .requestMatchers("/api/v1/admin/dashboard")
                             .hasAnyRole("ADMIN")
                            .requestMatchers("/api/v1/doctor/dashboard")
                            .hasAnyRole("ADMIN","DOCTOR")
                            .requestMatchers("/api/v1/patient/dashboard")
                            .hasAnyRole("ADMIN","PATIENT")
                            .anyRequest()
                            .authenticated();
                })
                // ✅ JWT filter runs only AFTER auth endpoints are permitted
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authProvider());

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Autowired
    private CustomUserDetailsService customerUserDetailsService;

    @Bean
    public AuthenticationProvider authProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customerUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

}