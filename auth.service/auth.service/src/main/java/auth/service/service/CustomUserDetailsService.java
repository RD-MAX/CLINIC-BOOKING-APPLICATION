package auth.service.service;

import auth.service.entity.User;
import auth.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
//abstract method to be overridden
//interact with db
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User userInfo = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails
                .User(
                userInfo.getUsername(),
                userInfo.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(userInfo.getRole()))
        );
    }
}