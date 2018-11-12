package de.schlichtherle.app.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RequestMapping("/truelicense")
@Controller
@RestController
public class TrueLicenseResource {
    private String FilePath = "E:\\project\\truelicense\\src\\main\\config\\verify.properties";

    @GetMapping("/verify")
    public Map<String, Object> licenseVerify() {
        Map<String, Object> map = new HashMap<String, Object>();

        return map;
    }
}
