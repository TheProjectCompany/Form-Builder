package org.tpc.form_builder.security.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.tpc.form_builder.models.User;
import org.tpc.form_builder.security.JWTUtils;
import org.tpc.form_builder.service.impl.CustomUserDetailService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j2
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;
    private final CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        String token, userPublicId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            if (!jwtUtils.validateToken(token)) throw new ServletException("Invalid JWT token");
            userPublicId = authHeader.substring(7);
        }

        if (userPublicId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User userDetails = customUserDetailService.loadUserByUsername(userPublicId);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("Authenticated user: {}", userPublicId);
        }

        filterChain.doFilter(request, response);
     }
}
