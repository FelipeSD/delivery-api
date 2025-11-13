package com.deliverytech.delivery_api.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.deliverytech.delivery_api.auth.model.Role;
import com.deliverytech.delivery_api.auth.model.Usuario;
import com.deliverytech.delivery_api.common.security.JwtUtil;

class JwtUtilTest {

  private JwtUtil jwtUtil;
  private Usuario usuario;

  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil();
    ReflectionTestUtils.setField(jwtUtil, "secret", "mysecretkeymysecretkeymysecretkeymysecretkey");
    ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L); // 1 hora

    usuario = new Usuario("user@email.com", "123", "User Test", Role.CLIENTE);
    usuario.setId(10L);
  }

  @Test
  void deveGerarETestarTokenComSucesso() {
    String token = jwtUtil.generateToken(usuario);

    assertNotNull(token);
    assertEquals("user@email.com", jwtUtil.extractUsername(token));
    assertEquals(10L, jwtUtil.extractUserId(token));
    assertEquals("CLIENTE", jwtUtil.extractRole(token));
    assertFalse(jwtUtil.isTokenExpired(token));
    assertTrue(jwtUtil.validateToken(token, usuario));
  }

  @Test
  void deveDetectarTokenExpirado() throws InterruptedException {
    ReflectionTestUtils.setField(jwtUtil, "expiration", 1L); // expira em 1ms
    String token = jwtUtil.generateToken(usuario);

    // Espera 5ms
    Thread.sleep(5);

    assertThrows(io.jsonwebtoken.ExpiredJwtException.class, () -> {
      jwtUtil.isTokenExpired(token);
    });
  }
}
