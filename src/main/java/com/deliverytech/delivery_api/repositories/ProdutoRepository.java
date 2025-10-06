package com.deliverytech.delivery_api.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.deliverytech.delivery_api.entities.Produto;
import com.deliverytech.delivery_api.entities.Restaurante;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
  // Buscar produtos por restaurante
  List<Produto> findByRestauranteAndDisponivelTrue(Restaurante restaurante);

  // Buscar produtos por restaurante ID
  List<Produto> findByRestauranteIdAndDisponivelTrue(Long restauranteId);

  // Buscar por categoria
  List<Produto> findByCategoriaAndDisponivelTrue(String categoria);

  // Buscar por nome contendo
  List<Produto> findByNomeContainingIgnoreCaseAndDisponivelTrue(String nome);

  // Buscar por faixa de preço
  List<Produto> findByPrecoBetweenAndDisponivelTrue(BigDecimal precoMin, BigDecimal precoMax);

  // Buscar produtos mais baratos que um valor
  List<Produto> findByPrecoLessThanEqualAndDisponivelTrue(BigDecimal preco);

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
}
