package com.deliverytech.delivery_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.delivery_api.dtos.ApiResponseWrapper;
import com.deliverytech.delivery_api.dtos.LoginRequestDTO;
import com.deliverytech.delivery_api.dtos.LoginResponseDTO;
import com.deliverytech.delivery_api.dtos.RegisterRequestDTO;
import com.deliverytech.delivery_api.dtos.UserResponseDTO;
import com.deliverytech.delivery_api.entities.Usuario;
import com.deliverytech.delivery_api.security.JwtUtil;
import com.deliverytech.delivery_api.security.SecurityUtils;
import com.deliverytech.delivery_api.services.AuthService;

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

  @Operation(summary = "Login de usuário", description = "Autentica um usuário e retorna um token JWT")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Login bem-sucedido"),
      @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
  })
  @PostMapping("/login")
  public ResponseEntity<ApiResponseWrapper<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
    try {
      // Autenticar usuário
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getEmail(),
              loginRequest.getSenha()));

      // Carregar detalhes do usuário
      UserDetails userDetails = authService.loadUserByUsername(loginRequest.getEmail());

      // Gerar token JWT
      String token = jwtUtil.generateToken(userDetails);

      // Criar resposta
      Usuario usuario = (Usuario) userDetails;
      UserResponseDTO userResponse = new UserResponseDTO(usuario);
      LoginResponseDTO loginResponse = new LoginResponseDTO(token, jwtExpiration,
          userResponse);

      ApiResponseWrapper<LoginResponseDTO> response = new ApiResponseWrapper<>(true, loginResponse,
          "Login bem-sucedido");
      return ResponseEntity.ok(response);
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(401)
          .body(new ApiResponseWrapper<LoginResponseDTO>(false, null, "Credenciais inválidas"));
    } catch (Exception e) {
      System.err.println("Erro ao autenticar usuário: " + e.getMessage());
      return ResponseEntity.status(500)
          .body(new ApiResponseWrapper<LoginResponseDTO>(false, null, "Erro ao autenticar usuário"));
    }
  }

  @Operation(summary = "Registro de usuário", description = "Registra um novo usuário no sistema")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já em uso")
  })
  @PostMapping("/register")
  public ResponseEntity<ApiResponseWrapper<UserResponseDTO>> register(
      @Valid @RequestBody RegisterRequestDTO registerRequest) {
    try {
      // Verificar se email já existe
      if (authService.existsByEmail(registerRequest.getEmail())) {
        return ResponseEntity.badRequest()
            .body(new ApiResponseWrapper<UserResponseDTO>(false, null, "Email já está em uso"));
      }

      // Criar usuário
      Usuario novoUsuario = authService.criarUsuario(registerRequest);

      // Retornar dados do usuário (sem "token" - usuário deve fazer login)
      UserResponseDTO userResponse = new UserResponseDTO(novoUsuario);
      ApiResponseWrapper<UserResponseDTO> response = new ApiResponseWrapper<>(true, userResponse,
          "Usuário criado com sucesso");
      return ResponseEntity.status(201).body(response);
    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(new ApiResponseWrapper<UserResponseDTO>(false, null,
              "Erro ao criar usuário: " + e.getMessage()));
    }
  }

  @Operation(summary = "Obter usuário autenticado", description = "Retorna os detalhes do usuário atualmente autenticado")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Usuário autenticado retornado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
  })
  @GetMapping("/me")
  public ResponseEntity<ApiResponseWrapper<UserResponseDTO>> getCurrentUser() {
    try {
      Usuario usuarioLogado = SecurityUtils.getCurrentUser();
      UserResponseDTO userResponse = new UserResponseDTO(usuarioLogado);
      ApiResponseWrapper<UserResponseDTO> response = new ApiResponseWrapper<>(true, userResponse,
          "Usuário autenticado retornado com sucesso");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(new ApiResponseWrapper<UserResponseDTO>(false, null,
              "Erro ao obter usuário autenticado: " + e.getMessage()));
    }
  }
}