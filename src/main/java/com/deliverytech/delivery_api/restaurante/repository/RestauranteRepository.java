package com.deliverytech.delivery_api.restaurante.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.deliverytech.delivery_api.dashboard.dto.RelatorioVendas;
import com.deliverytech.delivery_api.restaurante.model.Restaurante;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
  boolean existsByEmail(String email);

  boolean existsByCnpj(String cnpj);

  // Buscar por nome
  Optional<Restaurante> findByNome(String nome);

  // Buscar restaurantes ativos
  Page<Restaurante> findByAtivoTrue(Pageable pageable);

  // Buscar por categoria
  Page<Restaurante> findByCategoriaAndAtivoTrue(String categoria, Pageable pageable);

  // Buscar por nome contendo (case insensitive)
  Page<Restaurante> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome, Pageable pageable);

  // Buscar por avaliação mínima
  Page<Restaurante> findByAvaliacaoGreaterThanEqualAndAtivoTrue(BigDecimal avaliacao, Pageable pageable);

  // Ordenar por avaliação (descendente)
  Page<Restaurante> findByAtivoTrueOrderByAvaliacaoDesc(Pageable pageable);

  // Por taxa de entrega menor ou igual
  Page<Restaurante> findByTaxaEntregaLessThanEqual(BigDecimal taxa, Pageable pageable);

  // Top 5 restaurantes por nome (ordem alfabética)
  Page<Restaurante> findTop5ByOrderByNomeAsc(Pageable pageable);

  // Verificar se o usuário é dono ou associado do restaurante
  boolean existsByIdAndUsuarios_Id(Long restauranteId, Long usuarioId);

  // Query customizada - restaurantes com produtos
  @Query("SELECT DISTINCT r FROM Restaurante r JOIN r.produtos p WHERE r.ativo = true")
  List<Restaurante> findRestaurantesComProdutos();

  // Buscar por faixa de taxa de entrega
  @Query("SELECT r FROM Restaurante r WHERE r.taxaEntrega BETWEEN :min AND :max AND r.ativo = true")
  List<Restaurante> findByTaxaEntregaBetween(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

  // Categorias disponíveis
  @Query("SELECT DISTINCT r.categoria FROM Restaurante r WHERE r.ativo = true ORDER BY r.categoria")
  List<String> findCategoriasDisponiveis();

  // No RestauranteRepository:
  @Query("SELECT r.nome as nomeRestaurante, " +
      "COALESCE(SUM(p.valorTotal), 0) as totalVendas, " +
      "COUNT(p.id) as quantidadePedidos " +
      "FROM Restaurante r " +
      "LEFT JOIN Pedido p ON r.id = p.restaurante.id " +
      "GROUP BY r.id, r.nome")
  List<RelatorioVendas> relatorioVendasPorRestaurante();
}
