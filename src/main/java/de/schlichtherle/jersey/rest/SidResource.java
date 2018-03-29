package de.schlichtherle.jersey.rest;

import de.schlichtherle.util.SidUtils;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/sid")
@Component
public class SidResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String,Object> getSid() {
        Map<String,Object> map = new HashMap<String,Object>();
        String sid = SidUtils.getSid();
        map.put("sid",sid);
        return map;
    }
}