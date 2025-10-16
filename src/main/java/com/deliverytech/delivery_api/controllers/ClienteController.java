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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
@Tag(name = "Clientes", description = "API para gerenciamento de clientes")
public class ClienteController {

  @Autowired
  private ClienteService clienteService;

  @Operation(summary = "Cadastrar cliente", description = "Cria um novo cliente no sistema")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados inválidos")
  })
  @PostMapping
  public ResponseEntity<ClienteResponseDTO> cadastrarCliente(@Valid @RequestBody ClienteDTO dto) {
    ClienteResponseDTO cliente = clienteService.cadastrarCliente(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
  }

  @Operation(summary = "Buscar cliente por ID", description = "Retorna os detalhes de um cliente específico")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
      @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
    ClienteResponseDTO cliente = clienteService.buscarClientePorId(id);
    return ResponseEntity.ok(cliente);
  }

  @Operation(summary = "Listar clientes ativos", description = "Retorna uma lista de todos os clientes ativos no sistema")
  @GetMapping
  public ResponseEntity<List<ClienteResponseDTO>> listarClientesAtivos() {
    List<ClienteResponseDTO> clientes = clienteService.listarClientesAtivos();
    return ResponseEntity.ok(clientes);
  }

  @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados inválidos"),
      @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
  })
  @PutMapping("/{id}")
  public ResponseEntity<ClienteResponseDTO> atualizarCliente(
      @PathVariable Long id,
      @Valid @RequestBody ClienteDTO dto) {
    ClienteResponseDTO cliente = clienteService.atualizarCliente(id, dto);
    return ResponseEntity.ok(cliente);
  }

  @Operation(summary = "Ativar/Desativar cliente", description = "Ativa ou desativa um cliente no sistema")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Status do cliente atualizado com sucesso"),
      @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
  })
  @PatchMapping("/{id}/status")
  public ResponseEntity<ClienteResponseDTO> ativarDesativarCliente(@PathVariable Long id) {
    ClienteResponseDTO cliente = clienteService.ativarDesativarCliente(id);
    return ResponseEntity.ok(cliente);
  }

  @Operation(summary = "Buscar cliente por email", description = "Retorna os detalhes de um cliente específico pelo email")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
      @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
  })
  @GetMapping("/email/{email}")
  public ResponseEntity<ClienteResponseDTO> buscarPorEmail(@PathVariable String email) {
    ClienteResponseDTO cliente = clienteService.buscarClientePorEmail(email);
    return ResponseEntity.ok(cliente);
  }
}