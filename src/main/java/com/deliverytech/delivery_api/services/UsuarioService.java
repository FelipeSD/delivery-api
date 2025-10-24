package com.deliverytech.delivery_api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.deliverytech.delivery_api.entities.Usuario;
import com.deliverytech.delivery_api.repositories.UsuarioRepository;

@Service
public class UsuarioService
    implements UserDetailsService {

  @Autowired
  private UsuarioRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email)
      throws UsernameNotFoundException {

    Usuario user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(
            "Usuário não encontrado com email: " + email));

    return User.builder()
        .username(user.getEmail())
        .password(user.getSenha())
        .authorities(user.getRole().toString()) // Ou use as roles do usuário
        .build();
  }
}
