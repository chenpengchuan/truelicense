package de.schlichtherle.app.service;

import de.schlichtherle.app.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Date;

@Component("jwtTokenHandler")
public class JwtTokenHandler {

    @Value("${server.login.username:admin}")
    public String userName;

    @Value("${server.login.password:inforefiner}")
    public String passWord;

    @Value("${server.login.token.expiration:900}")
    private int expiration = 0;

    @Value("${server.login.token.key:mysecretkey}")
    private String secret;

    private byte[] secretBytes;

    @PostConstruct
    public void doInit() {

        secretBytes = secret.getBytes();

    }

    public String createTokenForUser(User user) {
        long miss = System.currentTimeMillis();
        String token = Jwts.builder().setSubject(user.getUserName()).setExpiration(new Date(miss + expiration * 1000)).setIssuedAt(new Date(miss))
                .claim("password", user.getPassWord()).signWith(SignatureAlgorithm.HS512, secretBytes)
                .compact();
        return token;
    }

    public User parseUserFromToken(String token) throws IOException {
        Jws<Claims> jwsClaims = Jwts.parser().setSigningKey(secretBytes).parseClaimsJws(token);
//        Jws<Claims> jwsClaims = Jwts.parser().parseClaimsJws(token);
        String name = jwsClaims.getBody().getSubject();
        String password = jwsClaims.getBody().get("password").toString();
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("can not find user/password in jwtToken");
        } else if (!userName.equals(name) || !passWord.equals(password)) {
            throw new IllegalArgumentException("the user/password in jwtToken is error");
        }
        User user = new User();
        if (userName.equals(name) && passWord.equals(password)) {
            user.setUserName(name);
            user.setPassWord(passWord);
        }

        return user;
    }
}
