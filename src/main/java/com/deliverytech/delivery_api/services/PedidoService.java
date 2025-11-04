package com.deliverytech.delivery_api.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.deliverytech.delivery_api.dtos.ItemPedidoDTO;
import com.deliverytech.delivery_api.dtos.PedidoDTO;
import com.deliverytech.delivery_api.dtos.PedidoResponseDTO;
import com.deliverytech.delivery_api.enums.StatusPedido;

public interface PedidoService {
  PedidoResponseDTO criarPedido(PedidoDTO dto);

  PedidoResponseDTO buscarPedidoPorId(Long id);

  Page<PedidoResponseDTO> buscarPedidosPorUsuario(Long usuarioId, Pageable pageable);

  Page<PedidoResponseDTO> buscarMeusPedidos(Pageable pageable);

  Page<PedidoResponseDTO> buscarPedidosPorRestaurante(Long restauranteId, Pageable pageable);

  PedidoResponseDTO atualizarStatusPedido(Long id, StatusPedido status);

  BigDecimal calcularTotalPedido(List<ItemPedidoDTO> itens);

  boolean isOwner(Long pedidoId);

  void cancelarPedido(Long id);
}
