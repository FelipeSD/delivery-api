package com.deliverytech.delivery_api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.deliverytech.delivery_api.auth.dto.RegisterRequestDTO;
import com.deliverytech.delivery_api.auth.model.Role;
import com.deliverytech.delivery_api.auth.model.Usuario;
import com.deliverytech.delivery_api.auth.repository.UsuarioRepository;
import com.deliverytech.delivery_api.auth.service.AuthService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Testes Unitários")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private RegisterRequestDTO registerRequest;
    private static final String EMAIL = "teste@email.com";
    private static final String SENHA = "senha123";
    private static final String SENHA_ENCODED = "senhaEncoded123";

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Teste Usuario");
        usuario.setEmail(EMAIL);
        usuario.setSenha(SENHA_ENCODED);
        usuario.setRole(Role.CLIENTE);
        usuario.setAtivo(true);

        registerRequest = new RegisterRequestDTO();
        registerRequest.setNome("Novo Usuario");
        registerRequest.setEmail("novo@email.com");
        registerRequest.setSenha(SENHA);
        registerRequest.setRole(Role.CLIENTE);
    }

    @Test
    @DisplayName("Deve carregar usuário por username com sucesso")
    void deveCarregarUsuarioPorUsername() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));

        Usuario result = authService.loadUserByUsername(EMAIL);

        assertNotNull(result);
        assertEquals(EMAIL, result.getEmail());
        verify(usuarioRepository).findByEmail(EMAIL);
    }

    @Test
    @DisplayName("Deve lançar exceção ao não encontrar usuário por username")
    void deveLancarExcecaoAoNaoEncontrarUsuarioPorUsername() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, 
            () -> authService.loadUserByUsername(EMAIL));
        verify(usuarioRepository).findByEmail(EMAIL);
    }

    @Test
    @DisplayName("Deve verificar se email existe")
    void deveVerificarSeEmailExiste() {
        when(usuarioRepository.existsByEmail(EMAIL)).thenReturn(true);

        boolean exists = authService.existsByEmail(EMAIL);

        assertTrue(exists);
        verify(usuarioRepository).existsByEmail(EMAIL);
    }

    @Test
    @DisplayName("Deve retornar false quando email não existe")
    void deveRetornarFalseQuandoEmailNaoExiste() {
        when(usuarioRepository.existsByEmail(EMAIL)).thenReturn(false);

        boolean exists = authService.existsByEmail(EMAIL);

        assertFalse(exists);
        verify(usuarioRepository).existsByEmail(EMAIL);
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuarioComSucesso() {
        when(passwordEncoder.encode(SENHA)).thenReturn(SENHA_ENCODED);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario result = authService.criarUsuario(registerRequest);

        assertNotNull(result);
        verify(passwordEncoder).encode(SENHA);
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve criar usuário com restaurante associado")
    void deveCriarUsuarioComRestaurante() {
        registerRequest.setRestauranteId(1L);
        when(passwordEncoder.encode(SENHA)).thenReturn(SENHA_ENCODED);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        Usuario result = authService.criarUsuario(registerRequest);

        assertNotNull(result);
        verify(usuarioRepository).save(argThat(u -> 
            u.getRestaurante() != null && u.getRestaurante().getId().equals(1L)
        ));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarUsuarioPorId() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario result = authService.buscarPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(usuarioRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao não encontrar usuário por ID")
    void deveLancarExcecaoAoNaoEncontrarUsuarioPorId() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> authService.buscarPorId(1L));
        verify(usuarioRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve buscar usuário por email com sucesso")
    void deveBuscarUsuarioPorEmail() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));

        Usuario result = authService.buscarPorEmail(EMAIL);

        assertNotNull(result);
        assertEquals(EMAIL, result.getEmail());
        verify(usuarioRepository).findByEmail(EMAIL);
    }

    @Test
    @DisplayName("Deve lançar exceção ao não encontrar usuário por email")
    void deveLancarExcecaoAoNaoEncontrarUsuarioPorEmail() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> authService.buscarPorEmail(EMAIL));
        verify(usuarioRepository).findByEmail(EMAIL);
    }
}