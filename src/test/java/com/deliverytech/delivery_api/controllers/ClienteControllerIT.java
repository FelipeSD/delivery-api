package com.deliverytech.delivery_api.controllers;

import com.deliverytech.delivery_api.base.BaseIntegrationTest;
import com.deliverytech.delivery_api.dtos.ClienteDTO;
import com.deliverytech.delivery_api.entities.Cliente;
import com.deliverytech.delivery_api.factories.EntityFactory;
import com.deliverytech.delivery_api.factories.UsuarioFactory;
import com.deliverytech.delivery_api.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static com.deliverytech.delivery_api.matchers.ApiResponseMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Teste de Integração do ClienteController")
class ClienteControllerIT extends BaseIntegrationTest {

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private UsuarioFactory usuarioFactory;

  private Cliente clienteAtivo;
  private Cliente clienteInativo;
  private ClienteDTO clienteDTO;
  private String clienteJwtToken;
  private String adminJwtToken;

  @BeforeEach
  void setUp() throws Exception {
    // Criar clientes
    clienteAtivo = clienteRepository.save(EntityFactory.criarClienteAtivo());
    clienteInativo = clienteRepository.save(EntityFactory.criarClienteInativo());

    // Criar usuários e tokens
    var usuarioCliente = usuarioRepository.save(usuarioFactory.criarUsuarioCliente());
    clienteJwtToken = loginAndGetToken(usuarioCliente.getEmail(), "123456");

    var usuarioAdmin = usuarioRepository.save(usuarioFactory.criarUsuarioAdmin());
    adminJwtToken = loginAndGetToken(usuarioAdmin.getEmail(), "123456");

    // DTO base
    clienteDTO = EntityFactory.criarClienteDTO();
  }

  @Test
  void deveCadastrarClienteComSucesso() throws Exception {
    postJson("/api/clientes", clienteJwtToken, clienteDTO)
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.data.id").exists())
      .andExpect(jsonPath("$.data.nome").value("João Silva"))
      .andExpect(jsonPath("$.data.email").value("joao@email.com"))
      .andExpect(jsonPath("$.data.telefone").value("11777777777"))
      .andExpect(mensagem("Cliente criado com sucesso"));
  }

  @Test
  void deveRejeitarCadastroComDadosInvalidos() throws Exception {
    ClienteDTO dtoInvalido = EntityFactory.criarClienteDTOInvalido();

    postJson("/api/clientes", clienteJwtToken, dtoInvalido)
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(erro("VALIDATION_ERROR"));
  }

  @Test
  void deveBuscarClientePorIdComSucesso() throws Exception {
    getJson("/api/clientes/{id}", clienteJwtToken, clienteAtivo.getId())
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.data.id").value(clienteAtivo.getId()))
      .andExpect(jsonPath("$.data.nome").value("Maria Oliveira"))
      .andExpect(mensagem("Cliente encontrado com sucesso"));
  }

  @Test
  void deveRetornar404AoBuscarClienteInexistente() throws Exception {
    getJson("/api/clientes/{id}", clienteJwtToken, 999L)
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(erro("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveListarClientesAtivos() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("page", "0");
    params.put("size", "10");

    getJson("/api/clientes", adminJwtToken, params)
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.content").isArray())
      .andExpect(jsonPath("$.content", hasSize(1)))
      .andExpect(jsonPath("$.content[0].ativo").value(true))
      .andExpect(jsonPath("$.page.number").value(0))
      .andExpect(jsonPath("$.page.totalElements").value(1));
  }

  @Test
  void deveAtualizarClienteComSucesso() throws Exception {
    clienteDTO.setNome("Maria Oliveira Atualizada");

    putJson("/api/clientes/{id}", clienteJwtToken, clienteDTO, clienteAtivo.getId())
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.data.nome").value("Maria Oliveira Atualizada"))
      .andExpect(mensagem("Cliente atualizado com sucesso"));
  }

  @Test
  void deveRetornar404AoAtualizarClienteInexistente() throws Exception {
    putJson("/api/clientes/{id}", clienteJwtToken, clienteDTO, 999L)
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(erro("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveAtivarOuDesativarClienteComSucesso() throws Exception {
    patchJson("/api/clientes/{id}/status", adminJwtToken, null, clienteInativo.getId())
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.data.ativo").value(true))
      .andExpect(mensagem("Status do cliente atualizado com sucesso"));
  }

  @Test
  void deveRetornar404AoAtivarDesativarClienteInexistente() throws Exception {
    patchJson("/api/clientes/{id}/status", adminJwtToken, null, 999L)
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(erro("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveBuscarClientePorEmailComSucesso() throws Exception {
    Map<String, String> params = new HashMap<>();
    getJson("/api/clientes/email/{email}", clienteJwtToken, params, clienteAtivo.getEmail())
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.data.email").value(clienteAtivo.getEmail()))
      .andExpect(mensagem("Cliente encontrado com sucesso"));
  }

  @Test
  void deveRetornar404AoBuscarClientePorEmailInexistente() throws Exception {
    Map<String, String> params = new HashMap<>();
    getJson("/api/clientes/email/{email}", clienteJwtToken, params, "naoexiste@email.com")
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(erro("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveRejeitarCadastroDuplicadoPorEmail() throws Exception {
    clienteDTO.setEmail(clienteAtivo.getEmail());

    postJson("/api/clientes", clienteJwtToken, clienteDTO)
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(erro("BUSINESS_ERROR"));
  }
}