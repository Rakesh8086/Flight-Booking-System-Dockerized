package com.spring.login.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import com.spring.login.security.services.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private static final String SERVICE_ID_PREFIX = "/authentication-service";
    private static final String PUBLIC_AUTH_PATTERN = "/api/auth/**";
    private static final String TEST_PUBLIC_PATTERN = "/api/test/all";
    private static final String CHANGE_PASSWORD_PATH = "/api/test/change/password";

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
    	System.out.println("************JWT shouldNotFilter URI = " + request.getRequestURI());
    	// return false; 
        String uri = normalizePath(request.getRequestURI());
        return pathMatcher.match(PUBLIC_AUTH_PATTERN, uri)
            || pathMatcher.match(TEST_PUBLIC_PATTERN, uri)
            || pathMatcher.match(CHANGE_PASSWORD_PATH, uri);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = jwtUtils.getJwtFromCookies(request);
            if(jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } 
        catch (Exception e) {
            logger.error("JWT authentication failed", e);
        }

        filterChain.doFilter(request, response);
    }

    private String normalizePath(String requestUri) {
        String path = requestUri;
        if(path.startsWith(SERVICE_ID_PREFIX)) {
            path = path.substring(SERVICE_ID_PREFIX.length());
        }
        if(!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

    private String parseJwt(HttpServletRequest request) {
		String jwt = jwtUtils.getJwtFromCookies(request);
		return jwt;
	}
}