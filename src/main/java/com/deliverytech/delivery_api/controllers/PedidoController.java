package com.deliverytech.delivery_api.controllers;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.deliverytech.delivery_api.dtos.ItemPedidoDTO;
import com.deliverytech.delivery_api.dtos.PedidoDTO;
import com.deliverytech.delivery_api.dtos.PedidoResponseDTO;
import com.deliverytech.delivery_api.dtos.StatusPedidoDTO;
import com.deliverytech.delivery_api.services.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

  @Autowired
  private PedidoService pedidoService;

  @PostMapping
  public ResponseEntity<PedidoResponseDTO> criarPedido(@Valid @RequestBody PedidoDTO dto) {
    PedidoResponseDTO pedido = pedidoService.criarPedido(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
    PedidoResponseDTO pedido = pedidoService.buscarPedidoPorId(id);
    return ResponseEntity.ok(pedido);
  }

  @GetMapping("/cliente/{clienteId}")
  public ResponseEntity<List<PedidoResponseDTO>> buscarPorCliente(@PathVariable Long clienteId) {
    List<PedidoResponseDTO> pedidos = pedidoService.buscarPedidosPorCliente(clienteId);
    return ResponseEntity.ok(pedidos);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<PedidoResponseDTO> atualizarStatus(
      @PathVariable Long id,
      @Valid @RequestBody StatusPedidoDTO statusDTO) {
    PedidoResponseDTO pedido = pedidoService.atualizarStatusPedido(id, statusDTO.getStatus());
    return ResponseEntity.ok(pedido);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> cancelarPedido(@PathVariable Long id) {
    pedidoService.cancelarPedido(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/calcular")
  public ResponseEntity<BigDecimal> calcularTotal(@Valid @RequestBody List<ItemPedidoDTO> itens) {
    BigDecimal total = pedidoService.calcularTotalPedido(itens);
    return ResponseEntity.ok(total);
  }
}