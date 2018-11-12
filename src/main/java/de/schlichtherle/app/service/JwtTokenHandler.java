package de.schlichtherle.app.service;

import de.schlichtherle.app.entity.User;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("jwtTokenHandler")
public class JwtTokenHandler {

    @Value("${server.login.token.expiration:900}")
    private int expiration = 0;


    public String createTokenForUser(User user) {
        long miss = System.currentTimeMillis();
        String token = Jwts.builder().setSubject(user.getUserName()).setExpiration(new Date(miss + expiration * 1000)).setIssuedAt(new Date(miss))
                .claim("password", user.getPassWord())
                .compact();
        return token;
    }
}
