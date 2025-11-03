package com.deliverytech.delivery_api.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.dtos.ItemPedidoDTO;
import com.deliverytech.delivery_api.dtos.PedidoDTO;
import com.deliverytech.delivery_api.dtos.PedidoResponseDTO;
import com.deliverytech.delivery_api.entities.Cliente;
import com.deliverytech.delivery_api.entities.ItemPedido;
import com.deliverytech.delivery_api.entities.Pedido;
import com.deliverytech.delivery_api.entities.Produto;
import com.deliverytech.delivery_api.entities.Restaurante;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.exceptions.BusinessException;
import com.deliverytech.delivery_api.exceptions.EntityNotFoundException;
import com.deliverytech.delivery_api.exceptions.InactiveEntityException;
import com.deliverytech.delivery_api.exceptions.OrderStatusException;
import com.deliverytech.delivery_api.monitoring.metrics.MetricsService;
import com.deliverytech.delivery_api.repositories.ClienteRepository;
import com.deliverytech.delivery_api.repositories.PedidoRepository;
import com.deliverytech.delivery_api.repositories.ProdutoRepository;
import com.deliverytech.delivery_api.repositories.RestauranteRepository;
import com.deliverytech.delivery_api.security.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service("pedidoService")
@Transactional
@Primary
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

  @Autowired
  private PedidoRepository pedidoRepository;

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private RestauranteRepository restauranteRepository;

  @Autowired
  private ProdutoRepository produtoRepository;

  private final MetricsService metricsService;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public PedidoResponseDTO criarPedido(PedidoDTO dto) {
    // 1. Validar cliente existe e está ativo
    Cliente cliente = clienteRepository.findById(dto.getClienteId())
        .orElseThrow(() -> new EntityNotFoundException("Cliente", dto.getClienteId()));

    if (!cliente.isAtivo()) {
      throw new InactiveEntityException("Cliente", dto.getClienteId());
    }

    // 2. Validar restaurante existe e está ativo
    Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
        .orElseThrow(() -> new EntityNotFoundException("Restaurante", dto.getRestauranteId()));

    if (!restaurante.isAtivo()) {
      throw new InactiveEntityException("Restaurante", dto.getRestauranteId());
    }

    // 3. Validar todos os produtos existem e estão disponíveis
    List<ItemPedido> itensPedido = new ArrayList<>();
    BigDecimal subtotal = BigDecimal.ZERO;

    for (ItemPedidoDTO itemDTO : dto.getItens()) {
      Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
          .orElseThrow(() -> new EntityNotFoundException("Produto", itemDTO.getProdutoId()));

      if (!produto.isDisponivel()) {
        throw new BusinessException("Produto indisponível:", produto.getNome());
      }

      if (!produto.getRestaurante().getId().equals(dto.getRestauranteId())) {
        throw new BusinessException("Produto não pertence ao restaurante selecionado",
            produto.getRestaurante().getNome());
      }

      // Criar ítem do pedido
      ItemPedido item = new ItemPedido();
      item.setProduto(produto);
      item.setQuantidade(itemDTO.getQuantidade());
      item.setPrecoUnitario(produto.getPreco());

      item.setSubtotal(produto.getPreco().multiply(BigDecimal.valueOf(itemDTO.getQuantidade())));

      itensPedido.add(item);
      subtotal = subtotal.add(item.getSubtotal());
    }

    // 4. Calcular total do pedido
    BigDecimal taxaEntrega = restaurante.getTaxaEntrega();
    BigDecimal valorTotal = subtotal.add(taxaEntrega);

    // 5. Salvar pedido
    Pedido pedido = new Pedido();
    pedido.setCliente(cliente);
    pedido.setRestaurante(restaurante);
    pedido.setDataPedido(LocalDateTime.now());
    pedido.setStatus(StatusPedido.PENDENTE);
    pedido.setEnderecoEntrega(dto.getEnderecoEntrega());
    pedido.setSubtotal(subtotal);
    pedido.setTaxaEntrega(taxaEntrega);
    pedido.setValorTotal(valorTotal);

    Pedido pedidoSalvo = pedidoRepository.save(pedido);

    // 6. Salvar itens do pedido
    for (ItemPedido item : itensPedido) {
      item.setPedido(pedidoSalvo);
    }
    pedidoSalvo.setItens(itensPedido);

    // 7. Atualizar estoque (se aplicável) - Simulação
    // Em um cenário real, aqui seria decrementado o estoque

    metricsService.incrementarPedidosComSucesso();

    // 8. Retornar pedido criado
    return modelMapper.map(pedidoSalvo, PedidoResponseDTO.class);
  }

  @Override
  @Transactional(readOnly = true)
  public PedidoResponseDTO buscarPedidoPorId(Long id) {
    Pedido pedido = pedidoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Pedido", id));

    return modelMapper.map(pedido, PedidoResponseDTO.class);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PedidoResponseDTO> buscarPedidosPorCliente(Long clienteId, Pageable pageable) {
    Page<Pedido> pedidosPage = pedidoRepository.findByClienteIdOrderByDataPedidoDesc(clienteId, pageable);
    return pedidosPage.stream()
        .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
        .collect(Collectors.collectingAndThen(Collectors.toList(),
            list -> new PageImpl<>(list, pageable, list.size())));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PedidoResponseDTO> buscarMeusPedidos(Pageable pageable) {
    Long usuarioId = SecurityUtils.getCurrentUserId();
    Page<Pedido> pedidosPage = pedidoRepository.findByClienteIdOrderByDataPedidoDesc(usuarioId, pageable);
    return pedidosPage.stream()
        .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
        .collect(Collectors.collectingAndThen(Collectors.toList(),
            list -> new PageImpl<>(list, pageable, list.size())));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PedidoResponseDTO> buscarPedidosPorRestaurante(Long restauranteId, Pageable pageable) {
    Page<Pedido> pedidosPage = pedidoRepository.findByRestauranteIdOrderByDataPedidoDesc(restauranteId, pageable);
    return pedidosPage.stream()
        .map(pedido -> modelMapper.map(pedido, PedidoResponseDTO.class))
        .collect(Collectors.collectingAndThen(Collectors.toList(),
            list -> new PageImpl<>(list, pageable, list.size())));
  }

  @Override
  public PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedido novoStatus) {
    Pedido pedido = pedidoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Pedido", id));

    // Validar transições de status permitidas
    if (!isTransicaoValida(pedido.getStatus(), novoStatus)) {
      throw new OrderStatusException(id, pedido.getStatus().name(), novoStatus.name());
    }

    pedido.setStatus(novoStatus);
    Pedido pedidoAtualizado = pedidoRepository.save(pedido);

    return modelMapper.map(pedidoAtualizado, PedidoResponseDTO.class);
  }

  @Override
  @Transactional(readOnly = true)
  public BigDecimal calcularTotalPedido(List<ItemPedidoDTO> itens) {
    BigDecimal total = BigDecimal.ZERO;

    for (ItemPedidoDTO item : itens) {
      Produto produto = produtoRepository.findById(item.getProdutoId())
          .orElseThrow(() -> new EntityNotFoundException("Produto", item.getProdutoId()));

      BigDecimal subtotalItem = produto.getPreco()
          .multiply(BigDecimal.valueOf(item.getQuantidade()));
      total = total.add(subtotalItem);
    }

    return total;
  }

  @Override
  public void cancelarPedido(Long id) {
    Pedido pedido = pedidoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Pedido", id));

    if (!podeSerCancelado(pedido.getStatus())) {
      throw new OrderStatusException(id, pedido.getStatus().name(), "CANCELADO");
    }

    pedido.setStatus(StatusPedido.CANCELADO);
    pedidoRepository.save(pedido);
  }

  private boolean isTransicaoValida(StatusPedido statusAtual, StatusPedido novoStatus) {
    // Implementar lógica de transições válidas
    return switch (statusAtual) {
      case PENDENTE -> novoStatus == StatusPedido.CONFIRMADO || novoStatus == StatusPedido.CANCELADO;
      case CONFIRMADO -> novoStatus == StatusPedido.PREPARANDO || novoStatus == StatusPedido.CANCELADO;
      case PREPARANDO -> novoStatus == StatusPedido.SAIU_PARA_ENTREGA;
      case SAIU_PARA_ENTREGA -> novoStatus == StatusPedido.ENTREGUE;
      default -> false;
    };
  }

  private boolean podeSerCancelado(StatusPedido status) {
    return status == StatusPedido.PENDENTE || status == StatusPedido.CONFIRMADO;
  }

  @Override
  public boolean isOwner(Long pedidoId) {
    Long usuarioId = SecurityUtils.getCurrentUserId();
    Pedido pedido = pedidoRepository.findById(pedidoId)
        .orElseThrow(() -> new EntityNotFoundException("Pedido", pedidoId));

    return pedido.getCliente().getId().equals(usuarioId);
  }
}