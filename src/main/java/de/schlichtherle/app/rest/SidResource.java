package de.schlichtherle.app.rest;

import de.schlichtherle.util.SidUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/sid")
@Controller
@RestController
public class SidResource {
    @GetMapping
    public Map<String,Object> getSid() {
        Map<String,Object> map = new HashMap<String,Object>();
        String sid = SidUtils.getSid();
        map.put("sid",sid);
        return map;
    }
}