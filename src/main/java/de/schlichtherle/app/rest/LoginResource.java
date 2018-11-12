package de.schlichtherle.app.rest;

import org.jboss.logging.Param;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/auth")
@Controller
@RestController
public class LoginResource {
    @Value("${server.login.username:admin}")
    private String userName;

    @Value("${server.login.password:inforefiner}")
    private String passWord;

    @PostMapping("/login")
    public ResponseEntity logIn(@Param LoginUser loginUser) {
        Map<String, Object> map = new HashMap<String, Object>();
        String name = loginUser.getUserName();
        String pwd = loginUser.getPassWord();
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(pwd)) {
            map.put("err", "userName/passWord can not be null");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.BAD_REQUEST);
        }
        if (!userName.equals(name) || !passWord.equals(pwd)) {
            map.put("err", "wrong userName/passWord");
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.BAD_REQUEST);
        } else {
            map.put("loginUser:", name);
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
        }
    }


}

class LoginUser {
    private String userName;
    private String passWord;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
}