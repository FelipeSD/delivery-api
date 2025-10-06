package com.deliverytech.delivery_api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.entities.Cliente;
import com.deliverytech.delivery_api.entities.ItemPedido;
import com.deliverytech.delivery_api.entities.Pedido;
import com.deliverytech.delivery_api.entities.Produto;
import com.deliverytech.delivery_api.entities.Restaurante;
import com.deliverytech.delivery_api.enums.StatusPedido;
import com.deliverytech.delivery_api.repositories.ClienteRepository;
import com.deliverytech.delivery_api.repositories.PedidoRepository;
import com.deliverytech.delivery_api.repositories.ProdutoRepository;
import com.deliverytech.delivery_api.repositories.RestauranteRepository;

@Service
@Transactional
public class PedidoService {

  @Autowired
  private PedidoRepository pedidoRepository;

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private RestauranteRepository restauranteRepository;

  @Autowired
  private ProdutoRepository produtoRepository;

  /**
   * Criar novo pedido
   */
  public Pedido criarPedido(Long clienteId, Long restauranteId) {
    Cliente cliente = clienteRepository.findById(clienteId)
        .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " +
            clienteId));

    Restaurante restaurante = restauranteRepository.findById(restauranteId)
        .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " +
            restauranteId));

    if (!cliente.isAtivo()) {
      throw new IllegalArgumentException("Cliente inativo não pode fazer pedidos");
    }

    if (!restaurante.isAtivo()) {
      throw new IllegalArgumentException("Restaurante não está disponível");
    }

    Pedido pedido = new Pedido();
    pedido.setCliente(cliente);
    pedido.setRestaurante(restaurante);
    pedido.setStatus(StatusPedido.PENDENTE);

    return pedidoRepository.save(pedido);
  }

  /**
   * Adicionar item ao pedido
   */
  public Pedido adicionarItem(Long pedidoId, Long produtoId, Integer quantidade) {
    Pedido pedido = buscarPorId(pedidoId)
        .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " +
            pedidoId));

    Produto produto = produtoRepository.findById(produtoId)
        .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " +
            produtoId));

    if (!produto.isDisponivel()) {
      throw new IllegalArgumentException("Produto não disponível: " + produto.getNome());
    }

    if (quantidade <= 0) {
      throw new IllegalArgumentException("Quantidade deve ser maior que zero");
    }

    // Verificar se produto pertence ao mesmo restaurante do pedido
    if (!produto.getRestaurante().getId().equals(pedido.getRestaurante().getId())) {
      throw new IllegalArgumentException("Produto não pertence ao restaurante do pedido");
    }

    ItemPedido item = new ItemPedido();
    item.setPedido(pedido);
    item.setProduto(produto);

    item.setQuantidade(quantidade);
    item.setPrecoUnitario(produto.getPreco());
    item.calcularSubtotal();

    pedido.adicionarItem(item);

    return pedidoRepository.save(pedido);

  }

  /**
   * Atualizar status do pedido
   */
  public Pedido atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
    Pedido pedido = buscarPorId(pedidoId)
        .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " +
            pedidoId));

    // Regras simples de transição de status
    if (pedido.getStatus() == StatusPedido.CANCELADO ||
        pedido.getStatus() == StatusPedido.ENTREGUE) {
      throw new IllegalArgumentException("Não é possível alterar o status de um pedido " +
          "cancelado ou entregue");
    }

    if (pedido.getStatus() == StatusPedido.PENDENTE &&
        novoStatus != StatusPedido.CONFIRMADO &&
        novoStatus != StatusPedido.CANCELADO) {
      throw new IllegalArgumentException("Status inválido para pedido pendente");
    }

    if (pedido.getStatus() == StatusPedido.CONFIRMADO &&
        novoStatus != StatusPedido.ENTREGUE &&
        novoStatus != StatusPedido.CANCELADO) {
      throw new IllegalArgumentException("Status inválido para pedido confirmado");
    }

    pedido.setStatus(novoStatus);
    return pedidoRepository.save(pedido);
  }

  /**
   * Confirmar pedido
   */
  public Pedido confirmarPedido(Long pedidoId) {
    Pedido pedido = buscarPorId(pedidoId)
        .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " +
            pedidoId));

    if (pedido.getStatus() != StatusPedido.PENDENTE) {
      throw new IllegalArgumentException("Apenas pedidos pendentes podem ser confirmados");
    }

    if (pedido.getItens().isEmpty()) {
      throw new IllegalArgumentException("Pedido deve ter pelo menos um item");
    }

    pedido.confirmar();
    return pedidoRepository.save(pedido);
  }

  /**
   * Buscar por ID
   */
  @Transactional(readOnly = true)
  public Optional<Pedido> buscarPorId(Long id) {
    return pedidoRepository.findById(id);
  }

  /**
   * Listar pedidos por cliente
   */
  @Transactional(readOnly = true)
  public List<Pedido> listarPorCliente(Long clienteId) {
    return pedidoRepository.findByClienteIdOrderByDataPedidoDesc(clienteId);
  }

  /**
   * Buscar por número do pedido
   */
  @Transactional(readOnly = true)
  public Optional<Pedido> buscarPorNumero(String numeroPedido) {
    return Optional.ofNullable(pedidoRepository.findByNumeroPedido(numeroPedido));
  }

  /**
   * Cancelar pedido
   */
  public Pedido cancelarPedido(Long pedidoId, String motivo) {
    Pedido pedido = buscarPorId(pedidoId)
        .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " +
            pedidoId));

    if (pedido.getStatus() == StatusPedido.ENTREGUE) {
      throw new IllegalArgumentException("Pedido já entregue não pode ser cancelado");
    }

    if (pedido.getStatus() == StatusPedido.CANCELADO) {
      throw new IllegalArgumentException("Pedido já está cancelado");
    }

    pedido.setStatus(StatusPedido.CANCELADO);
    if (motivo != null && !motivo.trim().isEmpty()) {
      pedido.setObservacoes(pedido.getObservacoes() + " | Cancelado: " + motivo);
    }

    return pedidoRepository.save(pedido);
  }
}