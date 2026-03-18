package auth.service.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

private static final String SECRET_KEY="secret12345";

private static final Long EXPIRATION_TIME= 86400000L;

//token generation using role and username
public String generateToken(String username,String role){

    return JWT.create()
            .withSubject(username)
            .withClaim("role",role)
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
            .sign(Algorithm.HMAC256(SECRET_KEY));

}

// validate token -same algorithm used
    public String validateTokenAndRetrieveSubject(String token){

    return JWT.require(Algorithm.HMAC256(SECRET_KEY))
            .build()
            .verify(token)// if secret key match decode
            .getSubject();// get username back and pass to filter
    }


    public String extractRole(String token) {
        return JWT.require(Algorithm.HMAC256(SECRET_KEY))
                .build()
                .verify(token)
                .getClaim("role")
                .asString();
    }
}
