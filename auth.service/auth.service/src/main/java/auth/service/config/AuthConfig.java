package auth.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.authorization.SingleResultAuthorizationManager.permitAll;

@Configuration
public class AuthConfig {

    @Bean
public SecurityFilterChain authConfig(HttpSecurity http )throws Exception
{
    //incoming requests
    http.authorizeHttpRequests(req ->{
        req.requestMatchers("api/v1/auth/**").permitAll().anyRequest().authenticated();
});
 return http.build();
}


    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config){


        return new config.getAuthenticationManager();// in controller
}

}
