package com.deliverytech.delivery_api.controllers;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.delivery_api.dtos.ApiResponseWrapper;
import com.deliverytech.delivery_api.dtos.ItemPedidoDTO;
import com.deliverytech.delivery_api.dtos.PagedResponseWrapper;
import com.deliverytech.delivery_api.dtos.PedidoDTO;
import com.deliverytech.delivery_api.dtos.PedidoResponseDTO;
import com.deliverytech.delivery_api.dtos.StatusPedidoDTO;
import com.deliverytech.delivery_api.services.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
@Tag(name = "Pedidos", description = "API para gerenciamento de pedidos")
public class PedidoController {

  @Autowired
  private PedidoService pedidoService;

  @Operation(summary = "Criar novo pedido", description = "Cria um novo pedido no sistema")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
      @ApiResponse(responseCode = "404", description = "Cliente ou restaurante não encontrado")
  })
  @PostMapping
  public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> criarPedido(@Valid @RequestBody PedidoDTO dto) {
    PedidoResponseDTO pedido = pedidoService.criarPedido(dto);
    ApiResponseWrapper<PedidoResponseDTO> response = new ApiResponseWrapper<>(true, pedido,
        "Pedido criado com sucesso");

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(summary = "Buscar pedido por ID", description = "Retorna os detalhes de um pedido específico")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
      @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> buscarPorId(@PathVariable Long id) {
    PedidoResponseDTO pedido = pedidoService.buscarPedidoPorId(id);
    ApiResponseWrapper<PedidoResponseDTO> response = new ApiResponseWrapper<>(true, pedido,
        "Pedido retornado com sucesso");
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Listar pedidos por cliente", description = "Retorna todos os pedidos feitos por um cliente específico")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso"),
      @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
  })
  @GetMapping("/cliente/{clienteId}")
  public ResponseEntity<PagedResponseWrapper<PedidoResponseDTO>> buscarPorCliente(
      @PathVariable Long clienteId,
      @PageableDefault(size = 20) Pageable pageable) {
    Page<PedidoResponseDTO> pedidos = pedidoService.buscarPedidosPorCliente(clienteId, pageable);
    PagedResponseWrapper<PedidoResponseDTO> response = new PagedResponseWrapper<>(pedidos);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Atualizar status do pedido", description = "Atualiza o status de um pedido existente")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Status do pedido atualizado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Status inválido fornecido"),
      @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
  })
  @PatchMapping("/{id}/status")
  public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> atualizarStatus(
      @PathVariable Long id,
      @Valid @RequestBody StatusPedidoDTO statusDTO) {
    PedidoResponseDTO pedido = pedidoService.atualizarStatusPedido(id, statusDTO.getStatus());
    ApiResponseWrapper<PedidoResponseDTO> response = new ApiResponseWrapper<>(true, pedido,
        "Status do pedido atualizado com sucesso");
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Cancelar pedido", description = "Cancela um pedido existente")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Pedido cancelado com sucesso"),
      @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
      @ApiResponse(responseCode = "400", description = "Pedido não pode ser cancelado")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponseWrapper<Void>> cancelarPedido(@PathVariable Long id) {
    pedidoService.cancelarPedido(id);
    ApiResponseWrapper<Void> response = new ApiResponseWrapper<>(true, null, "Pedido cancelado com sucesso");
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
  }

  @Operation(summary = "Calcular total do pedido", description = "Calcula o valor total de um pedido com base nos itens fornecidos")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Total do pedido calculado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos")
  })
  @PostMapping("/calcular")
  public ResponseEntity<ApiResponseWrapper<BigDecimal>> calcularTotal(@Valid @RequestBody List<ItemPedidoDTO> itens) {
    BigDecimal total = pedidoService.calcularTotalPedido(itens);
    ApiResponseWrapper<BigDecimal> response = new ApiResponseWrapper<>(true, total,
        "Total do pedido calculado com sucesso");
    return ResponseEntity.ok(response);
  }
}