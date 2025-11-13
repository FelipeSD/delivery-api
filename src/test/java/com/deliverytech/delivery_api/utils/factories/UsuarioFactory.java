package com.deliverytech.delivery_api.utils.factories;

import com.deliverytech.delivery_api.auth.model.Role;
import com.deliverytech.delivery_api.auth.model.Usuario;
import com.deliverytech.delivery_api.restaurante.model.Restaurante;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioFactory {
  @Autowired protected PasswordEncoder passwordEncoder;

  public Usuario criarUsuario(String email, String senha, String nome, Role role, Restaurante restaurante)  {
    Usuario u = new Usuario();
    u.setEmail(email);
    u.setSenha(passwordEncoder.encode(senha));
    u.setNome(nome);
    u.setRole(role);
    u.setAtivo(true);
    u.setRestaurante(restaurante);
    return u;
  }

  public Usuario criarUsuarioRestaurante(Restaurante restaurante) {
    return criarUsuario(
      "restaurante@email.com",
      "123456",
      "Restaurante",
      Role.RESTAURANTE,
      restaurante
    );
  }

  public Usuario criarUsuarioCliente() {
    return criarUsuario(
      "cliente@email.com",
      "123456",
      "Cliente",
      Role.CLIENTE,
      null
    );
  }

  public Usuario criarUsuarioAdmin() {
    return criarUsuario(
      "admin@email.com",
      "123456",
      "Cliente",
      Role.ADMIN,
      null
    );
  }
}
