package com.deliverytech.delivery_api.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
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

import com.deliverytech.delivery_api.common.exceptions.ConflictException;
import com.deliverytech.delivery_api.common.exceptions.EntityNotFoundException;
import com.deliverytech.delivery_api.common.exceptions.InactiveEntityException;
import com.deliverytech.delivery_api.common.exceptions.ValidationException;
import com.deliverytech.delivery_api.produto.dto.ProdutoDTO;
import com.deliverytech.delivery_api.produto.dto.ProdutoFiltroDTO;
import com.deliverytech.delivery_api.produto.dto.ProdutoResponseDTO;
import com.deliverytech.delivery_api.produto.model.Produto;
import com.deliverytech.delivery_api.produto.repository.ProdutoRepository;
import com.deliverytech.delivery_api.produto.service.ProdutoServiceImpl;
import com.deliverytech.delivery_api.restaurante.model.Restaurante;
import com.deliverytech.delivery_api.restaurante.repository.RestauranteRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProdutoServiceImpl - Testes Unitários")
class ProdutoServiceImplTest {

  @Mock
  private ProdutoRepository produtoRepository;

  @Mock
  private RestauranteRepository restauranteRepository;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private ProdutoServiceImpl produtoService;

  private Restaurante restaurante;
  private Produto produto;
  private ProdutoDTO produtoDTO;
  private ProdutoResponseDTO produtoResponseDTO;

  @BeforeEach
  void setUp() {
    // Setup Restaurante
    restaurante = new Restaurante();
    restaurante.setId(1L);
    restaurante.setNome("Restaurante Teste");
    restaurante.setAtivo(true);

    // Setup Produto
    produto = new Produto();
    produto.setId(1L);
    produto.setNome("Pizza Margherita");
    produto.setDescricao("Pizza tradicional");
    produto.setPreco(BigDecimal.valueOf(35.00));
    produto.setCategoria("Pizza");
    produto.setRestaurante(restaurante);
    produto.setDisponivel(true);

    // Setup ProdutoDTO
    produtoDTO = new ProdutoDTO();
    produtoDTO.setNome("Pizza Margherita");
    produtoDTO.setDescricao("Pizza tradicional");
    produtoDTO.setPreco(BigDecimal.valueOf(35.00));
    produtoDTO.setCategoria("Pizza");
    produtoDTO.setRestauranteId(1L);
    produtoDTO.setDisponivel(true);

    // Setup ProdutoResponseDTO
    produtoResponseDTO = new ProdutoResponseDTO();
    produtoResponseDTO.setId(1L);
    produtoResponseDTO.setNome("Pizza Margherita");
    produtoResponseDTO.setPreco(BigDecimal.valueOf(35.00));
  }

  @Test
  @DisplayName("Deve cadastrar produto com sucesso")
  void deveCadastrarProdutoComSucesso() {
    when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
    when(produtoRepository.findByRestauranteIdAndDisponivelTrue(eq(1L), any(Pageable.class)))
        .thenReturn(Page.empty());
    when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
    when(modelMapper.map(any(Produto.class), eq(ProdutoResponseDTO.class)))
        .thenReturn(produtoResponseDTO);

    ProdutoResponseDTO result = produtoService.cadastrarProduto(produtoDTO);

    assertNotNull(result);
    assertEquals("Pizza Margherita", result.getNome());
    verify(restauranteRepository).findById(1L);
    verify(produtoRepository).save(any(Produto.class));
  }

  @Test
  @DisplayName("Deve lançar exceção ao cadastrar produto com restaurante inexistente")
  void deveLancarExcecaoAoCadastrarProdutoComRestauranteInexistente() {
    when(restauranteRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> produtoService.cadastrarProduto(produtoDTO));
    verify(restauranteRepository).findById(1L);
    verify(produtoRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao cadastrar produto com restaurante inativo")
  void deveLancarExcecaoAoCadastrarProdutoComRestauranteInativo() {
    restaurante.setAtivo(false);
    when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

    assertThrows(InactiveEntityException.class,
        () -> produtoService.cadastrarProduto(produtoDTO));
    verify(restauranteRepository).findById(1L);
    verify(produtoRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao cadastrar produto com nome duplicado")
  void deveLancarExcecaoAoCadastrarProdutoComNomeDuplicado() {
    when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));
    when(produtoRepository.findByRestauranteIdAndDisponivelTrue(eq(1L), any(Pageable.class)))
        .thenReturn(new PageImpl<>(Arrays.asList(produto)));

    assertThrows(ConflictException.class,
        () -> produtoService.cadastrarProduto(produtoDTO));
    verify(restauranteRepository).findById(1L);
    verify(produtoRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao cadastrar produto sem nome")
  void deveLancarExcecaoAoCadastrarProdutoSemNome() {
    when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

    produtoDTO.setNome(null);

    assertThrows(ValidationException.class,
        () -> produtoService.cadastrarProduto(produtoDTO));
    verify(produtoRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao cadastrar produto com preço inválido")
  void deveLancarExcecaoAoCadastrarProdutoComPrecoInvalido() {
    when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

    produtoDTO.setPreco(BigDecimal.ZERO);

    assertThrows(ValidationException.class,
        () -> produtoService.cadastrarProduto(produtoDTO));
    verify(produtoRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve lançar exceção ao cadastrar produto sem categoria")
  void deveLancarExcecaoAoCadastrarProdutoSemCategoria() {
    when(restauranteRepository.findById(1L)).thenReturn(Optional.of(restaurante));

    produtoDTO.setCategoria(null);

    assertThrows(ValidationException.class,
        () -> produtoService.cadastrarProduto(produtoDTO));
    verify(produtoRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve buscar produto por ID com sucesso")
  void deveBuscarProdutoPorId() {
    when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
    when(modelMapper.map(any(Produto.class), eq(ProdutoResponseDTO.class)))
        .thenReturn(produtoResponseDTO);

    ProdutoResponseDTO result = produtoService.buscarProdutoPorId(1L);

    assertNotNull(result);
    verify(produtoRepository).findById(1L);
  }

  @Test
  @DisplayName("Deve lançar exceção ao buscar produto inexistente")
  void deveLancarExcecaoAoBuscarProdutoInexistente() {
    when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> produtoService.buscarProdutoPorId(1L));
    verify(produtoRepository).findById(1L);
  }

  @Test
  @DisplayName("Deve listar produtos por restaurante")
  void deveListarProdutosPorRestaurante() {
    when(restauranteRepository.existsById(1L)).thenReturn(true);
    when(produtoRepository.findByRestauranteIdAndDisponivelTrue(eq(1L), any(Pageable.class)))
        .thenReturn(new PageImpl<>(Arrays.asList(produto)));
    when(modelMapper.map(any(Produto.class), eq(ProdutoResponseDTO.class)))
        .thenReturn(produtoResponseDTO);

    Page<ProdutoResponseDTO> result = produtoService.listarPorRestaurante(1L, Pageable.unpaged());

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(restauranteRepository).existsById(1L);
  }

  @Test
  @DisplayName("Deve lançar exceção ao listar produtos de restaurante inexistente")
  void deveLancarExcecaoAoListarProdutosDeRestauranteInexistente() {
    when(restauranteRepository.existsById(1L)).thenReturn(false);

    assertThrows(EntityNotFoundException.class,
        () -> produtoService.listarPorRestaurante(1L, Pageable.unpaged()));
    verify(restauranteRepository).existsById(1L);
  }

  @Test
  @DisplayName("Deve buscar produtos por categoria")
  void deveBuscarProdutosPorCategoria() {
    when(produtoRepository.findByCategoriaAndDisponivelTrue(eq("Pizza"), any(Pageable.class)))
        .thenReturn(new PageImpl<>(Arrays.asList(produto)));
    when(modelMapper.map(any(Produto.class), eq(ProdutoResponseDTO.class)))
        .thenReturn(produtoResponseDTO);

    Page<ProdutoResponseDTO> result = produtoService.buscarPorCategoria("Pizza", Pageable.unpaged());

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(produtoRepository).findByCategoriaAndDisponivelTrue(eq("Pizza"), any(Pageable.class));
  }

  @Test
  @DisplayName("Deve lançar exceção ao buscar produtos com categoria vazia")
  void deveLancarExcecaoAoBuscarProdutosComCategoriaVazia() {
    assertThrows(ValidationException.class,
        () -> produtoService.buscarPorCategoria("", Pageable.unpaged()));
  }

  @Test
  @DisplayName("Deve atualizar produto com sucesso")
  void deveAtualizarProdutoComSucesso() {
    when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
    when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
    when(modelMapper.map(any(Produto.class), eq(ProdutoResponseDTO.class)))
        .thenReturn(produtoResponseDTO);

    ProdutoResponseDTO result = produtoService.atualizarProduto(1L, produtoDTO);

    assertNotNull(result);
    verify(produtoRepository).findById(1L);
    verify(produtoRepository).save(any(Produto.class));
  }

  @Test
  @DisplayName("Deve lançar exceção ao atualizar produto inexistente")
  void deveLancarExcecaoAoAtualizarProdutoInexistente() {
    when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> produtoService.atualizarProduto(1L, produtoDTO));
    verify(produtoRepository).findById(1L);
    verify(produtoRepository, never()).save(any());
  }

  @Test
  @DisplayName("Deve alterar disponibilidade do produto")
  void deveAlterarDisponibilidadeDoProduto() {
    when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
    when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
    when(modelMapper.map(any(Produto.class), eq(ProdutoResponseDTO.class)))
        .thenReturn(produtoResponseDTO);

    ProdutoResponseDTO result = produtoService.alterarDisponibilidade(1L, false);

    assertNotNull(result);
    verify(produtoRepository).findById(1L);
    verify(produtoRepository).save(any(Produto.class));
  }

  @Test
  @DisplayName("Deve buscar produtos por faixa de preço")
  void deveBuscarProdutosPorFaixaPreco() {
    when(produtoRepository.findByPrecoBetweenAndDisponivelTrue(
        any(BigDecimal.class), any(BigDecimal.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(Arrays.asList(produto)));
    when(modelMapper.map(any(Produto.class), eq(ProdutoResponseDTO.class)))
        .thenReturn(produtoResponseDTO);

    Page<ProdutoResponseDTO> result = produtoService.buscarPorFaixaPreco(
        BigDecimal.valueOf(20.00), BigDecimal.valueOf(50.00), Pageable.unpaged());

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  @DisplayName("Deve lançar exceção ao buscar produtos com preços nulos")
  void deveLancarExcecaoAoBuscarProdutosComPrecosNulos() {
    assertThrows(ValidationException.class,
        () -> produtoService.buscarPorFaixaPreco(null, BigDecimal.valueOf(50.00), Pageable.unpaged()));
  }

  @Test
  @DisplayName("Deve lançar exceção ao buscar produtos com preço mínimo negativo")
  void deveLancarExcecaoAoBuscarProdutosComPrecoMinimoNegativo() {
    assertThrows(ValidationException.class,
        () -> produtoService.buscarPorFaixaPreco(
            BigDecimal.valueOf(-10.00), BigDecimal.valueOf(50.00), Pageable.unpaged()));
  }

  @Test
  @DisplayName("Deve lançar exceção ao buscar produtos com preço mínimo maior que máximo")
  void deveLancarExcecaoAoBuscarProdutosComPrecoMinimoMaiorQueMaximo() {
    assertThrows(ValidationException.class,
        () -> produtoService.buscarPorFaixaPreco(
            BigDecimal.valueOf(60.00), BigDecimal.valueOf(50.00), Pageable.unpaged()));
  }

  @Test
  @DisplayName("Deve buscar produtos com filtros")
  void deveBuscarProdutosComFiltros() {
    ProdutoFiltroDTO filtro = new ProdutoFiltroDTO();
    filtro.setCategoria("Pizza");
    filtro.setDisponivel(true);

    when(produtoRepository.buscarComFiltros(
        any(), any(), any(), any(), any(), any(), any(Pageable.class)))
        .thenReturn(new PageImpl<>(Arrays.asList(produto)));
    when(modelMapper.map(any(Produto.class), eq(ProdutoResponseDTO.class)))
        .thenReturn(produtoResponseDTO);

    Page<ProdutoResponseDTO> result = produtoService.buscarComFiltros(filtro, Pageable.unpaged());

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  @DisplayName("Deve deletar produto com sucesso")
  void deveDeletarProdutoComSucesso() {
    when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
    doNothing().when(produtoRepository).delete(any(Produto.class));

    assertDoesNotThrow(() -> produtoService.deletarProduto(1L));
    verify(produtoRepository).findById(1L);
    verify(produtoRepository).delete(produto);
  }

  @Test
  @DisplayName("Deve lançar exceção ao deletar produto inexistente")
  void deveLancarExcecaoAoDeletarProdutoInexistente() {
    when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> produtoService.deletarProduto(1L));
    verify(produtoRepository).findById(1L);
    verify(produtoRepository, never()).delete(any());
  }
}