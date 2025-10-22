package com.deliverytech.delivery_api;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.dtos.ItemPedidoDTO;
import com.deliverytech.delivery_api.dtos.PedidoDTO;
import com.deliverytech.delivery_api.dtos.StatusPedidoDTO;
import com.deliverytech.delivery_api.entities.Cliente;
import com.deliverytech.delivery_api.entities.Pedido;
import com.deliverytech.delivery_api.entities.Produto;
import com.deliverytech.delivery_api.entities.Restaurante;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.repositories.ClienteRepository;
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
class PedidoControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PedidoRepository pedidoRepository;

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private RestauranteRepository restauranteRepository;

  @Autowired
  private ProdutoRepository produtoRepository;

  private PedidoDTO pedidoDTO;
  private Cliente clienteSalvo;
  private Restaurante restauranteSalvo;
  private Produto produtoSalvo;
  private Pedido pedidoSalvo;

  @BeforeEach
  void setUp() {
    // Limpar dados na ordem correta
    pedidoRepository.deleteAll();
    produtoRepository.deleteAll();
    clienteRepository.deleteAll();
    restauranteRepository.deleteAll();

    // Criar cliente
    Cliente cliente = new Cliente();
    cliente.setNome("João Silva");
    cliente.setEmail("joao@email.com");
    cliente.setTelefone("11999999999");
    cliente.setAtivo(true);
    clienteSalvo = clienteRepository.save(cliente);

    // Criar restaurante
    Restaurante restaurante = new Restaurante();
    restaurante.setNome("Pizzaria Bella");
    restaurante.setCategoria("Italiana");
    restaurante.setEndereco("Rua das Flores, 123");
    restaurante.setEmail("contato@pizzaria.com");
    restaurante.setTelefone("11888888888");
    restaurante.setCnpj("12345678000195");
    restaurante.setTaxaEntrega(new BigDecimal("8.50"));
    restaurante.setTempoEntregaMin(30);
    restaurante.setTempoEntregaMax(45);
    restaurante.setAtivo(true);
    restauranteSalvo = restauranteRepository.save(restaurante);

    // Criar produto
    Produto produto = new Produto();
    produto.setNome("Pizza Margherita");
    produto.setDescricao("Molho de tomate, mussarela e manjericão");
    produto.setPreco(new BigDecimal("45.90"));
    produto.setCategoria("Pizzas");
    produto.setRestaurante(restauranteSalvo);
    produto.setDisponivel(true);
    produtoSalvo = produtoRepository.save(produto);

    // Criar DTO de pedido
    pedidoDTO = new PedidoDTO();
    pedidoDTO.setClienteId(clienteSalvo.getId());
    pedidoDTO.setRestauranteId(restauranteSalvo.getId());
    pedidoDTO.setEnderecoEntrega("Rua A, 123 - São Paulo/SP");
    pedidoDTO.setCep("46464584");
    pedidoDTO.setObservacoes("Por favor, entregar após as 18h");
    pedidoDTO.setFormaPagamento("PIX");

    List<ItemPedidoDTO> itens = new ArrayList<>();
    ItemPedidoDTO item = new ItemPedidoDTO();
    item.setProdutoId(produtoSalvo.getId());
    item.setQuantidade(2);
    itens.add(item);
    pedidoDTO.setItens(itens);

    // Criar pedido para testes de busca
    Pedido pedido = new Pedido();
    pedido.setCliente(clienteSalvo);
    pedido.setRestaurante(restauranteSalvo);
    pedido.setDataPedido(LocalDateTime.now());
    pedido.setStatus(StatusPedido.PENDENTE);
    pedido.setEnderecoEntrega("Rua A, 123 - São Paulo/SP");
    pedido.setSubtotal(new BigDecimal("91.80"));
    pedido.setTaxaEntrega(new BigDecimal("8.50"));
    pedido.setValorTotal(new BigDecimal("100.30"));
    pedido.setCep("46464584");
    pedido.setFormaPagamento("PIX");
    pedidoSalvo = pedidoRepository.save(pedido);
  }

  @Test
  void deveCriarPedidoComSucesso() throws Exception {
    mockMvc.perform(post("/api/pedidos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(pedidoDTO)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.clienteId").value(clienteSalvo.getId()))
        .andExpect(jsonPath("$.data.restauranteId").value(restauranteSalvo.getId()))
        .andExpect(jsonPath("$.data.status").value("PENDENTE"))
        .andExpect(jsonPath("$.data.subtotal").value(91.80))
        .andExpect(jsonPath("$.data.taxaEntrega").value(8.50))
        .andExpect(jsonPath("$.data.valorTotal").value(100.30))
        .andExpect(jsonPath("$.data.itens", hasSize(1)))
        .andExpect(jsonPath("$.message").value("Pedido criado com sucesso"));
  }

  @Test
  void deveRejeitarPedidoComDadosInvalidos() throws Exception {
    pedidoDTO.setClienteId(null); // Cliente nulo
    pedidoDTO.setItens(new ArrayList<>()); // Sem itens

    mockMvc.perform(post("/api/pedidos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(pedidoDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
  }

  @Test
  void deveRejeitarPedidoComClienteInexistente() throws Exception {
    pedidoDTO.setClienteId(999L);

    mockMvc.perform(post("/api/pedidos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(pedidoDTO)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveRejeitarPedidoComRestauranteInexistente() throws Exception {
    pedidoDTO.setRestauranteId(999L);

    mockMvc.perform(post("/api/pedidos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(pedidoDTO)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveRejeitarPedidoComProdutoInexistente() throws Exception {
    pedidoDTO.getItens().get(0).setProdutoId(999L);

    mockMvc.perform(post("/api/pedidos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(pedidoDTO)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveBuscarPedidoPorId() throws Exception {
    mockMvc.perform(get("/api/pedidos/{id}", pedidoSalvo.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(pedidoSalvo.getId()))
        .andExpect(jsonPath("$.data.status").value("PENDENTE"))
        .andExpect(jsonPath("$.data.valorTotal").value(100.30))
        .andExpect(jsonPath("$.message").value("Pedido retornado com sucesso"));
  }

  @Test
  void deveRetornar404ParaPedidoInexistente() throws Exception {
    mockMvc.perform(get("/api/pedidos/{id}", 999L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveListarPedidosPorCliente() throws Exception {
    mockMvc.perform(get("/api/pedidos/cliente/{clienteId}", clienteSalvo.getId())
        .param("page", "0")
        .param("size", "10"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].clienteId").value(clienteSalvo.getId()))
        .andExpect(jsonPath("$.page.number").value(0))
        .andExpect(jsonPath("$.page.size").value(10))
        .andExpect(jsonPath("$.page.totalElements").value(1));
  }

  @Test
  void deveAtualizarStatusDoPedido() throws Exception {
    StatusPedidoDTO statusDTO = new StatusPedidoDTO();
    statusDTO.setStatus(StatusPedido.CONFIRMADO);

    mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoSalvo.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(statusDTO)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.status").value("CONFIRMADO"))
        .andExpect(jsonPath("$.message").value("Status do pedido atualizado com sucesso"));
  }

  @Test
  void deveRejeitarTransicaoDeStatusInvalida() throws Exception {
    // Tentar mudar de PENDENTE direto para ENTREGUE (transição inválida)
    StatusPedidoDTO statusDTO = new StatusPedidoDTO();
    statusDTO.setStatus(StatusPedido.ENTREGUE);

    mockMvc.perform(patch("/api/pedidos/{id}/status", pedidoSalvo.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(statusDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("BUSINESS_ERROR"));
  }

  @Test
  void deveCancelarPedidoComSucesso() throws Exception {
    mockMvc.perform(delete("/api/pedidos/{id}", pedidoSalvo.getId()))
        .andDo(print())
        .andExpect(status().isNoContent());

    // Verificar se o pedido foi realmente cancelado
    mockMvc.perform(get("/api/pedidos/{id}", pedidoSalvo.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("CANCELADO"));
  }

  @Test
  void deveRejeitarCancelamentoDePedidoEmAndamento() throws Exception {
    // Atualizar status para SAIU_PARA_ENTREGA
    pedidoSalvo.setStatus(StatusPedido.SAIU_PARA_ENTREGA);
    pedidoRepository.save(pedidoSalvo);

    mockMvc.perform(delete("/api/pedidos/{id}", pedidoSalvo.getId()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("BUSINESS_ERROR"));
  }

  @Test
  void deveCalcularTotalDoPedido() throws Exception {
    List<ItemPedidoDTO> itens = new ArrayList<>();

    ItemPedidoDTO item1 = new ItemPedidoDTO();
    item1.setProdutoId(produtoSalvo.getId());
    item1.setQuantidade(2);
    itens.add(item1);

    mockMvc.perform(post("/api/pedidos/calcular")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(itens)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").value(91.80)) // 2 * 45.90
        .andExpect(jsonPath("$.message").value("Total do pedido calculado com sucesso"));
  }

  @Test
  void deveRetornarErroAoCalcularTotalComProdutoInexistente() throws Exception {
    List<ItemPedidoDTO> itens = new ArrayList<>();

    ItemPedidoDTO item = new ItemPedidoDTO();
    item.setProdutoId(999L);
    item.setQuantidade(2);
    itens.add(item);

    mockMvc.perform(post("/api/pedidos/calcular")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(itens)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveRejeitarPedidoComClienteInativo() throws Exception {
    // Desativar cliente
    clienteSalvo.setAtivo(false);
    clienteRepository.save(clienteSalvo);

    mockMvc.perform(post("/api/pedidos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(pedidoDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("BUSINESS_ERROR"));
  }

  @Test
  void deveRejeitarPedidoComRestauranteInativo() throws Exception {
    // Desativar restaurante
    restauranteSalvo.setAtivo(false);
    restauranteRepository.save(restauranteSalvo);

    mockMvc.perform(post("/api/pedidos")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(pedidoDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("BUSINESS_ERROR"));
  }
}