package auth.service.filter;

import auth.service.service.CustomUserDetailsService;
import auth.service.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

//incoming re +token comes here
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // to see bearer token displayed on console
        System.out.println("JwtFilter HIT");
        System.out.println("Authorization header = " + request.getHeader("Authorization"));

        String authHeader=request.getHeader("Authorization");
        if(authHeader!=null&& authHeader.startsWith("Bearer ")){
            String jwt= authHeader.substring(7);// remove Bearer_from token

           // use token to get username
           String username =jwtService.validateTokenAndRetrieveSubject(jwt);
           if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
               var userDetails= customUserDetailsService.loadUserByUsername(username);

               String role = jwtService.extractRole(jwt);

               var authToken = new UsernamePasswordAuthenticationToken(
                       username,
                       null,
                       java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role))
               );
               SecurityContextHolder.getContext().setAuthentication(authToken);
           }


        }
filterChain.doFilter(request,response);
    }
}
