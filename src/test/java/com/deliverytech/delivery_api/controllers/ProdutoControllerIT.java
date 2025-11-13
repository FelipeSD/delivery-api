package com.deliverytech.delivery_api.controllers;

import static com.deliverytech.delivery_api.utils.matchers.ApiResponseMatchers.erro;
import static com.deliverytech.delivery_api.utils.matchers.ApiResponseMatchers.mensagem;
import static com.deliverytech.delivery_api.utils.matchers.ApiResponseMatchers.sucesso;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.deliverytech.delivery_api.auth.repository.UsuarioRepository;
import com.deliverytech.delivery_api.produto.dto.ProdutoDTO;
import com.deliverytech.delivery_api.produto.model.Produto;
import com.deliverytech.delivery_api.produto.repository.ProdutoRepository;
import com.deliverytech.delivery_api.restaurante.model.Restaurante;
import com.deliverytech.delivery_api.restaurante.repository.RestauranteRepository;
import com.deliverytech.delivery_api.utils.base.BaseIntegrationTest;
import com.deliverytech.delivery_api.utils.factories.EntityFactory;
import com.deliverytech.delivery_api.utils.factories.UsuarioFactory;

@DisplayName("Teste de Integração do ProdutoController")
class ProdutoControllerIT extends BaseIntegrationTest {

  @Autowired
  private ProdutoRepository produtoRepository;

  @Autowired
  private RestauranteRepository restauranteRepository;

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private UsuarioFactory usuarioFactory;

  private ProdutoDTO produtoDTO;
  private Restaurante restauranteSalvo;
  private Produto produtoSalvo;
  private String restauranteJwtToken;

  @BeforeEach
  void setUp() throws Exception {
    restauranteSalvo = restauranteRepository.save(EntityFactory.criarRestaurante());
    produtoSalvo = produtoRepository.save(EntityFactory.criarProduto(restauranteSalvo));
    produtoDTO = EntityFactory.criarProdutoDTO(restauranteSalvo.getId());

    var usuario = usuarioRepository.save(usuarioFactory.criarUsuarioRestaurante(restauranteSalvo));
    restauranteJwtToken = loginAndGetToken(usuario.getEmail(), "123456");
  }

  @Test
  @Order(1)
  void deveCadastrarProdutoComSucesso() throws Exception {
    postJson("/api/produtos", restauranteJwtToken, produtoDTO)
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.data.nome").value("Pizza Margherita"))
        .andExpect(jsonPath("$.data.preco").value(45.90))
        .andExpect(jsonPath("$.data.categoria").value("Pizzas"))
        .andExpect(jsonPath("$.data.disponivel").value(true))
        .andExpect(jsonPath("$.data.restauranteId").value(restauranteSalvo.getId()))
        .andExpect(jsonPath("$.data.restauranteNome").value("Pizzaria Bella"))
        .andExpect(mensagem("Produto criado com sucesso"));
  }

  @Test
  @Order(2)
  void deveRejeitarProdutoComDadosInvalidos() throws Exception {
    produtoDTO.setNome("");
    produtoDTO.setPreco(new BigDecimal("-10.00"));
    produtoDTO.setCategoria(null);

    postJson("/api/produtos", restauranteJwtToken, produtoDTO)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("VALIDATION_ERROR"));
  }

  @Test
  @Order(3)
  void deveRejeitarProdutoSemRestaurante() throws Exception {
    produtoDTO.setRestauranteId(999L);

    postJson("/api/produtos", restauranteJwtToken, produtoDTO)
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("ENTITY_NOT_FOUND"));
  }

  @Test
  @Order(4)
  void deveBuscarProdutoPorId() throws Exception {
    getJson("/api/produtos/{id}", restauranteJwtToken, produtoSalvo.getId())
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.data.id").value(produtoSalvo.getId()))
        .andExpect(jsonPath("$.data.nome").value("Pizza Calabresa"))
        .andExpect(jsonPath("$.data.preco").value(45.90))
        .andExpect(jsonPath("$.data.categoria").value("Pizzas"))
        .andExpect(mensagem("Produto encontrado com sucesso"));
  }

  @Test
  @Order(5)
  void deveRetornar404ParaProdutoInexistente() throws Exception {
    getJson("/api/produtos/{id}", restauranteJwtToken, 999L)
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("ENTITY_NOT_FOUND"));
  }

  @Test
  @Order(6)
  void deveListarProdutosPorRestaurante() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("page", "0");
    params.put("size", "10");
    getJson("/api/restaurantes/{restauranteId}/produtos", restauranteJwtToken, params, restauranteSalvo.getId())
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].nome").value("Pizza Calabresa"))
        .andExpect(jsonPath("$.content[0].restauranteId").value(restauranteSalvo.getId()))
        .andExpect(jsonPath("$.page.number").value(0))
        .andExpect(jsonPath("$.page.size").value(10))
        .andExpect(jsonPath("$.page.totalElements").value(1));
  }

  @Test
  @Order(7)
  void deveListarProdutosPorCategoria() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("page", "0");
    params.put("size", "10");
    getJson("/api/produtos/categoria/{categoria}", restauranteJwtToken, "Pizzas", params)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].categoria").value("Pizzas"));
  }

  @Test
  @Order(8)
  void deveAtualizarProdutoComSucesso() throws Exception {
    produtoDTO.setNome("Pizza Margherita Premium");
    produtoDTO.setPreco(new BigDecimal("55.90"));

    putJson("/api/produtos/{id}", restauranteJwtToken, produtoDTO, produtoSalvo.getId())
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.data.nome").value("Pizza Margherita Premium"))
        .andExpect(jsonPath("$.data.preco").value(55.90))
        .andExpect(mensagem("Produto atualizado com sucesso"));
  }

  @Test
  @Order(9)
  void deveAlterarDisponibilidadeDoProduto() throws Exception {
    Map<String, Object> body = Map.of("disponivel", false);

    patchJson("/api/produtos/{id}/disponibilidade", restauranteJwtToken, body, produtoSalvo.getId())
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.data.disponivel").value(false))
        .andExpect(mensagem("Produto atualizado com sucesso"));
  }

  @Test
  @Order(10)
  void deveBuscarProdutosPorFaixaDePreco() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("precoMin", "40.00");
    params.put("precoMax", "50.00");
    params.put("page", "0");
    params.put("size", "10");
    getJson("/api/produtos/buscar", restauranteJwtToken, params)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].preco").value(45.90));
  }

  @Test
  @Order(11)
  void deveRetornarListaVaziaQuandoNaoHouverProdutosNaFaixaDePreco() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("precoMin", "100.00");
    params.put("precoMax", "200.00");
    params.put("page", "0");
    params.put("size", "10");
    getJson("/api/produtos/buscar", restauranteJwtToken, params)
        .andExpect(status().isOk())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(0)));
  }

  @Test
  @Order(12)
  void deveRejeitarCadastroDeProdutoDuplicado() throws Exception {
    produtoDTO.setNome("Pizza Calabresa");

    postJson("/api/produtos", restauranteJwtToken, produtoDTO)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("BUSINESS_ERROR"));
  }

  @Test
  @Order(13)
  void deveDeletarProduto() throws Exception {
    deleteJson("/api/produtos/{id}", restauranteJwtToken, produtoSalvo.getId())
        .andExpect(status().isNoContent());
  }
}