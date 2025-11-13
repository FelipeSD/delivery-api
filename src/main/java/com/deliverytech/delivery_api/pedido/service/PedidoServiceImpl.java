package com.deliverytech.delivery_api.pedido.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.auth.model.Usuario;
import com.deliverytech.delivery_api.auth.repository.UsuarioRepository;
import com.deliverytech.delivery_api.common.exceptions.BusinessException;
import com.deliverytech.delivery_api.common.exceptions.EntityNotFoundException;
import com.deliverytech.delivery_api.common.exceptions.InactiveEntityException;
import com.deliverytech.delivery_api.common.exceptions.OrderStatusException;
import com.deliverytech.delivery_api.common.monitoring.metrics.MetricsService;
import com.deliverytech.delivery_api.common.security.SecurityUtils;
import com.deliverytech.delivery_api.pedido.dto.PedidoDTO;
import com.deliverytech.delivery_api.pedido.dto.PedidoFiltroDTO;
import com.deliverytech.delivery_api.pedido.dto.PedidoItemDTO;
import com.deliverytech.delivery_api.pedido.dto.PedidoResponseDTO;
import com.deliverytech.delivery_api.pedido.model.Pedido;
import com.deliverytech.delivery_api.pedido.model.PedidoItem;
import com.deliverytech.delivery_api.pedido.model.PedidoStatus;
import com.deliverytech.delivery_api.pedido.repository.PedidoRepository;
import com.deliverytech.delivery_api.produto.model.Produto;
import com.deliverytech.delivery_api.produto.repository.ProdutoRepository;
import com.deliverytech.delivery_api.restaurante.model.Restaurante;
import com.deliverytech.delivery_api.restaurante.repository.RestauranteRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do serviço de gerenciamento de pedidos.
 * 
 * Esta classe é responsável por toda a lógica de negócio relacionada a pedidos,
 * incluindo criação, consulta, atualização de status e cancelamento.
 * 
 * @author DeliveryTech Team
 * @version 2.0
 */
@Slf4j
@Service("pedidoService")
@Primary
@RequiredArgsConstructor
@Transactional
public class PedidoServiceImpl implements PedidoService {

  private final PedidoRepository pedidoRepository;
  private final UsuarioRepository usuarioRepository;
  private final RestauranteRepository restauranteRepository;
  private final ProdutoRepository produtoRepository;
  private final MetricsService metricsService;
  private final ModelMapper modelMapper;

  // ==================== MÉTODOS PÚBLICOS ====================

  /**
   * Cria um novo pedido no sistema.
   * 
   * Este método realiza todas as validações necessárias, calcula valores e
   * persiste o pedido com seus itens.
   * 
   * @param dto Dados do pedido a ser criado
   * @return PedidoResponseDTO com os dados do pedido criado
   * @throws EntityNotFoundException se usuário, restaurante ou produto não
   *                                 existir
   * @throws InactiveEntityException se usuário ou restaurante estiver inativo
   * @throws BusinessException       se houver regra de negócio violada
   */
  @Override
  @Transactional(isolation = Isolation.REPEATABLE_READ)
  public PedidoResponseDTO criarPedido(PedidoDTO dto) {
    log.info("Iniciando criação de pedido para usuário ID: {} e restaurante ID: {}",
        dto.getUsuarioId(), dto.getRestauranteId());

    try {
      // 1. Validar e buscar entidades
      Usuario usuario = validarEBuscarUsuario(dto.getUsuarioId());
      Restaurante restaurante = validarEBuscarRestaurante(dto.getRestauranteId());

      // 2. Processar itens do pedido
      List<PedidoItem> itensPedido = processarItensPedido(dto.getItens(), restaurante.getId());

      // 3. Calcular valores do pedido
      BigDecimal subtotal = calcularSubtotal(itensPedido);
      BigDecimal taxaEntrega = restaurante.getTaxaEntrega();
      BigDecimal valorTotal = subtotal.add(taxaEntrega);

      // 4. Criar e configurar pedido
      Pedido pedido = construirPedido(dto, usuario, restaurante, subtotal, taxaEntrega, valorTotal);

      // 5. Salvar pedido
      Pedido pedidoSalvo = pedidoRepository.save(pedido);

      // 6. Associar itens ao pedido
      associarItensPedido(pedidoSalvo, itensPedido);

      // 7. Atualizar métricas
      metricsService.incrementarPedidosComSucesso();

      log.info("Pedido criado com sucesso. ID: {} | Número: {} | Valor: R$ {}",
          pedidoSalvo.getId(), pedidoSalvo.getNumeroPedido(), valorTotal);

      return converterParaDTO(pedidoSalvo);

    } catch (Exception e) {
      log.error("Erro ao criar pedido: {}", e.getMessage(), e);
      throw e;
    }
  }

  /**
   * Busca um pedido específico por ID.
   * 
   * @param id ID do pedido
   * @return PedidoResponseDTO com os dados do pedido
   * @throws EntityNotFoundException se o pedido não for encontrado
   */
  @Override
  @Transactional(readOnly = true)
  public PedidoResponseDTO buscarPedidoPorId(Long id) {
    log.debug("Buscando pedido por ID: {}", id);

    Pedido pedido = buscarPedidoOuLancarExcecao(id);
    return converterParaDTO(pedido);
  }

  /**
   * Busca pedidos de um usuário específico com paginação.
   * 
   * @param usuarioId ID do usuário
   * @param pageable  Configuração de paginação
   * @return Page de PedidoResponseDTO
   */
  @Override
  @Transactional(readOnly = true)
  public Page<PedidoResponseDTO> buscarPedidosPorUsuario(Long usuarioId, Pageable pageable) {
    log.debug("Buscando pedidos do usuário ID: {}", usuarioId);

    validarUsuarioExiste(usuarioId);

    Page<Pedido> pedidosPage = pedidoRepository.findByUsuarioIdOrderByDataPedidoDesc(usuarioId, pageable);
    return pedidosPage.map(this::converterParaDTO);
  }

  /**
   * Busca pedidos do usuário autenticado.
   * 
   * @param filtro   Filtro dos pedidos
   * @param pageable Configuração de paginação
   * @return Page de PedidoResponseDTO
   */
  @Override
  @Transactional(readOnly = true)
  public Page<PedidoResponseDTO> buscarMeusPedidos(PedidoFiltroDTO filtro, Pageable pageable) {
    Long usuarioId = SecurityUtils.getCurrentUserId();

    Page<Pedido> pedidos = pedidoRepository.buscarPedidosComFiltro(
        usuarioId,
        filtro.getStatus(),
        filtro.getDataInicio() != null ? filtro.getDataInicio().atStartOfDay() : null,
        filtro.getDataFim() != null ? filtro.getDataFim().atTime(23, 59, 59) : null,
        filtro.getValorMinimo(),
        filtro.getValorMaximo(),
        pageable);

    return pedidos.map(p -> modelMapper.map(p, PedidoResponseDTO.class));
  }

  /**
   * Busca pedidos de um restaurante específico com paginação.
   * 
   * @param restauranteId ID do restaurante
   * @param pageable      Configuração de paginação
   * @return Page de PedidoResponseDTO
   */
  @Override
  @Transactional(readOnly = true)
  public Page<PedidoResponseDTO> buscarPedidosPorRestaurante(Long restauranteId, Pageable pageable) {
    log.debug("Buscando pedidos do restaurante ID: {}", restauranteId);

    validarRestauranteExiste(restauranteId);

    Page<Pedido> pedidosPage = pedidoRepository.findByRestauranteIdOrderByDataPedidoDesc(restauranteId, pageable);
    return pedidosPage.map(this::converterParaDTO);
  }

  /**
   * Atualiza o status de um pedido.
   * 
   * @param id         ID do pedido
   * @param novoStatus Novo status a ser aplicado
   * @return PedidoResponseDTO com os dados atualizados
   * @throws EntityNotFoundException se o pedido não for encontrado
   * @throws OrderStatusException    se a transição de status não for permitida
   */
  @Override
  public PedidoResponseDTO atualizarStatusPedido(Long id, PedidoStatus novoStatus) {
    log.info("Atualizando status do pedido ID: {} para: {}", id, novoStatus);

    Pedido pedido = buscarPedidoOuLancarExcecao(id);
    PedidoStatus statusAnterior = pedido.getStatus();

    // Validar transição de status
    validarTransicaoStatus(pedido, novoStatus);

    // Atualizar status
    pedido.setStatus(novoStatus);
    Pedido pedidoAtualizado = pedidoRepository.save(pedido);

    log.info("Status do pedido ID: {} atualizado de {} para {}",
        id, statusAnterior, novoStatus);

    return converterParaDTO(pedidoAtualizado);
  }

  /**
   * Calcula o valor total de um pedido baseado nos itens fornecidos.
   * 
   * @param itens Lista de itens do pedido
   * @return BigDecimal com o valor total calculado
   * @throws EntityNotFoundException se algum produto não for encontrado
   */
  @Override
  @Transactional(readOnly = true)
  public BigDecimal calcularTotalPedido(List<PedidoItemDTO> itens) {
    log.debug("Calculando total do pedido com {} itens", itens.size());

    BigDecimal total = itens.stream()
        .map(this::calcularSubtotalItem)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    log.debug("Total calculado: R$ {}", total);
    return total;
  }

  /**
   * Cancela um pedido existente.
   * 
   * @param id ID do pedido a ser cancelado
   * @throws EntityNotFoundException se o pedido não for encontrado
   * @throws OrderStatusException    se o pedido não puder ser cancelado
   */
  @Override
  public void cancelarPedido(Long id) {
    log.info("Iniciando cancelamento do pedido ID: {}", id);

    Pedido pedido = buscarPedidoOuLancarExcecao(id);

    // Validar se pode ser cancelado
    if (!podeSerCancelado(pedido.getStatus())) {
      throw new OrderStatusException(
          id,
          pedido.getStatus().name(),
          "CANCELADO");
    }

    // Cancelar pedido
    pedido.setStatus(PedidoStatus.CANCELADO);
    pedidoRepository.save(pedido);

    log.info("Pedido ID: {} cancelado com sucesso", id);
  }

  /**
   * Verifica se o usuário autenticado é o dono do pedido.
   * 
   * @param pedidoId ID do pedido
   * @return true se for o dono, false caso contrário
   */
  @Override
  @Transactional(readOnly = true)
  public boolean isOwner(Long pedidoId) {
    Long usuarioId = SecurityUtils.getCurrentUserId();
    Pedido pedido = buscarPedidoOuLancarExcecao(pedidoId);

    return pedido.getUsuario().getId().equals(usuarioId);
  }

  // ==================== MÉTODOS PRIVADOS - VALIDAÇÃO ====================

  /**
   * Valida e busca um usuário pelo ID.
   */
  private Usuario validarEBuscarUsuario(Long usuarioId) {
    Usuario usuario = usuarioRepository.findById(usuarioId)
        .orElseThrow(() -> new EntityNotFoundException("Usuario", usuarioId));

    if (!usuario.getAtivo()) {
      throw new InactiveEntityException("Usuario", usuarioId);
    }

    return usuario;
  }

  /**
   * Valida e busca um restaurante pelo ID.
   */
  private Restaurante validarEBuscarRestaurante(Long restauranteId) {
    Restaurante restaurante = restauranteRepository.findById(restauranteId)
        .orElseThrow(() -> new EntityNotFoundException("Restaurante", restauranteId));

    if (!restaurante.isAtivo()) {
      throw new InactiveEntityException("Restaurante", restauranteId);
    }

    return restaurante;
  }

  /**
   * Valida se um usuário existe.
   */
  private void validarUsuarioExiste(Long usuarioId) {
    if (!usuarioRepository.existsById(usuarioId)) {
      throw new EntityNotFoundException("Usuario", usuarioId);
    }
  }

  /**
   * Valida se um restaurante existe.
   */
  private void validarRestauranteExiste(Long restauranteId) {
    if (!restauranteRepository.existsById(restauranteId)) {
      throw new EntityNotFoundException("Restaurante", restauranteId);
    }
  }

  /**
   * Valida a transição de status do pedido.
   */
  private void validarTransicaoStatus(Pedido pedido, PedidoStatus novoStatus) {
    if (!isTransicaoValida(pedido.getStatus(), novoStatus)) {
      throw new OrderStatusException(
          pedido.getId(),
          pedido.getStatus().name(),
          novoStatus.name());
    }
  }

  /**
   * Valida se um produto pertence ao restaurante.
   */
  private void validarProdutoDoRestaurante(Produto produto, Long restauranteId) {
    if (!produto.getRestaurante().getId().equals(restauranteId)) {
      throw new BusinessException(
          "Produto não pertence ao restaurante selecionado",
          produto.getNome());
    }
  }

  /**
   * Valida se um produto está disponível.
   */
  private void validarProdutoDisponivel(Produto produto) {
    if (!produto.isDisponivel()) {
      throw new BusinessException("Produto indisponível", produto.getNome());
    }
  }

  // ==================== MÉTODOS PRIVADOS - PROCESSAMENTO ====================

  /**
   * Processa os itens do pedido, validando produtos e criando ItemPedido.
   */
  private List<PedidoItem> processarItensPedido(List<PedidoItemDTO> itensDTO, Long restauranteId) {
    return itensDTO.stream()
        .map(itemDTO -> processarItemPedido(itemDTO, restauranteId))
        .collect(Collectors.toList());
  }

  /**
   * Processa um item individual do pedido.
   */
  private PedidoItem processarItemPedido(PedidoItemDTO itemDTO, Long restauranteId) {
    // Buscar e validar produto
    Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
        .orElseThrow(() -> new EntityNotFoundException("Produto", itemDTO.getProdutoId()));

    validarProdutoDisponivel(produto);
    validarProdutoDoRestaurante(produto, restauranteId);

    // Criar item do pedido
    PedidoItem item = new PedidoItem();
    item.setProduto(produto);
    item.setQuantidade(itemDTO.getQuantidade());
    item.setPrecoUnitario(produto.getPreco());
    item.setObservacoes(itemDTO.getObservacoes());
    item.calcularSubtotal();

    return item;
  }

  /**
   * Calcula o subtotal de todos os itens do pedido.
   */
  private BigDecimal calcularSubtotal(List<PedidoItem> itens) {
    return itens.stream()
        .map(PedidoItem::getSubtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Calcula o subtotal de um item específico.
   */
  private BigDecimal calcularSubtotalItem(PedidoItemDTO itemDTO) {
    Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
        .orElseThrow(() -> new EntityNotFoundException("Produto", itemDTO.getProdutoId()));

    return produto.getPreco().multiply(BigDecimal.valueOf(itemDTO.getQuantidade()));
  }

  /**
   * Constrói um novo objeto Pedido com os dados fornecidos.
   */
  private Pedido construirPedido(
      PedidoDTO dto,
      Usuario usuario,
      Restaurante restaurante,
      BigDecimal subtotal,
      BigDecimal taxaEntrega,
      BigDecimal valorTotal) {

    Pedido pedido = new Pedido();
    pedido.setNumeroPedido(gerarNumeroPedido());
    pedido.setUsuario(usuario);
    pedido.setRestaurante(restaurante);
    pedido.setDataPedido(LocalDateTime.now());
    pedido.setStatus(PedidoStatus.PENDENTE);
    pedido.setEnderecoEntrega(dto.getEnderecoEntrega());
    pedido.setCep(dto.getCep());
    pedido.setObservacoes(dto.getObservacoes());
    pedido.setFormaPagamento(dto.getFormaPagamento());
    pedido.setSubtotal(subtotal);
    pedido.setTaxaEntrega(taxaEntrega);
    pedido.setValorTotal(valorTotal);

    return pedido;
  }

  /**
   * Associa os itens ao pedido salvo.
   */
  private void associarItensPedido(Pedido pedido, List<PedidoItem> itens) {
    itens.forEach(item -> {
      item.setPedido(pedido);
      pedido.adicionarItem(item);
    });
  }

  /**
   * Gera um número único para o pedido.
   */
  private String gerarNumeroPedido() {
    return "PED-" + LocalDateTime.now().toString().replaceAll("[^0-9]", "").substring(0, 12)
        + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
  }

  // ==================== MÉTODOS PRIVADOS - BUSCA ====================

  /**
   * Busca um pedido por ID ou lança exceção se não encontrado.
   */
  private Pedido buscarPedidoOuLancarExcecao(Long id) {
    return pedidoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Pedido", id));
  }

  // ==================== MÉTODOS PRIVADOS - REGRAS DE NEGÓCIO
  // ====================

  /**
   * Verifica se a transição de status é válida.
   */
  private boolean isTransicaoValida(PedidoStatus statusAtual, PedidoStatus novoStatus) {
    return switch (statusAtual) {
      case PENDENTE -> novoStatus == PedidoStatus.CONFIRMADO
          || novoStatus == PedidoStatus.CANCELADO;
      case CONFIRMADO -> novoStatus == PedidoStatus.PREPARANDO
          || novoStatus == PedidoStatus.CANCELADO;
      case PREPARANDO -> novoStatus == PedidoStatus.SAIU_PARA_ENTREGA;
      case SAIU_PARA_ENTREGA -> novoStatus == PedidoStatus.ENTREGUE;
      case ENTREGUE, CANCELADO -> false;
    };
  }

  /**
   * Verifica se um pedido pode ser cancelado baseado no seu status.
   */
  private boolean podeSerCancelado(PedidoStatus status) {
    return status == PedidoStatus.PENDENTE || status == PedidoStatus.CONFIRMADO;
  }

  // ==================== MÉTODOS PRIVADOS - CONVERSÃO ====================

  /**
   * Converte uma entidade Pedido para PedidoResponseDTO.
   */
  private PedidoResponseDTO converterParaDTO(Pedido pedido) {
    return modelMapper.map(pedido, PedidoResponseDTO.class);
  }
}