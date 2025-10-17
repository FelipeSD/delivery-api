package com.deliverytech.delivery_api.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
import com.deliverytech.delivery_api.repositories.ClienteRepository;
import com.deliverytech.delivery_api.repositories.PedidoRepository;
import com.deliverytech.delivery_api.repositories.ProdutoRepository;
import com.deliverytech.delivery_api.repositories.RestauranteRepository;

@Service
@Transactional
public class PedidoServiceImpl implements PedidoService {

  @Autowired
  private PedidoRepository pedidoRepository;

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private RestauranteRepository restauranteRepository;

  @Autowired
  private ProdutoRepository produtoRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  @Transactional
  public PedidoResponseDTO criarPedido(PedidoDTO dto) {
    // 1. Validar cliente existe e está ativo
    Cliente cliente = clienteRepository.findById(dto.getClienteId())
        .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

    if (!cliente.isAtivo()) {
      throw new BusinessException("Cliente inativo não pode fazer pedidos");
    }

    // 2. Validar restaurante existe e está ativo
    Restaurante restaurante = restauranteRepository.findById(dto.getRestauranteId())
        .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado"));

    if (!restaurante.isAtivo()) {
      throw new BusinessException("Restaurante não está disponível");
    }

    // 3. Validar todos os produtos existem e estão disponíveis
    List<ItemPedido> itensPedido = new ArrayList<ItemPedido>();
    BigDecimal subtotal = BigDecimal.ZERO;

    for (ItemPedidoDTO itemDTO : dto.getItens()) {
      Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
          .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + itemDTO.getProdutoId()));

      if (!produto.isDisponivel()) {
        throw new BusinessException("Produto indisponível: " + produto.getNome());
      }

      if (!produto.getRestaurante().getId().equals(dto.getRestauranteId())) {
        throw new BusinessException("Produto não pertence ao restaurante selecionado");
      }

      // Criar item do pedido
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

    // 8. Retornar pedido criado
    return modelMapper.map(pedidoSalvo, PedidoResponseDTO.class);
  }

  @Override
  @Transactional(readOnly = true)
  public PedidoResponseDTO buscarPedidoPorId(Long id) {
    Pedido pedido = pedidoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + id));

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
  public PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedido novoStatus) {
    Pedido pedido = pedidoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

    // Validar transições de status permitidas
    if (!isTransicaoValida(pedido.getStatus(), novoStatus)) {
      throw new BusinessException("Transição de status inválida: " + pedido.getStatus() + " -> " + novoStatus);
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
          .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

      BigDecimal subtotalItem = produto.getPreco()
          .multiply(BigDecimal.valueOf(item.getQuantidade()));
      total = total.add(subtotalItem);
    }

    return total;
  }

  @Override
  public void cancelarPedido(Long id) {
    Pedido pedido = pedidoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

    if (!podeSerCancelado(pedido.getStatus())) {
      throw new BusinessException("Pedido não pode ser cancelado no status: " +
          pedido.getStatus());
    }

    pedido.setStatus(StatusPedido.CANCELADO);
    pedidoRepository.save(pedido);
  }

  private boolean isTransicaoValida(StatusPedido statusAtual, StatusPedido novoStatus) {
    // Implementar lógica de transições válidas
    switch (statusAtual) {
      case PENDENTE:
        return novoStatus == StatusPedido.CONFIRMADO || novoStatus == StatusPedido.CANCELADO;
      case CONFIRMADO:
        return novoStatus == StatusPedido.PREPARANDO || novoStatus == StatusPedido.CANCELADO;
      case PREPARANDO:
        return novoStatus == StatusPedido.SAIU_PARA_ENTREGA;
      case SAIU_PARA_ENTREGA:
        return novoStatus == StatusPedido.ENTREGUE;
      default:
        return false;
    }
  }

  private boolean podeSerCancelado(StatusPedido status) {
    return status == StatusPedido.PENDENTE || status == StatusPedido.CONFIRMADO;
  }
}