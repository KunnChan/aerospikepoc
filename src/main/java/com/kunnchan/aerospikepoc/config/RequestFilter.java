package com.kunnchan.aerospikepoc.config;
/*
 * Created by kunnchan on 13/11/2021
 * package :  com.kunnchan.aerospikepoc.config
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Configuration
public class RequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String path = httpRequest.getRequestURI();

        if(!path.startsWith("/api")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String param1 = httpRequest.getHeader("missingparam1");
        String param2 = httpRequest.getHeader("missingparam2");
        if(!StringUtils.hasText(param1) || !StringUtils.hasText(param2)){
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            String error = "Missing Headers";
            resp.reset();
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            servletResponse.setContentLength(error.length());
            servletResponse.getWriter().write(error);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }
}
