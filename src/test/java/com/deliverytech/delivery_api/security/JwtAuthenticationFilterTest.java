package com.deliverytech.delivery_api.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.deliverytech.delivery_api.auth.model.Role;
import com.deliverytech.delivery_api.auth.model.Usuario;
import com.deliverytech.delivery_api.auth.service.AuthService;
import com.deliverytech.delivery_api.common.security.JwtAuthenticationFilter;
import com.deliverytech.delivery_api.common.security.JwtUtil;

class JwtAuthenticationFilterTest {

  private JwtAuthenticationFilter filter;
  private JwtUtil jwtUtil;
  private AuthService authService;
  private Usuario usuario;
  private String token;

  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil();
    ReflectionTestUtils.setField(jwtUtil, "secret", "mysecretkeymysecretkeymysecretkeymysecretkey");
    ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L); // 1h
    authService = mock(AuthService.class);
    filter = new JwtAuthenticationFilter(authService, jwtUtil);

    usuario = new Usuario("user@email.com", "123", "User", Role.CLIENTE);
    usuario.setId(10L);
    token = jwtUtil.generateToken(usuario);
    when(authService.loadUserByUsername("user@email.com")).thenReturn(usuario);

    // Limpa o contexto de autenticação antes de cada teste
    SecurityContextHolder.clearContext();
  }

  @Test
  void devePermitirFluxoSemToken() throws Exception {
    var request = new MockHttpServletRequest();
    var response = new MockHttpServletResponse();
    var chain = spy(new MockFilterChain());

    filter.doFilter(request, response, chain);

    verify(chain, times(1)).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void deveAutenticarComTokenValido() throws Exception {
    var request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);
    var response = new MockHttpServletResponse();
    var chain = spy(new MockFilterChain());

    filter.doFilter(request, response, chain);

    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    assertEquals("user@email.com",
        ((Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail());
    verify(chain, times(1)).doFilter(request, response);
  }

  @Test
  void deveRetornar401ParaTokenExpirado() throws Exception {
    // força expiração
    ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L);
    String expiredToken = jwtUtil.generateToken(usuario);

    var request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + expiredToken);
    var response = new MockHttpServletResponse();

    filter.doFilter(request, response, new MockFilterChain());

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    assertTrue(response.getContentAsString().contains("Token expirado"));
  }

  @Test
  void deveRetornar401ParaTokenInvalido() throws Exception {
    var request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer token_invalido");
    var response = new MockHttpServletResponse();

    filter.doFilter(request, response, new MockFilterChain());

    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    assertTrue(response.getContentAsString().contains("Token inválido"));
  }
}
