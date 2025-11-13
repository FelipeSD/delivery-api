package com.deliverytech.delivery_api.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.deliverytech.delivery_api.auth.model.Usuario;
import com.deliverytech.delivery_api.auth.repository.UsuarioRepository;
import com.deliverytech.delivery_api.common.exceptions.BusinessException;
import com.deliverytech.delivery_api.common.exceptions.EntityNotFoundException;
import com.deliverytech.delivery_api.common.exceptions.InactiveEntityException;
import com.deliverytech.delivery_api.common.exceptions.OrderStatusException;
import com.deliverytech.delivery_api.common.monitoring.metrics.MetricsService;
import com.deliverytech.delivery_api.pedido.dto.PedidoDTO;
import com.deliverytech.delivery_api.pedido.dto.PedidoItemDTO;
import com.deliverytech.delivery_api.pedido.dto.PedidoResponseDTO;
import com.deliverytech.delivery_api.pedido.model.Pedido;
import com.deliverytech.delivery_api.pedido.model.PedidoStatus;
import com.deliverytech.delivery_api.pedido.repository.PedidoRepository;
import com.deliverytech.delivery_api.pedido.service.PedidoServiceImpl;
import com.deliverytech.delivery_api.produto.model.Produto;
import com.deliverytech.delivery_api.produto.repository.ProdutoRepository;
import com.deliverytech.delivery_api.restaurante.model.Restaurante;
import com.deliverytech.delivery_api.restaurante.repository.RestauranteRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoServiceImpl - Testes Unitários")
class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RestauranteRepository restauranteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MetricsService metricsService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Usuario usuario;
    private Restaurante restaurante;
    private Produto produto;
    private Pedido pedido;
    private PedidoDTO pedidoDTO;
    private PedidoItemDTO itemPedidoDTO;

    @BeforeEach
    void setUp() {
        // Setup Usuario
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Cliente Teste");
        usuario.setEmail("cliente@teste.com");
        usuario.setAtivo(true);

        // Setup Restaurante
        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNome("Restaurante Teste");
        restaurante.setTaxaEntrega(BigDecimal.valueOf(5.00));
        restaurante.setAtivo(true);

        // Setup Produto
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Pizza");
        produto.setPreco(BigDecimal.valueOf(30.00));
        produto.setDisponivel(true);
        produto.setRestaurante(restaurante);

        // Setup ItemPedidoDTO
        itemPedidoDTO = new PedidoItemDTO();
        itemPedidoDTO.setProdutoId(1L);
        itemPedidoDTO.setQuantidade(2);
        itemPedidoDTO.setObservacoes("Sem cebola");

        // Setup PedidoDTO
        pedidoDTO = new PedidoDTO();
        pedidoDTO.setUsuarioId(1L);
        pedidoDTO.setRestauranteId(1L);
        pedidoDTO.setItens(Arrays.asList(itemPedidoDTO));
        pedidoDTO.setEnderecoEntrega("Rua Teste, 123");
        pedidoDTO.setCep("14870000");
        pedidoDTO.setFormaPagamento("CARTAO");

        // Setup Pedido
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setNumeroPedido("PED-123456");
        pedido.setUsuario(usuario);
        pedido.setRestaurante(restaurante);
        pedido.setStatus(PedidoStatus.PENDENTE);
        pedido.setSubtotal(BigDecimal.valueOf(60.00));
        pedido.setTaxaEntrega(BigDecimal.valueOf(5.00));
        pedido.setValorTotal(BigDecimal.valueOf(65.00));
        pedido.setDataPedido(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso")
    void deveCriarPedidoComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(modelMapper.map(any(Pedido.class), eq(PedidoResponseDTO.class)))
                .thenReturn(new PedidoResponseDTO());

        PedidoResponseDTO result = pedidoService.criarPedido(pedidoDTO);

        assertNotNull(result);
        verify(usuarioRepository).findById(1L);
        verify(restauranteRepository).findById(1L);
        verify(produtoRepository).findById(1L);
        verify(pedidoRepository).save(any(Pedido.class));
        verify(metricsService).incrementarPedidosComSucesso();
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pedido com usuário inexistente")
    void deveLancarExcecaoAoCriarPedidoComUsuarioInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> pedidoService.criarPedido(pedidoDTO));
        verify(usuarioRepository).findById(1L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pedido com usuário inativo")
    void deveLancarExcecaoAoCriarPedidoComUsuarioInativo() {
        usuario.setAtivo(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThrows(InactiveEntityException.class,
                () -> pedidoService.criarPedido(pedidoDTO));
        verify(usuarioRepository).findById(1L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pedido com restaurante inexistente")
    void deveLancarExcecaoAoCriarPedidoComRestauranteInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> pedidoService.criarPedido(pedidoDTO));
        verify(restauranteRepository).findById(1L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pedido com restaurante inativo")
    void deveLancarExcecaoAoCriarPedidoComRestauranteInativo() {
        restaurante.setAtivo(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

        assertThrows(InactiveEntityException.class,
                () -> pedidoService.criarPedido(pedidoDTO));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pedido com produto inexistente")
    void deveLancarExcecaoAoCriarPedidoComProdutoInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> pedidoService.criarPedido(pedidoDTO));
        verify(produtoRepository).findById(1L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pedido com produto indisponível")
    void deveLancarExcecaoAoCriarPedidoComProdutoIndisponivel() {
        produto.setDisponivel(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        assertThrows(BusinessException.class,
                () -> pedidoService.criarPedido(pedidoDTO));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar pedido com produto de outro restaurante")
    void deveLancarExcecaoAoCriarPedidoComProdutoDeOutroRestaurante() {
        Restaurante outroRestaurante = new Restaurante();
        outroRestaurante.setId(2L);
        produto.setRestaurante(outroRestaurante);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        assertThrows(BusinessException.class,
                () -> pedidoService.criarPedido(pedidoDTO));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar pedido por ID com sucesso")
    void deveBuscarPedidoPorId() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(modelMapper.map(pedido, PedidoResponseDTO.class))
                .thenReturn(new PedidoResponseDTO());

        PedidoResponseDTO result = pedidoService.buscarPedidoPorId(1L);

        assertNotNull(result);
        verify(pedidoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar pedido inexistente")
    void deveLancarExcecaoAoBuscarPedidoInexistente() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> pedidoService.buscarPedidoPorId(1L));
        verify(pedidoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve buscar pedidos por usuário")
    void deveBuscarPedidosPorUsuario() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        Page<Pedido> page = new PageImpl<>(Arrays.asList(pedido));
        when(pedidoRepository.findByUsuarioIdOrderByDataPedidoDesc(eq(1L), any(Pageable.class)))
                .thenReturn(page);
        when(modelMapper.map(any(Pedido.class), eq(PedidoResponseDTO.class)))
                .thenReturn(new PedidoResponseDTO());

        Page<PedidoResponseDTO> result = pedidoService.buscarPedidosPorUsuario(1L, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(usuarioRepository).existsById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar pedidos de usuário inexistente")
    void deveLancarExcecaoAoBuscarPedidosDeUsuarioInexistente() {
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> pedidoService.buscarPedidosPorUsuario(1L, Pageable.unpaged()));
        verify(usuarioRepository).existsById(1L);
    }

    @Test
    @DisplayName("Deve buscar pedidos por restaurante")
    void deveBuscarPedidosPorRestaurante() {
        when(restauranteRepository.existsById(1L)).thenReturn(true);
        Page<Pedido> page = new PageImpl<>(Arrays.asList(pedido));
        when(pedidoRepository.findByRestauranteIdOrderByDataPedidoDesc(eq(1L), any(Pageable.class)))
                .thenReturn(page);
        when(modelMapper.map(any(Pedido.class), eq(PedidoResponseDTO.class)))
                .thenReturn(new PedidoResponseDTO());

        Page<PedidoResponseDTO> result = pedidoService.buscarPedidosPorRestaurante(1L, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(restauranteRepository).existsById(1L);
    }

    @Test
    @DisplayName("Deve atualizar status do pedido com sucesso")
    void deveAtualizarStatusDoPedido() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(modelMapper.map(pedido, PedidoResponseDTO.class))
                .thenReturn(new PedidoResponseDTO());

        PedidoResponseDTO result = pedidoService.atualizarStatusPedido(1L, PedidoStatus.CONFIRMADO);

        assertNotNull(result);
        verify(pedidoRepository).findById(1L);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar status inválido")
    void deveLancarExcecaoAoAtualizarStatusInvalido() {
        pedido.setStatus(PedidoStatus.ENTREGUE);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(OrderStatusException.class,
                () -> pedidoService.atualizarStatusPedido(1L, PedidoStatus.CONFIRMADO));
        verify(pedidoRepository).findById(1L);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve calcular total do pedido corretamente")
    void deveCalcularTotalDoPedido() {
        List<PedidoItemDTO> itens = Arrays.asList(itemPedidoDTO);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        BigDecimal total = pedidoService.calcularTotalPedido(itens);

        assertNotNull(total);
        assertEquals(BigDecimal.valueOf(60.00), total);
        verify(produtoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve cancelar pedido com sucesso")
    void deveCancelarPedidoComSucesso() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        assertDoesNotThrow(() -> pedidoService.cancelarPedido(1L));
        verify(pedidoRepository).findById(1L);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar pedido com status inválido")
    void deveLancarExcecaoAoCancelarPedidoComStatusInvalido() {
        pedido.setStatus(PedidoStatus.ENTREGUE);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(OrderStatusException.class,
                () -> pedidoService.cancelarPedido(1L));
        verify(pedidoRepository).findById(1L);
        verify(pedidoRepository, never()).save(any());
    }
}