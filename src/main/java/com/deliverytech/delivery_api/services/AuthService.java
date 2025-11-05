package com.deliverytech.delivery_api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.deliverytech.delivery_api.dtos.RegisterRequestDTO;
import com.deliverytech.delivery_api.entities.Restaurante;
import com.deliverytech.delivery_api.entities.Usuario;
import com.deliverytech.delivery_api.repositories.UsuarioRepository;

@Service
public class AuthService implements UserDetailsService {

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public Usuario loadUserByUsername(String email) throws UsernameNotFoundException {
    return usuarioRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
  }

  public boolean existsByEmail(String email) {
    return usuarioRepository.existsByEmail(email);
  }

  public Usuario criarUsuario(RegisterRequestDTO request) {
    Usuario usuario = new Usuario();
    usuario.setNome(request.getNome());
    usuario.setEmail(request.getEmail());
    usuario.setSenha(passwordEncoder.encode(request.getSenha()));
    usuario.setRole(request.getRole());

    if (request.getRestauranteId() != null) {
      Restaurante restaurante = new Restaurante();
      restaurante.setId(request.getRestauranteId());
      usuario.setRestaurante(restaurante);
    }

    return usuarioRepository.save(usuario);
  }

  public Usuario buscarPorId(Long id) {
    return usuarioRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
  }

  public Usuario buscarPorEmail(String email) {
    return usuarioRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
  }
}