package com.deliverytech.delivery_api.pedido.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.deliverytech.delivery_api.pedido.dto.PedidoDTO;
import com.deliverytech.delivery_api.pedido.dto.PedidoFiltroDTO;
import com.deliverytech.delivery_api.pedido.dto.PedidoItemDTO;
import com.deliverytech.delivery_api.pedido.dto.PedidoResponseDTO;
import com.deliverytech.delivery_api.pedido.model.PedidoStatus;

public interface PedidoService {
  PedidoResponseDTO criarPedido(PedidoDTO dto);

  PedidoResponseDTO buscarPedidoPorId(Long id);

  Page<PedidoResponseDTO> buscarPedidosPorUsuario(Long usuarioId, Pageable pageable);

  Page<PedidoResponseDTO> buscarMeusPedidos(PedidoFiltroDTO filtro, Pageable pageable);

  Page<PedidoResponseDTO> buscarPedidosPorRestaurante(Long restauranteId, Pageable pageable);

  PedidoResponseDTO atualizarStatusPedido(Long id, PedidoStatus status);

  BigDecimal calcularTotalPedido(List<PedidoItemDTO> itens);

  boolean isOwner(Long pedidoId);

  void cancelarPedido(Long id);
}
