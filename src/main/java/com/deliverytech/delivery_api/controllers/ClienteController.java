package com.deliverytech.delivery_api.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.delivery_api.dtos.ClienteDTO;
import com.deliverytech.delivery_api.dtos.ClienteResponseDTO;
import com.deliverytech.delivery_api.services.ClienteService;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

  @Autowired
  private ClienteService clienteService;

  @PostMapping
  public ResponseEntity<ClienteResponseDTO> cadastrarCliente(@Valid @RequestBody ClienteDTO dto) {
    ClienteResponseDTO cliente = clienteService.cadastrarCliente(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
    ClienteResponseDTO cliente = clienteService.buscarClientePorId(id);
    return ResponseEntity.ok(cliente);
  }

  @GetMapping
  public ResponseEntity<List<ClienteResponseDTO>> listarClientesAtivos() {
    List<ClienteResponseDTO> clientes = clienteService.listarClientesAtivos();
    return ResponseEntity.ok(clientes);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ClienteResponseDTO> atualizarCliente(
      @PathVariable Long id,
      @Valid @RequestBody ClienteDTO dto) {
    ClienteResponseDTO cliente = clienteService.atualizarCliente(id, dto);
    return ResponseEntity.ok(cliente);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<ClienteResponseDTO> ativarDesativarCliente(@PathVariable Long id) {
    ClienteResponseDTO cliente = clienteService.ativarDesativarCliente(id);
    return ResponseEntity.ok(cliente);
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<ClienteResponseDTO> buscarPorEmail(@PathVariable String email) {
    ClienteResponseDTO cliente = clienteService.buscarClientePorEmail(email);
    return ResponseEntity.ok(cliente);
  }
}