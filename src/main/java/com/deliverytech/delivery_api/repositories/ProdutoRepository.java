package com.deliverytech.delivery_api.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.deliverytech.delivery_api.entities.Produto;
import com.deliverytech.delivery_api.entities.Restaurante;
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
  // Buscar produtos por restaurante
  Page<Produto> findByRestauranteAndDisponivelTrue(Restaurante restaurante, Pageable pageable);

  // Buscar produtos por restaurante ID
  Page<Produto> findByRestauranteIdAndDisponivelTrue(Long restauranteId, Pageable pageable);

  // Buscar por categoria
  Page<Produto> findByCategoriaAndDisponivelTrue(String categoria, Pageable pageable);

  // Buscar por nome contendo
  Page<Produto> findByNomeContainingIgnoreCaseAndDisponivelTrue(String nome, Pageable pageable);

  // Buscar por faixa de preço
  Page<Produto> findByPrecoBetweenAndDisponivelTrue(BigDecimal precoMin, BigDecimal precoMax, Pageable pageable);

  // Buscar produtos mais baratos que um valor
  Page<Produto> findByPrecoLessThanEqualAndDisponivelTrue(BigDecimal preco, Pageable pageable);

  // Por faixa de preço (menor ou igual)
  Page<Produto> findByPrecoLessThanEqual(BigDecimal preco, Pageable pageable);

  // Ordenar por preço
  List<Produto> findByDisponivelTrueOrderByPrecoAsc();

  List<Produto> findByDisponivelTrueOrderByPrecoDesc();

  // Query customizada - produtos mais vendidos
  @Query("SELECT p FROM Produto p JOIN p.itensPedido ip " +
      "GROUP BY p ORDER BY COUNT(ip) DESC")
  List<Produto> findProdutosMaisVendidos();

  // Buscar por restaurante e categoria
  @Query("SELECT p FROM Produto p WHERE p.restaurante.id = :restauranteId " +
      "AND p.categoria = :categoria AND p.disponivel = true")
  List<Produto> findByRestauranteAndCategoria(@Param("restauranteId") Long restauranteId,
      @Param("categoria") String categoria);

  // Contar produtos por restaurante
  @Query("SELECT COUNT(p) FROM Produto p WHERE p.restaurante.id = :restauranteId AND p.disponivel = true")
  Long countByRestauranteId(@Param("restauranteId") Long restauranteId);

  @Query(value = "SELECT p.nome, COUNT(ip.produto_id) as quantidade_vendida " +
      "FROM produto p " +
      "LEFT JOIN item_pedido ip ON p.id = ip.produto_id " +
      "GROUP BY p.id, p.nome " +
      "ORDER BY quantidade_vendida DESC " +
      "LIMIT 5", nativeQuery = true)
  List<Object[]> produtosMaisVendidos();
}
