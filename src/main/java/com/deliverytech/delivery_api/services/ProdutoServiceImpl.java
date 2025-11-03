package com.deliverytech.delivery_api.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.dtos.ProdutoDTO;
import com.deliverytech.delivery_api.dtos.ProdutoResponseDTO;
import com.deliverytech.delivery_api.entities.Produto;
import com.deliverytech.delivery_api.entities.Restaurante;
import com.deliverytech.delivery_api.exceptions.ConflictException;
import com.deliverytech.delivery_api.exceptions.EntityNotFoundException;
import com.deliverytech.delivery_api.exceptions.InactiveEntityException;
import com.deliverytech.delivery_api.exceptions.ValidationException;
import com.deliverytech.delivery_api.repositories.ProdutoRepository;
import com.deliverytech.delivery_api.repositories.RestauranteRepository;
import com.deliverytech.delivery_api.security.SecurityUtils;

@Service("produtoService")
@Transactional
@Primary
public class ProdutoServiceImpl implements ProdutoService {

  @Autowired
  private ProdutoRepository produtoRepository;

  @Autowired
  private RestauranteRepository restauranteRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  @Transactional
  public ProdutoResponseDTO cadastrarProduto(ProdutoDTO produtoDTO) {
    // 1. Validar restaurante existe e está ativo
    Restaurante restaurante = restauranteRepository.findById(produtoDTO.getRestauranteId())
        .orElseThrow(() -> new EntityNotFoundException("Restaurante", produtoDTO.getRestauranteId()));

    if (!restaurante.isAtivo()) {
      throw new InactiveEntityException("Restaurante", produtoDTO.getRestauranteId());
    }

    // 2. Validar dados do produto
    validarDadosProduto(produtoDTO);

    // 3. Verificar se já existe produto com mesmo nome no restaurante
    Page<Produto> produtosExistentes = produtoRepository
        .findByRestauranteIdAndDisponivelTrue(restaurante.getId(), Pageable.unpaged());

    boolean nomeJaExiste = produtosExistentes.stream()
        .anyMatch(p -> p.getNome().equalsIgnoreCase(produtoDTO.getNome()));

    if (nomeJaExiste) {
      throw new ConflictException("Produto", "nome");
    }

    // 4. Criar e salvar produto
    Produto produto = new Produto();
    produto.setNome(produtoDTO.getNome());
    produto.setDescricao(produtoDTO.getDescricao());
    produto.setPreco(produtoDTO.getPreco());
    produto.setCategoria(produtoDTO.getCategoria());
    produto.setRestaurante(restaurante);
    produto.setDisponivel(produtoDTO.isDisponivel());

    Produto produtoSalvo = produtoRepository.save(produto);

    // 5. Retornar DTO de resposta
    return converterParaResponseDTO(produtoSalvo);
  }

  @Override
  @Transactional(readOnly = true)
  public ProdutoResponseDTO buscarProdutoPorId(Long id) {
    Produto produto = produtoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Produto", id));

    return converterParaResponseDTO(produto);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProdutoResponseDTO> listarPorRestaurante(Long restauranteId, Pageable pageable) {
    // Validar se restaurante existe
    if (!restauranteRepository.existsById(restauranteId)) {
      throw new EntityNotFoundException("Restaurante", restauranteId);
    }

    Page<Produto> produtos = produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId, pageable);

    return produtos.stream()
        .map(this::converterParaResponseDTO)
        .collect(Collectors.collectingAndThen(Collectors.toList(),
            list -> new PageImpl<>(list, pageable, list.size())));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProdutoResponseDTO> buscarPorCategoria(String categoria, Pageable pageable) {
    if (categoria == null || categoria.trim().isEmpty()) {
      throw new ValidationException("Categoria não pode ser vazia");
    }

    Page<Produto> produtos = produtoRepository.findByCategoriaAndDisponivelTrue(categoria, pageable);

    return produtos.stream()
        .map(this::converterParaResponseDTO)
        .collect(Collectors.collectingAndThen(Collectors.toList(),
            list -> new PageImpl<>(list, pageable, list.size())));
  }

  @Override
  @Transactional
  public ProdutoResponseDTO atualizarProduto(Long id, ProdutoDTO produtoDTO) {
    // 1. Buscar produto existente
    Produto produto = produtoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Produto", id));

    // 2. Validar dados
    validarDadosProduto(produtoDTO);

    // 3. Verificar se mudou o nome e se já existe outro produto com esse nome
    if (!produto.getNome().equalsIgnoreCase(produtoDTO.getNome())) {
      Page<Produto> produtosExistentes = produtoRepository
          .findByRestauranteIdAndDisponivelTrue(produto.getRestaurante().getId(), Pageable.unpaged());

      boolean nomeJaExiste = produtosExistentes.stream()
          .filter(p -> !p.getId().equals(id))
          .anyMatch(p -> p.getNome().equalsIgnoreCase(produtoDTO.getNome()));

      if (nomeJaExiste) {
        throw new ConflictException("Produto", "nome");
      }
    }

    // 4. Atualizar campos
    produto.setNome(produtoDTO.getNome());
    produto.setDescricao(produtoDTO.getDescricao());
    produto.setPreco(produtoDTO.getPreco());
    produto.setCategoria(produtoDTO.getCategoria());

    // Nota: não atualiza restauranteId após criação
    // Nota: disponibilidade é atualizada via endpoint específico

    Produto produtoAtualizado = produtoRepository.save(produto);

    return converterParaResponseDTO(produtoAtualizado);
  }

  @Override
  @Transactional
  public ProdutoResponseDTO alterarDisponibilidade(Long id, boolean disponivel) {
    Produto produto = produtoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Produto", id));

    produto.setDisponivel(disponivel);
    Produto produtoAtualizado = produtoRepository.save(produto);

    return converterParaResponseDTO(produtoAtualizado);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProdutoResponseDTO> buscarPorFaixaPreco(BigDecimal precoMin, BigDecimal precoMax, Pageable pageable) {
    // Validações
    if (precoMin == null || precoMax == null) {
      throw new ValidationException("Preço mínimo e máximo são obrigatórios");
    }

    if (precoMin.compareTo(BigDecimal.ZERO) < 0) {
      throw new ValidationException("Preço mínimo não pode ser negativo");
    }

    if (precoMin.compareTo(precoMax) > 0) {
      throw new ValidationException("Preço mínimo não pode ser maior que o preço máximo");
    }

    Page<Produto> produtos = produtoRepository.findByPrecoBetweenAndDisponivelTrue(precoMin, precoMax, pageable);

    return produtos.stream()
        .map(this::converterParaResponseDTO)
        .collect(Collectors.collectingAndThen(Collectors.toList(),
            list -> new PageImpl<>(list, pageable, list.size())));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<ProdutoResponseDTO> buscarPorRestauranteECategoria(Long restauranteId, String categoria,
      Pageable pageable) {
    // Validar restaurante existe
    if (!restauranteRepository.existsById(restauranteId)) {
      throw new EntityNotFoundException("Restaurante", restauranteId);
    }

    if (categoria == null || categoria.trim().isEmpty()) {
      throw new ValidationException("Categoria não pode ser vazia");
    }

    List<Produto> produtos = produtoRepository
        .findByRestauranteIdAndDisponivelTrue(restauranteId, Pageable.unpaged())
        .stream()
        .filter(p -> p.getCategoria().equalsIgnoreCase(categoria))
        .toList();

    return produtos.stream()
        .map(this::converterParaResponseDTO)
        .collect(Collectors.collectingAndThen(Collectors.toList(),
            list -> new PageImpl<>(list, pageable, list.size())));
  }

  // ==================== MÉTODOS AUXILIARES ====================

  private void validarDadosProduto(ProdutoDTO produtoDTO) {
    if (produtoDTO.getNome() == null || produtoDTO.getNome().trim().isEmpty()) {
      throw new ValidationException("Nome do produto é obrigatório");
    }

    if (produtoDTO.getPreco() == null || produtoDTO.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
      throw new ValidationException("Preço deve ser maior que zero");
    }

    if (produtoDTO.getCategoria() == null || produtoDTO.getCategoria().trim().isEmpty()) {
      throw new ValidationException("Categoria é obrigatória");
    }
  }

  private ProdutoResponseDTO converterParaResponseDTO(Produto produto) {
    ProdutoResponseDTO dto = modelMapper.map(produto, ProdutoResponseDTO.class);

    // Garantir que informações do restaurante sejam incluídas
    if (produto.getRestaurante() != null) {
      dto.setRestauranteId(produto.getRestaurante().getId());
      dto.setRestauranteNome(produto.getRestaurante().getNome());
    }

    return dto;
  }

  @Override
  public void deletarProduto(Long id) {
    Produto produto = produtoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Produto", id));
    produtoRepository.delete(produto);
  }

  @Override
  public boolean isOwner(Long produtoId) {
    Long usuarioId = SecurityUtils.getCurrentUserId();
    if (usuarioId == null)
      return false;
    return produtoRepository.isOwner(produtoId, usuarioId);
  }
}