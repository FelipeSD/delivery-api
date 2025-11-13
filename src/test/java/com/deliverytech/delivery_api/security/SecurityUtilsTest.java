package com.deliverytech.delivery_api.security;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.deliverytech.delivery_api.auth.model.Role;
import com.deliverytech.delivery_api.auth.model.Usuario;
import com.deliverytech.delivery_api.common.security.SecurityUtils;

class SecurityUtilsTest {

  private Usuario usuario;

  @BeforeEach
  void setUp() {
    usuario = new Usuario("cliente@email.com", "123", "Cliente", Role.CLIENTE);
    usuario.setId(5L);
    var auth = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(auth);
  }

  @Test
  void deveRetornarUsuarioAtual() {
    var current = SecurityUtils.getCurrentUser();
    assertEquals(usuario.getEmail(), current.getEmail());
    assertEquals(5L, SecurityUtils.getCurrentUserId());
    assertEquals("CLIENTE", SecurityUtils.getCurrentUserRole());
  }

  @Test
  void deveValidarRoles() {
    assertTrue(SecurityUtils.isCliente());
    assertFalse(SecurityUtils.isAdmin());
  }

  @Test
  void deveLancarErroQuandoNaoAutenticado() {
    SecurityContextHolder.clearContext();
    assertThrows(RuntimeException.class, SecurityUtils::getCurrentUser);
  }
}
