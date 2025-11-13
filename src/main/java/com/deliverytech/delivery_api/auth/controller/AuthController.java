package com.deliverytech.delivery_api.auth.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.delivery_api.auth.dto.LoginRequestDTO;
import com.deliverytech.delivery_api.auth.dto.RegisterRequestDTO;
import com.deliverytech.delivery_api.auth.dto.UsuarioResponseDTO;
import com.deliverytech.delivery_api.auth.model.Usuario;
import com.deliverytech.delivery_api.auth.service.AuthService;
import com.deliverytech.delivery_api.common.dto.ApiResponseWrapper;
import com.deliverytech.delivery_api.common.security.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Autenticação", description = "API para autenticação e registro de usuários")
public class AuthController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private AuthService authService;

  @Autowired
  private JwtUtil jwtUtil;

  @Value("${jwt.expiration}")
  private Long jwtExpiration;

  @Operation(summary = "Login de usuário", description = "Autentica um usuário e retorna um token JWT de acesso e cookie de refresh token seguro")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login bem-sucedido"),
      @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
  })
  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
    try {
      // 1️⃣ Autentica o usuário
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getEmail(),
              loginRequest.getSenha()));

      // 2️⃣ Carrega o usuário autenticado
      Usuario userDetails = authService.loadUserByUsername(loginRequest.getEmail());

      // 3️⃣ Gera tokens
      String accessToken = jwtUtil.generateToken(userDetails);
      String refreshToken = jwtUtil.generateRefreshToken(userDetails);

      // 4️⃣ Cria cookie seguro para refresh token
      ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
          .httpOnly(true)
          .secure(true)
          .sameSite("Strict")
          .path("/api/auth/refresh")
          .maxAge(Duration.ofDays(7))
          .build();

      return ResponseEntity.ok()
          .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
          .body(accessToken);

    } catch (BadCredentialsException e) {
      return ResponseEntity.status(401)
          .body(new ApiResponseWrapper<>(false, null, "Credenciais inválidas"));
    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(new ApiResponseWrapper<>(false, null, "Erro ao autenticar usuário"));
    }
  }

  @Operation(summary = "Refresh de token", description = "Gera um novo token JWT de acesso usando o refresh token do cookie seguro")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Novo token de acesso gerado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado")
  })
  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(@CookieValue("refresh_token") String refreshToken) {
    try {
      if (!jwtUtil.validateRefreshToken(refreshToken)) {
        return ResponseEntity.status(401)
            .body(new ApiResponseWrapper<>(false, null, "Refresh token inválido ou expirado"));
      }

      String email = jwtUtil.extractUsername(refreshToken);

      Usuario usuario = authService.loadUserByUsername(email);

      String newAccessToken = jwtUtil.generateToken(usuario);

      return ResponseEntity.ok(newAccessToken);

    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(new ApiResponseWrapper<>(false, null, "Erro ao gerar novo token de acesso"));
    }
  }

  @Operation(summary = "Registro de usuário", description = "Registra um novo usuário no sistema")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já em uso")
  })
  @PostMapping("/register")
  public ResponseEntity<ApiResponseWrapper<UsuarioResponseDTO>> register(
      @Valid @RequestBody RegisterRequestDTO registerRequest) {
    try {
      // Verificar se email já existe
      if (authService.existsByEmail(registerRequest.getEmail())) {
        return ResponseEntity.badRequest()
            .body(new ApiResponseWrapper<UsuarioResponseDTO>(false, null, "Email já está em uso"));
      }

      // Criar usuário
      Usuario novoUsuario = authService.criarUsuario(registerRequest);

      // Retornar dados do usuário (sem "token" - usuário deve fazer login)
      UsuarioResponseDTO userResponse = new UsuarioResponseDTO(novoUsuario);
      ApiResponseWrapper<UsuarioResponseDTO> response = new ApiResponseWrapper<>(true, userResponse,
          "Usuário criado com sucesso");
      return ResponseEntity.status(201).body(response);
    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(new ApiResponseWrapper<UsuarioResponseDTO>(false, null,
              "Erro ao criar usuário: " + e.getMessage()));
    }
  }

  @Operation(summary = "Obter usuário autenticado", description = "Retorna os detalhes do usuário atualmente autenticado")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Usuário autenticado retornado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
  })
  @GetMapping("/me")
  public ResponseEntity<ApiResponseWrapper<UsuarioResponseDTO>> getCurrentUser(
      @AuthenticationPrincipal Usuario usuarioLogado) {
    try {
      UsuarioResponseDTO userResponse = new UsuarioResponseDTO(usuarioLogado);
      ApiResponseWrapper<UsuarioResponseDTO> response = new ApiResponseWrapper<>(true, userResponse,
          "Usuário autenticado retornado com sucesso");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(new ApiResponseWrapper<UsuarioResponseDTO>(false, null,
              "Erro ao obter usuário autenticado: " + e.getMessage()));
    }
  }
}