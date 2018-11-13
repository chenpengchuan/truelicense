package de.schlichtherle.app.rest;

import de.schlichtherle.app.entity.User;
import de.schlichtherle.app.service.JwtTokenHandler;
import org.jboss.logging.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/auth")
@Controller
@RestController
@CrossOrigin
public class LoginResource {
    public static String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

    @Autowired
    private JwtTokenHandler jwtTokenHandler;

    @PostMapping("/login")
    @CrossOrigin
    public ResponseEntity logIn(@Param User loginUser) {
        Map<String, Object> map = new HashMap<String, Object>();
        String name = loginUser.getUserName();
        String pwd = loginUser.getPassWord();
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(pwd)) {
            map.put("err", "userName/passWord can not be null");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.BAD_REQUEST);
        }
        if (!jwtTokenHandler.userName.equals(name) || !jwtTokenHandler.passWord.equals(pwd)) {
            map.put("err", "wrong userName/passWord");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.BAD_REQUEST);
        } else {
            HttpHeaders hdader = new HttpHeaders();
            hdader.add(AUTH_HEADER_NAME, jwtTokenHandler.createTokenForUser(loginUser));
            map.put("loginUser:", name);
            return new ResponseEntity<Map<String, Object>>(map,hdader, HttpStatus.OK);
        }
    }

}