package com.deliverytech.delivery_api;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.*;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.dtos.RestauranteDTO;
import com.deliverytech.delivery_api.entities.Restaurante;
import com.deliverytech.delivery_api.repositories.PedidoRepository;
import com.deliverytech.delivery_api.repositories.ProdutoRepository;
import com.deliverytech.delivery_api.repositories.RestauranteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RestauranteControllerIT {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private RestauranteRepository restauranteRepository;
  @Autowired
  private ProdutoRepository produtoRepository;
  @Autowired
  private PedidoRepository pedidoRepository;

  private RestauranteDTO restauranteDTO;
  private Restaurante restauranteSalvo;

  @BeforeEach
  void setUp() {
    pedidoRepository.deleteAll();
    produtoRepository.deleteAll();
    restauranteRepository.deleteAll();

    restauranteDTO = new RestauranteDTO();
    restauranteDTO.setNome("Pizza Express");
    restauranteDTO.setCategoria("Italiana");
    restauranteDTO.setEndereco("Rua das Flores, 123");
    restauranteDTO.setEmail("email@dsas.com");
    restauranteDTO.setTelefone("11999999999");
    restauranteDTO.setCnpj("12345678000195");
    restauranteDTO.setTaxaEntrega(new BigDecimal("5.50"));
    restauranteDTO.setTempoEntregaMin(10);
    restauranteDTO.setTempoEntregaMax(20);
    restauranteDTO.setHorarioFuncionamento("09:00-22:00");

    // Criar restaurante para testes de busca
    Restaurante restaurante = new Restaurante();
    restaurante.setNome("Burger King");
    restaurante.setCategoria("Americana");
    restaurante.setEndereco("Av. Paulista, 1000");
    restaurante.setEmail("email@teste.com");
    restaurante.setTelefone("11888888888");
    restaurante.setCnpj("75825600033170");
    restaurante.setTaxaEntrega(new BigDecimal("4.00"));
    restaurante.setTempoEntregaMin(10);
    restaurante.setTempoEntregaMax(20);
    restaurante.setHorarioFuncionamento("09:00-22:00");

    restaurante.setAtivo(true);
    restauranteSalvo = restauranteRepository.save(restaurante);
  }

  @Test
  void deveCadastrarRestauranteComSucesso() throws Exception {
    mockMvc.perform(post("/api/restaurantes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(restauranteDTO)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.nome").value("Pizza Express"))
        .andExpect(jsonPath("$.data.categoria").value("Italiana"))
        .andExpect(jsonPath("$.data.ativo").value(true))
        .andExpect(jsonPath("$.message").value("Restaurante criado com sucesso"));
  }

  @Test
  void deveRejeitarRestauranteComDadosInvalidos() throws Exception {
    restauranteDTO.setNome(""); // Nome inválido
    restauranteDTO.setTelefone("123"); // Telefone inválido

    mockMvc.perform(post("/api/restaurantes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(restauranteDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.error.details.nome").exists())
        .andExpect(jsonPath("$.error.details.telefone").exists());
  }

  @Test
  void deveBuscarRestaurantePorId() throws Exception {
    mockMvc.perform(get("/api/restaurantes/{id}", restauranteSalvo.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(restauranteSalvo.getId()))
        .andExpect(jsonPath("$.data.nome").value("Burger King"))
        .andExpect(jsonPath("$.data.categoria").value("Americana"));
  }

  @Test
  void deveRetornar404ParaRestauranteInexistente() throws Exception {
    mockMvc.perform(get("/api/restaurantes/{id}", 999L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveListarRestaurantesComPaginacao() throws Exception {
    mockMvc.perform(get("/api/restaurantes")
        .param("page", "0")
        .param("size", "10"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.page.number").value(0))
        .andExpect(jsonPath("$.page.size").value(10))
        .andExpect(jsonPath("$.page.totalElements").value(1));
  }

  @Test
  void deveAtualizarRestauranteComSucesso() throws Exception {
    restauranteDTO.setNome("Pizza Express Atualizada");

    mockMvc.perform(put("/api/restaurantes/{id}", restauranteSalvo.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(restauranteDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.nome").value("Pizza Express Atualizada"))
        .andExpect(jsonPath("$.message").value("Restaurante atualizado com sucesso"));
  }

  @Test
  void deveAlterarStatusRestaurante() throws Exception {
    mockMvc.perform(patch("/api/restaurantes/{id}/status", restauranteSalvo.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.ativo").value(false))
        .andExpect(jsonPath("$.message").value("Status alterado com sucesso"));
  }

  @Test
  void deveBuscarRestaurantesPorCategoria() throws Exception {
    mockMvc.perform(get("/api/restaurantes/categoria/{categoria}", "Americana"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].categoria").value("Americana"));
  }
}
