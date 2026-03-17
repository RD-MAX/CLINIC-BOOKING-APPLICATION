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
@RequestMapping("/api/v1/authservice")
public class AuthService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

// SIGNUP ---------------------------------------------------------------

    public ApiResponseDto<String > signup(UserDto userDto){
        System.out.println("signup API HIT");
        // if exist by email
        if(userRepository.existsByEmail((userDto.getEmail())){
            ApiResponseDto response= new ApiResponseDto<>();
            response.setData("registeration failed ");
            response.setMessage("user with this email  exist-  login  pls");
            response.setStatus(500);

            return response;
        }
// if exist by username
        if(userRepository.existsByUsername((userDto.getUsername())){
            ApiResponseDto response= new ApiResponseDto<>();
            response.setData("registeration failed ");
            response.setMessage("user with this username  exist-login pls");
            response.setStatus(500);

            return response;
        }

        // password encrypted
        User user=new User();
        BeanUtils.copyProperties(userDto ,user);// copy raw password from dto to user
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));// encoding the password in user

        // FOR ROLE
        user.setRole("ROLE_ADMIN");
        userRepository.save(user);// saving all user details +encrypted pass in database

//response back
        ApiResponseDto<String> message =new ApiResponseDto<>();
        message.setMessage(" sign up - successful");
        message.setData("user registered");
        message.setStatus(201);
        return  message;

    }

// LOGIN---------------------------------------------------------------
    @Autowired
    private AuthenticationManager authenticationManager;

    public ResponseEntity<ApiResponseDto<String>> login(UserDto userDto) {
        System.out.println("login API HIT");

        ApiResponseDto response=new ApiResponseDto<>();
// manager send  toke to provide --to authenticate with db data
        UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken<>();
       try{
        Authentication authenticate= authenticationManager.authenticate(token);
if(authenticate.isAuthenticated()){
    response.setMessage("user authenticated - login successfull");
    response.setMessage("");
    response.setStatus(200);

    return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
}
    } catch (Exception e) {
           throw new RuntimeException(e);
       }
 response.setMessage("login failed");
       response.setData("unauthorized");
       response.setStatus(400);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
}}
