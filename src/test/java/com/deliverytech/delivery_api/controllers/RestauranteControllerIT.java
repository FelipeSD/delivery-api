package com.deliverytech.delivery_api.controllers;

import com.deliverytech.delivery_api.base.BaseIntegrationTest;
import com.deliverytech.delivery_api.dtos.RestauranteDTO;
import com.deliverytech.delivery_api.entities.Restaurante;
import com.deliverytech.delivery_api.factories.EntityFactory;
import com.deliverytech.delivery_api.factories.UsuarioFactory;
import com.deliverytech.delivery_api.repositories.RestauranteRepository;
import com.deliverytech.delivery_api.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.deliverytech.delivery_api.matchers.ApiResponseMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Teste de Integração do RestauranteController")
class RestauranteControllerIT extends BaseIntegrationTest {

  @Autowired
  private RestauranteRepository restauranteRepository;

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private UsuarioFactory usuarioFactory;

  private Restaurante restauranteAtivo;
  private Restaurante restauranteInativo;
  private RestauranteDTO restauranteDTO;
  private String restauranteJwtToken;
  private String adminJwtToken;

  @BeforeEach
  void setUp() throws Exception {
    // Criar restaurantes
    restauranteAtivo = restauranteRepository.save(EntityFactory.criarRestauranteAtivo());
    restauranteInativo = restauranteRepository.save(EntityFactory.criarRestauranteInativo());

    // Criar usuários e tokens
    var usuarioRestaurante = usuarioRepository.save(usuarioFactory.criarUsuarioRestaurante(restauranteAtivo));
    restauranteJwtToken = loginAndGetToken(usuarioRestaurante.getEmail(), "123456");

    var usuarioAdmin = usuarioRepository.save(usuarioFactory.criarUsuarioAdmin());
    adminJwtToken = loginAndGetToken(usuarioAdmin.getEmail(), "123456");

    // DTO base
    restauranteDTO = EntityFactory.criarRestauranteDTO();
  }

  @Test
  void deveCadastrarRestauranteComSucesso() throws Exception {
    postJson("/api/restaurantes", adminJwtToken, restauranteDTO)
      .andDo(print())
      .andExpect(status().isCreated())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.data.nome").value("Pizza Express"))
      .andExpect(jsonPath("$.data.categoria").value("Italiana"))
      .andExpect(jsonPath("$.data.email").value("pizzaexpress@email.com"))
      .andExpect(jsonPath("$.data.cnpj").value("12345678000195"))
      .andExpect(jsonPath("$.data.taxaEntrega").value(5.50))
      .andExpect(jsonPath("$.data.ativo").value(true))
      .andExpect(mensagem("Restaurante criado com sucesso"));
  }

  @Test
  void deveRejeitarRestauranteComDadosInvalidos() throws Exception {
    RestauranteDTO dtoInvalido = EntityFactory.criarRestauranteDTOInvalido();

    postJson("/api/restaurantes", adminJwtToken, dtoInvalido)
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(erro("VALIDATION_ERROR"))
      .andExpect(jsonPath("$.error.details.nome").exists())
      .andExpect(jsonPath("$.error.details.telefone").exists());
  }

  @Test
  void deveBuscarRestaurantePorId() throws Exception {
    getJson("/api/restaurantes/{id}", restauranteJwtToken, restauranteAtivo.getId())
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.data.id").value(restauranteAtivo.getId()))
      .andExpect(jsonPath("$.data.nome").value("Burger King"))
      .andExpect(jsonPath("$.data.categoria").value("Americana"))
      .andExpect(mensagem("Restaurante encontrado"));
  }

  @Test
  void deveRetornar404ParaRestauranteInexistente() throws Exception {
    getJson("/api/restaurantes/{id}", restauranteJwtToken, 999L)
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(erro("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveListarRestaurantesComPaginacao() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("page", "0");
    params.put("size", "10");

    getJson("/api/restaurantes", restauranteJwtToken, params)
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.content").isArray())
      .andExpect(jsonPath("$.content", hasSize(2)))
      .andExpect(jsonPath("$.page.number").value(0))
      .andExpect(jsonPath("$.page.size").value(10))
      .andExpect(jsonPath("$.page.totalElements").value(2));
  }

  @Test
  void deveListarApenasRestaurantesAtivos() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("page", "0");
    params.put("size", "10");
    params.put("ativo", "true");

    getJson("/api/restaurantes", restauranteJwtToken, params)
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.content").isArray())
      .andExpect(jsonPath("$.content", hasSize(1)))
      .andExpect(jsonPath("$.content[0].ativo").value(true))
      .andExpect(jsonPath("$.page.totalElements").value(1));
  }

  @Test
  void deveAtualizarRestauranteComSucesso() throws Exception {
    restauranteDTO.setNome("Burger King Atualizado");
    restauranteDTO.setTaxaEntrega(new BigDecimal("7.00"));

    putJson("/api/restaurantes/{id}", restauranteJwtToken, restauranteDTO, restauranteAtivo.getId())
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.data.nome").value("Burger King Atualizado"))
      .andExpect(jsonPath("$.data.taxaEntrega").value(7.00))
      .andExpect(mensagem("Restaurante atualizado com sucesso"));
  }

  @Test
  void deveRetornar404AoAtualizarRestauranteInexistente() throws Exception {
    putJson("/api/restaurantes/{id}", restauranteJwtToken, restauranteDTO, 999L)
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(erro("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveAlterarStatusRestaurante() throws Exception {
    patchJson("/api/restaurantes/{id}/status", adminJwtToken, null, restauranteAtivo.getId())
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.data.ativo").value(false))
      .andExpect(mensagem("Status alterado com sucesso"));
  }

  @Test
  void deveAtivarRestauranteInativo() throws Exception {
    patchJson("/api/restaurantes/{id}/status", adminJwtToken, null, restauranteInativo.getId())
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.data.ativo").value(true))
      .andExpect(mensagem("Status alterado com sucesso"));
  }

  @Test
  void deveRetornar404AoAlterarStatusRestauranteInexistente() throws Exception {
    patchJson("/api/restaurantes/{id}/status", adminJwtToken, null, 999L)
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(erro("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveBuscarRestaurantesPorCategoria() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("page", "0");
    params.put("size", "10");

    getJson("/api/restaurantes/categoria/{categoria}", restauranteJwtToken, "Americana", params)
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.content").isArray())
      .andExpect(jsonPath("$.content", hasSize(1)))
      .andExpect(jsonPath("$.content[0].categoria").value("Americana"))
      .andExpect(jsonPath("$.content[0].nome").value("Burger King"));
  }

  @Test
  void deveRetornarListaVaziaParaCategoriaInexistente() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("page", "0");
    params.put("size", "10");

    getJson("/api/restaurantes/categoria/{categoria}", restauranteJwtToken, "Mexicana", params)
      .andExpect(status().isOk())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.content").isArray())
      .andExpect(jsonPath("$.content", hasSize(0)))
      .andExpect(jsonPath("$.page.totalElements").value(0));
  }

  @Test
  void deveBuscarRestaurantesPorNome() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("nome", "Burger");
    params.put("page", "0");
    params.put("size", "10");

    getJson("/api/restaurantes/buscar", restauranteJwtToken, params)
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(sucesso())
      .andExpect(jsonPath("$.content").isArray())
      .andExpect(jsonPath("$.content", hasSize(1)))
      .andExpect(jsonPath("$.content[0].nome").value("Burger King"));
  }

  @Test
  void deveRejeitarCadastroDuplicadoPorCnpj() throws Exception {
    restauranteDTO.setCnpj(restauranteAtivo.getCnpj());

    postJson("/api/restaurantes", adminJwtToken, restauranteDTO)
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(erro("BUSINESS_ERROR"));
  }

  @Test
  void deveRejeitarCadastroDuplicadoPorEmail() throws Exception {
    restauranteDTO.setEmail(restauranteAtivo.getEmail());

    postJson("/api/restaurantes", adminJwtToken, restauranteDTO)
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(erro("BUSINESS_ERROR"));
  }
}