package com.deliverytech.delivery_api.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.deliverytech.delivery_api.entities.Cliente;
import com.deliverytech.delivery_api.entities.Pedido;
import com.deliverytech.delivery_api.enums.StatusPedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
  // Buscar pedidos por cliente
  Page<Pedido> findByClienteOrderByDataPedidoDesc(Cliente cliente, Pageable pageable);

  // Buscar pedidos por cliente ID
  Page<Pedido> findByClienteIdOrderByDataPedidoDesc(Long clienteId, Pageable pageable);

  // Buscar por status
  Page<Pedido> findByStatusOrderByDataPedidoDesc(StatusPedido status, Pageable pageable);

  // Buscar por número do pedido
  Pedido findByNumeroPedido(String numeroPedido);

  // 10 pedidos mais recentes
  List<Pedido> findTop10ByOrderByDataPedidoDesc();

  // Buscar pedidos por período
  Page<Pedido> findByDataPedidoBetweenOrderByDataPedidoDesc(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

  // Buscar pedidos do dia
  @Query("SELECT p FROM Pedido p WHERE p.dataPedido >= :inicio AND p.dataPedido < :fim ORDER BY p.dataPedido DESC")
  Page<Pedido> findPedidosDoDia(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim,
      Pageable pageable);

  // Buscar pedidos por restaurante
  @Query("SELECT p FROM Pedido p WHERE p.restaurante.id = :restauranteId ORDER BY p.dataPedido DESC")
  Page<Pedido> findByRestauranteId(@Param("restauranteId") Long restauranteId, Pageable pageable);

  // Relatório - pedidos por status
  @Query("SELECT p.status, COUNT(p) FROM Pedido p GROUP BY p.status")
  Page<Object[]> countPedidosByStatus(Pageable pageable);

  // Pedidos pendentes (para dashboard)
  @Query("SELECT p FROM Pedido p WHERE p.status IN ('PENDENTE', 'CONFIRMADO', 'PREPARANDO') " +
      "ORDER BY p.dataPedido ASC")
  Page<Pedido> findPedidosPendentes(Pageable pageable);

  // Valor total de vendas por período
  @Query("SELECT SUM(p.valorTotal) FROM Pedido p WHERE p.dataPedido BETWEEN :inicio AND :fim " +
      "AND p.status NOT IN ('CANCELADO')")
  BigDecimal calcularVendasPorPeriodo(@Param("inicio") LocalDateTime inicio,
      @Param("fim") LocalDateTime fim);

  @Query("SELECT p.restaurante.nome, SUM(p.valorTotal) " +
      "FROM Pedido p " +
      "GROUP BY p.restaurante.id, p.restaurante.nome " +
      "ORDER BY SUM(p.valorTotal) DESC")
  Page<Object[]> calcularTotalVendasPorRestaurante(Pageable pageable);

  @Query("SELECT p FROM Pedido p WHERE p.valorTotal > :valor ORDER BY p.valorTotal DESC")
  Page<Pedido> buscarPedidosComValorAcimaDe(@Param("valor") BigDecimal valor, Pageable pageable);

  @Query("SELECT p FROM Pedido p " +
      "WHERE p.dataPedido BETWEEN :inicio AND :fim " +
      "AND p.status = :status " +
      "ORDER BY p.dataPedido DESC")
  List<Pedido> relatorioPedidosPorPeriodoEStatus(
      @Param("inicio") LocalDateTime inicio,
      @Param("fim") LocalDateTime fim,
      @Param("status") StatusPedido status);
}
