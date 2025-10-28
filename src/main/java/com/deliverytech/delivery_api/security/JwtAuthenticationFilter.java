package com.deliverytech.delivery_api.security;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.deliverytech.delivery_api.config.PublicEndpoints;
import com.deliverytech.delivery_api.services.AuthService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
  private static final String BEARER_PREFIX = "Bearer ";

  private final AuthService authService;
  private final JwtUtil jwtUtil;

  public JwtAuthenticationFilter(@Lazy AuthService authService, JwtUtil jwtUtil) {
    this.authService = authService;
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    String path = request.getRequestURI();
    return Arrays.stream(PublicEndpoints.ENDPOINTS).anyMatch(path::startsWith);
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    // 🚫 Sem token → segue o fluxo sem autenticação
    if (header == null || !header.startsWith(BEARER_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.substring(BEARER_PREFIX.length());

    try {
      // 🔍 Extrai usuário
      String username = jwtUtil.extractUsername(token);

      // 🔒 Se não há autenticação ainda, tenta validar o token
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = authService.loadUserByUsername(username);

        if (jwtUtil.validateToken(token, userDetails)) {
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
              userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }

      filterChain.doFilter(request, response);

    } catch (ExpiredJwtException e) {
      handleJwtError(response, HttpStatus.UNAUTHORIZED, "Token expirado. Faça login novamente.");
      log.warn("Token expirado: {}", e.getMessage());
    } catch (MalformedJwtException | UnsupportedJwtException e) {
      handleJwtError(response, HttpStatus.UNAUTHORIZED, "Token inválido ou malformado.");
      log.warn("Token inválido: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      handleJwtError(response, HttpStatus.BAD_REQUEST, "Token ausente ou inválido.");
      log.error("Erro ao processar token: {}", e.getMessage());
    }
  }

  /**
   * 🧾 Retorna um JSON padronizado em caso de erro JWT
   */
  private void handleJwtError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
    response.setStatus(status.value());
    response.setContentType("application/json");
    response.getWriter().write(
        String.format("{\"success\": false, \"message\": \"%s\"}", message));
  }
}
