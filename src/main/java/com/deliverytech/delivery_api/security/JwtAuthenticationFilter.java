package com.deliverytech.delivery_api.security;

import java.io.IOException;

import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.deliverytech.delivery_api.services.AuthService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final AuthService authService;
  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(@Lazy AuthService authService, JwtUtil jwtUtil) {
    this.authService = authService;
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    final String requestTokenHeader = request.getHeader("Authorization");

    String username = null;
    String jwtToken = null;

    if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
      jwtToken = requestTokenHeader.substring(7);
      try {
        username = jwtUtil.extractUsername(jwtToken);
      } catch (IllegalArgumentException e) {
        logger.error("Não foi possível obter o JWT Token", e);
      } catch (ExpiredJwtException e) {
        logger.error("JWT Token expirado", e);
      } catch (MalformedJwtException e) {
        logger.error("JWT Token malformado", e);
      }
    } else {
      logger.warn("JWT Token não começa com Bearer String");
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = authService.loadUserByUsername(username);

      if (jwtUtil.validateToken(jwtToken, userDetails)) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
            userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    filterChain.doFilter(request, response);
  }
}