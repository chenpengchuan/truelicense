package de.schlichtherle.jersey.rest;

import org.springframework.stereotype.Component;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/truelicense")
@Component
public class TrueLicenseResource {
    private String FilePath = "E:\\project\\truelicense\\src\\main\\config\\verify.properties";

    @Path("/verify")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> licenseVerify() {
        Map<String, Object> map = new HashMap<String, Object>();

        return map;
    }
}
