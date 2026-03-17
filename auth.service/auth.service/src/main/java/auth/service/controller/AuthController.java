package auth.service.controller;

import auth.service.dto.ApiResponseDto;
import auth.service.dto.UserDto;
import auth.service.entity.User;
import auth.service.repository.UserRepository;
import auth.service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<String>> signup(@RequestBody UserDto userDto) {
        ApiResponseDto<String> response = authService.signup(userDto);
return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));

    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<String>> login(@RequestBody UserDto userDto) {

        System.out.println("login API HIT");

        ApiResponseDto<String> response= authService.login(userDto).getBody();
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(response.getStatus()));
    }


    @Autowired
    private UserRepository userRepository;

    @GetMapping("/get-user")
    public User getUserByUsername(@RequestParam ("username") String username)
    {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}