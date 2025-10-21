package com.deliverytech.delivery_api;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.dtos.ProdutoDTO;
import com.deliverytech.delivery_api.entities.Produto;
import com.deliverytech.delivery_api.entities.Restaurante;
import com.deliverytech.delivery_api.repositories.PedidoRepository;
import com.deliverytech.delivery_api.repositories.ProdutoRepository;
import com.deliverytech.delivery_api.repositories.RestauranteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProdutoControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ProdutoRepository produtoRepository;

  @Autowired
  private RestauranteRepository restauranteRepository;

  @Autowired
  private PedidoRepository pedidoRepository;

  private ProdutoDTO produtoDTO;
  private Restaurante restauranteSalvo;
  private Produto produtoSalvo;

  @BeforeEach
  void setUp() {
    // Limpar dados na ordem correta
    pedidoRepository.deleteAll();
    produtoRepository.deleteAll();
    restauranteRepository.deleteAll();

    // Criar restaurante para testes
    Restaurante restaurante = new Restaurante();
    restaurante.setNome("Pizzaria Bella");
    restaurante.setCategoria("Italiana");
    restaurante.setEndereco("Rua das Flores, 123");
    restaurante.setEmail("contato@pizzaria.com");
    restaurante.setTelefone("11999999999");
    restaurante.setCnpj("12345678000195");
    restaurante.setTaxaEntrega(new BigDecimal("8.50"));
    restaurante.setTempoEntregaMin(30);
    restaurante.setTempoEntregaMax(45);
    restaurante.setAtivo(true);
    restauranteSalvo = restauranteRepository.save(restaurante);

    // Criar DTO para cadastro
    produtoDTO = new ProdutoDTO();
    produtoDTO.setNome("Pizza Margherita");
    produtoDTO.setDescricao("Molho de tomate, mussarela e manjericão");
    produtoDTO.setPreco(new BigDecimal("45.90"));
    produtoDTO.setCategoria("Pizzas");
    produtoDTO.setRestauranteId(restauranteSalvo.getId());
    produtoDTO.setImagemUrl("https://example.com/pizza.jpg");
    produtoDTO.setDisponivel(true);

    // Criar produto para testes de busca
    Produto produto = new Produto();
    produto.setNome("Pizza Calabresa");
    produto.setDescricao("Molho de tomate, calabresa e cebola");
    produto.setPreco(new BigDecimal("42.90"));
    produto.setCategoria("Pizzas");
    produto.setRestaurante(restauranteSalvo);
    produto.setDisponivel(true);
    produtoSalvo = produtoRepository.save(produto);
  }

  @Test
  void deveCadastrarProdutoComSucesso() throws Exception {
    mockMvc.perform(post("/api/produtos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(produtoDTO)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.nome").value("Pizza Margherita"))
        .andExpect(jsonPath("$.data.preco").value(45.90))
        .andExpect(jsonPath("$.data.categoria").value("Pizzas"))
        .andExpect(jsonPath("$.data.disponivel").value(true))
        .andExpect(jsonPath("$.data.restauranteId").value(restauranteSalvo.getId()))
        .andExpect(jsonPath("$.data.restauranteNome").value("Pizzaria Bella"))
        .andExpect(jsonPath("$.message").value("Produto criado com sucesso"));
  }

  @Test
  void deveRejeitarProdutoComDadosInvalidos() throws Exception {
    produtoDTO.setNome(""); // Nome inválido
    produtoDTO.setPreco(new BigDecimal("-10.00")); // Preço negativo
    produtoDTO.setCategoria(null); // Categoria nula

    mockMvc.perform(post("/api/produtos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(produtoDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
  }

  @Test
  void deveRejeitarProdutoSemRestaurante() throws Exception {
    produtoDTO.setRestauranteId(999L); // ID inexistente

    mockMvc.perform(post("/api/produtos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(produtoDTO)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveBuscarProdutoPorId() throws Exception {
    mockMvc.perform(get("/api/produtos/{id}", produtoSalvo.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(produtoSalvo.getId()))
        .andExpect(jsonPath("$.data.nome").value("Pizza Calabresa"))
        .andExpect(jsonPath("$.data.preco").value(42.90))
        .andExpect(jsonPath("$.data.categoria").value("Pizzas"))
        .andExpect(jsonPath("$.message").value("Produto encontrado com sucesso"));
  }

  @Test
  void deveRetornar404ParaProdutoInexistente() throws Exception {
    mockMvc.perform(get("/api/produtos/{id}", 999L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveListarProdutosPorRestaurante() throws Exception {
    mockMvc.perform(get("/api/restaurantes/{restauranteId}/produtos", restauranteSalvo.getId())
        .param("page", "0")
        .param("size", "10"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].nome").value("Pizza Calabresa"))
        .andExpect(jsonPath("$.content[0].restauranteId").value(restauranteSalvo.getId()))
        .andExpect(jsonPath("$.page.number").value(0))
        .andExpect(jsonPath("$.page.size").value(10))
        .andExpect(jsonPath("$.page.totalElements").value(1));
  }

  @Test
  void deveListarProdutosPorCategoria() throws Exception {
    mockMvc.perform(get("/api/produtos/categoria/{categoria}", "Pizzas")
        .param("page", "0")
        .param("size", "10"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].categoria").value("Pizzas"));
  }

  @Test
  void deveAtualizarProdutoComSucesso() throws Exception {
    produtoDTO.setNome("Pizza Margherita Premium");
    produtoDTO.setPreco(new BigDecimal("55.90"));

    mockMvc.perform(put("/api/produtos/{id}", produtoSalvo.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(produtoDTO)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.nome").value("Pizza Margherita Premium"))
        .andExpect(jsonPath("$.data.preco").value(55.90))
        .andExpect(jsonPath("$.message").value("Produto atualizado com sucesso"));
  }

  @Test
  void deveAlterarDisponibilidadeDoProduto() throws Exception {
    mockMvc.perform(patch("/api/produtos/{id}/disponibilidade", produtoSalvo.getId())
        .param("disponivel", "false"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.disponivel").value(false))
        .andExpect(jsonPath("$.message").value("Produto atualizado com sucesso"));
  }

  @Test
  void deveBuscarProdutosPorFaixaDePreco() throws Exception {
    mockMvc.perform(get("/api/produtos/buscar")
        .param("precoMin", "40.00")
        .param("precoMax", "50.00")
        .param("page", "0")
        .param("size", "10"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].preco").value(42.90));
  }

  @Test
  void deveRetornarListaVaziaQuandoNaoHouverProdutosNaFaixaDePreco() throws Exception {
    mockMvc.perform(get("/api/produtos/buscar")
        .param("precoMin", "100.00")
        .param("precoMax", "200.00")
        .param("page", "0")
        .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(0)));
  }

  @Test
  void deveRejeitarAtualizacaoComProdutoInexistente() throws Exception {
    mockMvc.perform(put("/api/produtos/{id}", 999L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(produtoDTO)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveRejeitarCadastroDeProdutoDuplicado() throws Exception {
    // Tentar cadastrar produto com mesmo nome do que já existe
    produtoDTO.setNome("Pizza Calabresa"); // Nome já existe

    mockMvc.perform(post("/api/produtos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(produtoDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("BUSINESS_ERROR"));
  }
}