package com.deliverytech.delivery_api.controllers;

import static com.deliverytech.delivery_api.matchers.ApiResponseMatchers.erro;
import static com.deliverytech.delivery_api.matchers.ApiResponseMatchers.mensagem;
import static com.deliverytech.delivery_api.matchers.ApiResponseMatchers.sucesso;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.deliverytech.delivery_api.base.BaseIntegrationTest;
import com.deliverytech.delivery_api.dtos.ItemPedidoDTO;
import com.deliverytech.delivery_api.dtos.PedidoDTO;
import com.deliverytech.delivery_api.dtos.StatusPedidoDTO;
import com.deliverytech.delivery_api.entities.Pedido;
import com.deliverytech.delivery_api.entities.Produto;
import com.deliverytech.delivery_api.entities.Restaurante;
import com.deliverytech.delivery_api.entities.Usuario;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.factories.EntityFactory;
import com.deliverytech.delivery_api.factories.UsuarioFactory;
import com.deliverytech.delivery_api.repositories.PedidoRepository;
import com.deliverytech.delivery_api.repositories.ProdutoRepository;
import com.deliverytech.delivery_api.repositories.RestauranteRepository;
import com.deliverytech.delivery_api.repositories.UsuarioRepository;

@DisplayName("Teste de Integração do PedidoController")
class PedidoControllerIT extends BaseIntegrationTest {

  @Autowired
  private PedidoRepository pedidoRepository;

  @Autowired
  private RestauranteRepository restauranteRepository;

  @Autowired
  private ProdutoRepository produtoRepository;

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private UsuarioFactory usuarioFactory;

  private Usuario usuarioSalvo;
  private Restaurante restauranteSalvo;
  private Produto produtoSalvo;
  private Pedido pedidoSalvo;
  private PedidoDTO pedidoDTO;
  private String clienteJwtToken;
  private String restauranteJwtToken;
  private String adminJwtToken;

  // Constantes para valores esperados
  private static final BigDecimal PRECO_UNITARIO = new BigDecimal("45.90");
  private static final int QUANTIDADE = 2;
  private static final BigDecimal TAXA_ENTREGA = new BigDecimal("8.50");
  private static final BigDecimal SUBTOTAL_ESPERADO = new BigDecimal("91.80"); // 45.90 * 2
  private static final BigDecimal VALOR_TOTAL_ESPERADO = new BigDecimal("100.30"); // 91.80 + 8.50

  @BeforeEach
  void setUp() throws Exception {
    // 1. PRIMEIRO: Criar e salvar TODOS os usuários
    var usuarioCliente = usuarioRepository.save(usuarioFactory.criarUsuarioCliente());
    usuarioSalvo = usuarioCliente;

    var usuarioAdmin = usuarioRepository.save(usuarioFactory.criarUsuarioAdmin());

    // 2. Criar restaurante
    restauranteSalvo = restauranteRepository.save(EntityFactory.criarRestaurante());

    // 3. Criar usuário do restaurante (após restaurante existir)
    var usuarioRestaurante = usuarioRepository.save(usuarioFactory.criarUsuarioRestaurante(restauranteSalvo));

    assertEquals(restauranteSalvo.getId(), usuarioRestaurante.getRestaurante().getId(), "Usuário do restaurante não está correto");

    // 4. Criar produto (após restaurante existir)
    produtoSalvo = produtoRepository.save(EntityFactory.criarProduto(restauranteSalvo));

    // IMPORTANTE: Validar que o produto foi salvo com o preço correto
    assertEquals(PRECO_UNITARIO, produtoSalvo.getPreco(), "Preço do produto deve ser R$ 45.90");

    // 5. AGORA SIM: Criar pedido (usuário e restaurante já existem)
    pedidoSalvo = pedidoRepository.save(EntityFactory.criarPedido(usuarioSalvo, restauranteSalvo));

    // 6. Gerar tokens JWT
    clienteJwtToken = loginAndGetToken(usuarioCliente.getEmail(), "123456");
    restauranteJwtToken = loginAndGetToken(usuarioRestaurante.getEmail(), "123456");
    adminJwtToken = loginAndGetToken(usuarioAdmin.getEmail(), "123456");

    // 7. DTO base (sem clienteId, lembre-se!)
    pedidoDTO = EntityFactory.criarPedidoDTO(usuarioSalvo.getId(), restauranteSalvo.getId(), produtoSalvo.getId());
  }

  @Test
  void deveCriarPedidoComSucesso() throws Exception {
    postJson("/api/pedidos", clienteJwtToken, pedidoDTO)
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.data.usuario.id").value(usuarioSalvo.getId()))
        .andExpect(jsonPath("$.data.restaurante.id").value(restauranteSalvo.getId()))
        .andExpect(jsonPath("$.data.status").value("PENDENTE"))
        .andExpect(jsonPath("$.data.subtotal").value(SUBTOTAL_ESPERADO.doubleValue()))
        .andExpect(jsonPath("$.data.taxaEntrega").value(TAXA_ENTREGA.doubleValue()))
        .andExpect(jsonPath("$.data.valorTotal").value(VALOR_TOTAL_ESPERADO.doubleValue()))
        .andExpect(jsonPath("$.data.itens", hasSize(1)))
        .andExpect(mensagem("Pedido criado com sucesso"));
  }

  @Test
  void deveRejeitarPedidoComDadosInvalidos() throws Exception {
    PedidoDTO dtoInvalido = EntityFactory.criarPedidoDTOInvalido();

    postJson("/api/pedidos", clienteJwtToken, dtoInvalido)
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("VALIDATION_ERROR"));
  }

  @Test
  void deveRejeitarPedidoComUsuarioInexistente() throws Exception {
    pedidoDTO.setUsuarioId(999L);

    postJson("/api/pedidos", clienteJwtToken, pedidoDTO)
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveRejeitarPedidoComRestauranteInexistente() throws Exception {
    pedidoDTO.setRestauranteId(999L);

    postJson("/api/pedidos", clienteJwtToken, pedidoDTO)
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveRejeitarPedidoComProdutoInexistente() throws Exception {
    pedidoDTO.getItens().get(0).setProdutoId(999L);

    postJson("/api/pedidos", clienteJwtToken, pedidoDTO)
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveBuscarPedidoPorId() throws Exception {
    getJson("/api/pedidos/{id}", clienteJwtToken, pedidoSalvo.getId())
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.data.id").value(pedidoSalvo.getId()))
        .andExpect(jsonPath("$.data.status").value("PENDENTE"))
        .andExpect(jsonPath("$.data.valorTotal").value(VALOR_TOTAL_ESPERADO.doubleValue()))
        .andExpect(mensagem("Pedido retornado com sucesso"));
  }

  @Test
  void deveRetornar404ParaPedidoInexistente() throws Exception {
    getJson("/api/pedidos/{id}", clienteJwtToken, 999L)
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveListarPedidosPorUsuario() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("page", "0");
    params.put("size", "10");

    getJson("/api/pedidos/usuario/{usuarioId}", adminJwtToken, params, usuarioSalvo.getId())
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].usuario.id").value(usuarioSalvo.getId()))
        .andExpect(jsonPath("$.page.number").value(0))
        .andExpect(jsonPath("$.page.size").value(10))
        .andExpect(jsonPath("$.page.totalElements").value(1));
  }

  @Test
  void deveListarPedidosPorRestaurante() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("page", "0");
    params.put("size", "10");

    getJson("/api/pedidos/restaurante/{restauranteId}", restauranteJwtToken, params, restauranteSalvo.getId())
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].restaurante.id").value(restauranteSalvo.getId()))
        .andExpect(jsonPath("$.page.totalElements").value(1));
  }

  @Test
  void deveListarPedidosPorStatus() throws Exception {
    Map<String, String> params = new HashMap<>();
    params.put("status", "PENDENTE");
    params.put("page", "0");
    params.put("size", "10");

    getJson("/api/pedidos/meus", clienteJwtToken, params)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].status").value("PENDENTE"));
  }

  @Test
  void deveAtualizarStatusDoPedido() throws Exception {
    StatusPedidoDTO statusDTO = EntityFactory.criarStatusPedidoDTO(StatusPedido.CONFIRMADO);

    patchJson("/api/pedidos/{id}/status", restauranteJwtToken, statusDTO, pedidoSalvo.getId())
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.data.status").value("CONFIRMADO"))
        .andExpect(mensagem("Status do pedido atualizado com sucesso"));
  }

  @Test
  void deveRejeitarTransicaoDeStatusInvalida() throws Exception {
    // Tentar mudar de PENDENTE direto para ENTREGUE (transição inválida)
    StatusPedidoDTO statusDTO = EntityFactory.criarStatusPedidoDTO(StatusPedido.ENTREGUE);

    patchJson("/api/pedidos/{id}/status", restauranteJwtToken, statusDTO, pedidoSalvo.getId())
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("BUSINESS_ERROR"));
  }

  @Test
  void devePermitirTransicaoDeStatusValida() throws Exception {
    // PENDENTE -> CONFIRMADO
    StatusPedidoDTO statusDTO1 = EntityFactory.criarStatusPedidoDTO(StatusPedido.CONFIRMADO);
    patchJson("/api/pedidos/{id}/status", restauranteJwtToken, statusDTO1, pedidoSalvo.getId())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("CONFIRMADO"));

    // CONFIRMADO -> PREPARANDO
    StatusPedidoDTO statusDTO2 = EntityFactory.criarStatusPedidoDTO(StatusPedido.PREPARANDO);
    patchJson("/api/pedidos/{id}/status", restauranteJwtToken, statusDTO2, pedidoSalvo.getId())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("PREPARANDO"));

    // EM_PREPARO -> SAIU_PARA_ENTREGA
    StatusPedidoDTO statusDTO3 = EntityFactory.criarStatusPedidoDTO(StatusPedido.SAIU_PARA_ENTREGA);
    patchJson("/api/pedidos/{id}/status", restauranteJwtToken, statusDTO3, pedidoSalvo.getId())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("SAIU_PARA_ENTREGA"));
  }

  @Test
  void deveCancelarPedidoComSucesso() throws Exception {
    deleteJson("/api/pedidos/{id}", clienteJwtToken, pedidoSalvo.getId())
        .andDo(print())
        .andExpect(status().isNoContent());

    // Verificar se o pedido foi realmente cancelado
    getJson("/api/pedidos/{id}", clienteJwtToken, pedidoSalvo.getId())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("CANCELADO"));
  }

  @Test
  void deveRejeitarCancelamentoDePedidoEmAndamento() throws Exception {
    // Atualizar status para SAIU_PARA_ENTREGA
    pedidoSalvo.setStatus(StatusPedido.SAIU_PARA_ENTREGA);
    pedidoRepository.save(pedidoSalvo);

    deleteJson("/api/pedidos/{id}", clienteJwtToken, pedidoSalvo.getId())
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("BUSINESS_ERROR"));
  }

  @Test
  void deveRejeitarCancelamentoDePedidoJaCancelado() throws Exception {
    // Cancelar pedido
    pedidoSalvo.setStatus(StatusPedido.CANCELADO);
    pedidoRepository.save(pedidoSalvo);

    deleteJson("/api/pedidos/{id}", clienteJwtToken, pedidoSalvo.getId())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("BUSINESS_ERROR"));
  }

  @Test
  void deveCalcularTotalDoPedido() throws Exception {
    List<ItemPedidoDTO> itens = new ArrayList<>();
    itens.add(EntityFactory.criarItemPedidoDTO(produtoSalvo.getId(), QUANTIDADE));

    postJson("/api/pedidos/calcular", clienteJwtToken, itens)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.data").value(SUBTOTAL_ESPERADO.doubleValue())) // 2 * 45.90
        .andExpect(mensagem("Total do pedido calculado com sucesso"));
  }

  @Test
  void deveCalcularTotalComMultiplosProdutos() throws Exception {
    // Criar outro produto
    Produto produto2 = new Produto();
    produto2.setNome("Pizza Calabresa");
    produto2.setPreco(new BigDecimal("39.90"));
    produto2.setCategoria("Pizzas");
    produto2.setRestaurante(restauranteSalvo);
    produto2.setDisponivel(true);
    produto2 = produtoRepository.save(produto2);

    List<ItemPedidoDTO> itens = new ArrayList<>();
    itens.add(EntityFactory.criarItemPedidoDTO(produtoSalvo.getId(), 2)); // 91.80
    itens.add(EntityFactory.criarItemPedidoDTO(produto2.getId(), 1)); // 39.90

    postJson("/api/pedidos/calcular", clienteJwtToken, itens)
        .andExpect(status().isOk())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.data").value(131.70)); // 91.80 + 39.90
  }

  @Test
  void deveRetornarErroAoCalcularTotalComProdutoInexistente() throws Exception {
    List<ItemPedidoDTO> itens = new ArrayList<>();
    itens.add(EntityFactory.criarItemPedidoDTO(999L, 2));

    postJson("/api/pedidos/calcular", clienteJwtToken, itens)
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveRejeitarPedidoComUsuarioInativo() throws Exception {
    // Desativar usuario
    usuarioSalvo.setAtivo(false);
    usuarioRepository.save(usuarioSalvo);

    postJson("/api/pedidos", clienteJwtToken, pedidoDTO)
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("BUSINESS_ERROR"));
  }

  @Test
  void deveRejeitarPedidoComRestauranteInativo() throws Exception {
    // Desativar restaurante
    restauranteSalvo.setAtivo(false);
    restauranteRepository.save(restauranteSalvo);

    postJson("/api/pedidos", clienteJwtToken, pedidoDTO)
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("BUSINESS_ERROR"));
  }

  @Test
  void deveRejeitarPedidoComProdutoIndisponivel() throws Exception {
    // Tornar produto indisponível
    produtoSalvo.setDisponivel(false);
    produtoRepository.save(produtoSalvo);

    postJson("/api/pedidos", clienteJwtToken, pedidoDTO)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("BUSINESS_ERROR"));
  }

  @Test
  void deveRetornarHistoricoDePedidosDoUsuario() throws Exception {
    // Criar mais pedidos com diferentes status
    Pedido pedido2 = EntityFactory.criarPedido(usuarioSalvo, restauranteSalvo);
    pedido2.setStatus(StatusPedido.ENTREGUE);
    pedidoRepository.save(pedido2);

    Pedido pedido3 = EntityFactory.criarPedido(usuarioSalvo, restauranteSalvo);
    pedido3.setStatus(StatusPedido.CANCELADO);
    pedidoRepository.save(pedido3);

    Map<String, String> params = new HashMap<>();
    params.put("page", "0");
    params.put("size", "10");

    getJson("/api/pedidos/usuario/{usuarioId}", adminJwtToken, params, usuarioSalvo.getId())
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(sucesso())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(3)))
        .andExpect(jsonPath("$.page.totalElements").value(3));
  }

  @Test
  void deveRetornar404AoAtualizarStatusDePedidoInexistente() throws Exception {
    StatusPedidoDTO statusDTO = EntityFactory.criarStatusPedidoDTO(StatusPedido.CONFIRMADO);

    patchJson("/api/pedidos/{id}/status", restauranteJwtToken, statusDTO, 999L)
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(erro("ENTITY_NOT_FOUND"));
  }
}