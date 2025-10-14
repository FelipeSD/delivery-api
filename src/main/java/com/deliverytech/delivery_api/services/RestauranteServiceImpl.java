package com.deliverytech.delivery_api.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.dtos.ProdutoResponseDTO;
import com.deliverytech.delivery_api.dtos.RestauranteDTO;
import com.deliverytech.delivery_api.dtos.RestauranteResponseDTO;
import com.deliverytech.delivery_api.dtos.TaxaEntregaResponseDTO;
import com.deliverytech.delivery_api.entities.Produto;
import com.deliverytech.delivery_api.entities.Restaurante;
import com.deliverytech.delivery_api.exceptions.BusinessException;
import com.deliverytech.delivery_api.exceptions.EntityNotFoundException;
import com.deliverytech.delivery_api.repositories.ProdutoRepository;
import com.deliverytech.delivery_api.repositories.RestauranteRepository;

@Service
@Transactional
public class RestauranteServiceImpl implements RestauranteService {
  @Autowired
  private RestauranteRepository restauranteRepository;

  @Autowired
  private ProdutoRepository produtoRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Override
  @Transactional
  public RestauranteResponseDTO cadastrar(RestauranteDTO restauranteDTO) {
    // 1. Validar dados básicos
    validarDadosRestaurante(restauranteDTO);

    // 4. Criar e salvar restaurante
    Restaurante restaurante = new Restaurante();
    restaurante.setNome(restauranteDTO.getNome());
    restaurante.setTelefone(restauranteDTO.getTelefone());
    restaurante.setCategoria(restauranteDTO.getCategoria());
    restaurante.setTaxaEntrega(restauranteDTO.getTaxaEntrega());
    restaurante.setEndereco(restauranteDTO.getEndereco());
    restaurante.setAtivo(true);

    Restaurante restauranteSalvo = restauranteRepository.save(restaurante);

    return converterParaResponseDTO(restauranteSalvo);
  }

  @Override
  @Transactional(readOnly = true)
  public RestauranteResponseDTO buscarPorId(Long id) {
    Restaurante restaurante = restauranteRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

    return converterParaResponseDTO(restaurante);
  }

  @Override
  @Transactional(readOnly = true)
  public List<RestauranteResponseDTO> listarDisponiveis() {
    List<Restaurante> restaurantes = restauranteRepository.findByAtivoTrue();

    return restaurantes.stream()
        .map(this::converterParaResponseDTO)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<RestauranteResponseDTO> listarPorCategoria(String categoria) {
    if (categoria == null || categoria.trim().isEmpty()) {
      throw new BusinessException("Categoria não pode ser vazia");
    }

    List<Restaurante> restaurantes = restauranteRepository
        .findByCategoriaAndAtivoTrue(categoria);

    return restaurantes.stream()
        .map(this::converterParaResponseDTO)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public RestauranteResponseDTO atualizar(Long id, RestauranteDTO restauranteDTO) {
    // 1. Buscar restaurante existente
    Restaurante restaurante = restauranteRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

    // 2. Validar dados
    validarDadosRestaurante(restauranteDTO);

    // 3. Atualizar campos
    restaurante.setNome(restauranteDTO.getNome());
    restaurante.setTelefone(restauranteDTO.getTelefone());
    restaurante.setCategoria(restauranteDTO.getCategoria());
    restaurante.setTaxaEntrega(restauranteDTO.getTaxaEntrega());
    restaurante.setTempoEntregaMin(restauranteDTO.getTempoEntregaMin());
    restaurante.setTempoEntregaMax(restauranteDTO.getTempoEntregaMax());
    restaurante.setEndereco(restauranteDTO.getEndereco());
    restaurante.setCidade(restauranteDTO.getCidade());
    restaurante.setEstado(restauranteDTO.getEstado());
    restaurante.setCep(restauranteDTO.getCep());

    Restaurante restauranteAtualizado = restauranteRepository.save(restaurante);

    return converterParaResponseDTO(restauranteAtualizado);
  }

  @Override
  @Transactional
  public RestauranteResponseDTO alterarStatus(Long id, boolean ativo) {
    Restaurante restaurante = restauranteRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

    // Se está desativando, verificar se há pedidos pendentes
    if (!ativo && restaurante.isAtivo()) {
      // Aqui você pode adicionar validação de pedidos pendentes se necessário
      // Por exemplo: verificar se há pedidos em andamento
    }

    restaurante.setAtivo(ativo);
    Restaurante restauranteAtualizado = restauranteRepository.save(restaurante);

    return converterParaResponseDTO(restauranteAtualizado);
  }

  @Override
  @Transactional(readOnly = true)
  public TaxaEntregaResponseDTO calcularTaxaEntrega(Long id, String cep) {
    // 1. Buscar restaurante
    Restaurante restaurante = restauranteRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

    if (!restaurante.isAtivo()) {
      throw new BusinessException("Restaurante não está disponível");
    }

    // 2. Validar CEP
    if (cep == null || cep.trim().isEmpty()) {
      throw new BusinessException("CEP é obrigatório");
    }

    String cepLimpo = cep.replaceAll("[^0-9]", "");
    if (cepLimpo.length() != 8) {
      throw new BusinessException("CEP inválido");
    }

    // 3. Calcular taxa (simulação - em produção seria integração com API de
    // geolocalização)
    TaxaEntregaResponseDTO response = new TaxaEntregaResponseDTO();
    response.setRestauranteId(restaurante.getId());
    response.setRestauranteNome(restaurante.getNome());
    response.setCep(cep);
    response.setTaxaEntrega(restaurante.getTaxaEntrega());

    // Simulação de cálculo de distância e tempo
    // Em produção: integrar com Google Maps API, ViaCEP, etc.
    Double distanciaSimulada = calcularDistanciaSimulada(restaurante.getCep(), cepLimpo);
    response.setDistanciaKm(distanciaSimulada);

    Integer tempoEstimado = calcularTempoEstimado(distanciaSimulada,
        restaurante.getTempoEntregaMin(), restaurante.getTempoEntregaMax());
    response.setTempoEstimadoMinutos(tempoEstimado);

    return response;
  }

  @Override
  @Transactional(readOnly = true)
  public List<RestauranteResponseDTO> buscarPorNome(String nome) {
    if (nome == null || nome.trim().isEmpty()) {
      throw new BusinessException("Nome para busca não pode ser vazio");
    }

    List<Restaurante> restaurantes = restauranteRepository
        .findByNomeContainingIgnoreCaseAndAtivoTrue(nome);

    return restaurantes.stream()
        .map(this::converterParaResponseDTO)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public RestauranteResponseDTO buscarComProdutos(Long id) {
    Restaurante restaurante = restauranteRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado com ID: " + id));

    RestauranteResponseDTO response = converterParaResponseDTO(restaurante);

    // Buscar produtos do restaurante
    List<Produto> produtos = produtoRepository.findByRestauranteIdAndDisponivelTrue(id);

    List<ProdutoResponseDTO> produtosDTO = produtos.stream()
        .map(produto -> {
          ProdutoResponseDTO dto = modelMapper.map(produto, ProdutoResponseDTO.class);
          dto.setRestauranteId(restaurante.getId());
          dto.setRestauranteNome(restaurante.getNome());
          return dto;
        })
        .collect(Collectors.toList());

    response.setProdutos(produtosDTO);

    return response;
  }

  // ==================== MÉTODOS AUXILIARES ====================

  private void validarDadosRestaurante(RestauranteDTO restauranteDTO) {
    if (restauranteDTO.getNome() == null || restauranteDTO.getNome().trim().isEmpty()) {
      throw new BusinessException("Nome do restaurante é obrigatório");
    }

    if (restauranteDTO.getCnpj() == null || restauranteDTO.getCnpj().trim().isEmpty()) {
      throw new BusinessException("CNPJ é obrigatório");
    }

    if (restauranteDTO.getEmail() == null || restauranteDTO.getEmail().trim().isEmpty()) {
      throw new BusinessException("Email é obrigatório");
    }

    if (restauranteDTO.getTaxaEntrega() == null ||
        restauranteDTO.getTaxaEntrega().compareTo(BigDecimal.ZERO) < 0) {
      throw new BusinessException("Taxa de entrega inválida");
    }

    if (restauranteDTO.getCategoria() == null || restauranteDTO.getCategoria().trim().isEmpty()) {
      throw new BusinessException("Categoria é obrigatória");
    }
  }

  private Double calcularDistanciaSimulada(String cepOrigem, String cepDestino) {
    // Simulação simples - em produção usar API de geolocalização
    // Calcula diferença entre CEPs e simula distância
    try {
      int cep1 = Integer.parseInt(cepOrigem.replaceAll("[^0-9]", ""));
      int cep2 = Integer.parseInt(cepDestino);
      int diferenca = Math.abs(cep1 - cep2);

      // Cada 1000 de diferença no CEP = ~1km (muito simplificado)
      return Math.min(diferenca / 1000.0, 50.0); // máximo 50km
    } catch (Exception e) {
      return 5.0; // distância padrão em caso de erro
    }
  }

  private Integer calcularTempoEstimado(Double distanciaKm, Integer tempoMin, Integer tempoMax) {
    // Tempo base de preparo
    int tempoBase = (tempoMin != null && tempoMax != null)
        ? (tempoMin + tempoMax) / 2
        : 30; // 30 minutos padrão

    // Tempo de deslocamento: ~2 minutos por km (velocidade média 30 km/h)
    int tempoDeslocamento = (int) (distanciaKm * 2);

    return tempoBase + tempoDeslocamento;
  }

  private RestauranteResponseDTO converterParaResponseDTO(Restaurante restaurante) {
    return modelMapper.map(restaurante, RestauranteResponseDTO.class);
  }
}