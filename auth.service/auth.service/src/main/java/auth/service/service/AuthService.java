package auth.service.service;

import auth.service.dto.ApiResponseDto;
import auth.service.dto.UserDto;
import auth.service.entity.User;
import auth.service.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Service

public class AuthService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // SIGNUP ---------------------------------------------------------------

    public ApiResponseDto<String> signup(UserDto userDto){
        System.out.println("signup API HIT");

        // if exist by email
        if(userRepository.existsByEmail(userDto.getEmail())){
            ApiResponseDto<String> response = new ApiResponseDto<>();
            response.setData("registration failed");
            response.setMessage("user with this email exist - login pls");
            response.setStatus(400);   // ✅ FIXED
            return response;
        }

        // if exist by username
        if(userRepository.existsByUsername(userDto.getUsername())){
            ApiResponseDto<String> response = new ApiResponseDto<>();
            response.setData("registration failed");
            response.setMessage("user with this username exist - login pls");
            response.setStatus(400);   // ✅ FIXED
            return response;
        }

        // password encrypted
        User user = new User();
        BeanUtils.copyProperties(userDto ,user); // copy raw password from dto to user
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // encoding the password in user

        // FOR ROLE
        user.setRole("ROLE_" + userDto.getRole());  // ✅ FIXED (dynamic role)

        userRepository.save(user); // saving all user details + encrypted pass in database

        // response back
        ApiResponseDto<String> message = new ApiResponseDto<>();
        message.setMessage("sign up - successful");
        message.setData("user registered");
        message.setStatus(201);

        return message;
    }


// LOGIN---------------------------------------------------------------

    @Autowired
    private AuthenticationManager authenticationManager;

    public ResponseEntity<ApiResponseDto<String>> login(UserDto userDto) {
        System.out.println("login API HIT");

        ApiResponseDto<String> response = new ApiResponseDto<>();

        // manager send token to provider --to authenticate with db data
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword());

        try {
            Authentication authenticate = authenticationManager.authenticate(token);

            if(authenticate.isAuthenticated()){

                String username = userDto.getUsername(); // correct way

                String role = authenticate.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority();

                String jwtToken = jwtService.generateToken(username, role);

                response.setMessage("user authenticated - login successful");
                response.setData(jwtToken); // ✅ RETURN TOKEN HERE
                response.setStatus(200);

                return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
            }

        } catch (Exception e) {
            // ✅ FIXED (no crash)
            response.setMessage("Invalid username or password");
            response.setData("unauthorized");
            response.setStatus(401);

            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
        }

        response.setMessage("login failed");
        response.setData("unauthorized");
        response.setStatus(400);

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

}