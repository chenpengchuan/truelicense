package de.schlichtherle.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component("cntexFilter")
public class ContexFilter extends GenericFilterBean {

    @Autowired
    private JwtTokenHandler jwtTokenHandler;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("X-AUTH-TOKEN");
        if(httpServletRequest.getRequestURI().equals("/auth/login")){
            chain.doFilter(request, response);
            return;
        }
        if (token == null) {
            token = httpServletRequest.getParameter("X-AUTH-TOKEN");
        }
        if (StringUtils.isEmpty(token)) {
            throw new IllegalArgumentException("X-AUTH-TOKEN can not be null in httpServletRequest's head");
        } else {
            jwtTokenHandler.parseUserFromToken(token);
            chain.doFilter(request, response);
        }
    }
}
