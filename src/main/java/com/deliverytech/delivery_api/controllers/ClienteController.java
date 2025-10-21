package com.deliverytech.delivery_api.controllers;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.deliverytech.delivery_api.dtos.ApiResponseWrapper;
import com.deliverytech.delivery_api.dtos.ClienteDTO;
import com.deliverytech.delivery_api.dtos.ClienteResponseDTO;
import com.deliverytech.delivery_api.dtos.PagedResponseWrapper;
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
  public ResponseEntity<ApiResponseWrapper<ClienteResponseDTO>> cadastrarCliente(@Valid @RequestBody ClienteDTO dto) {
    ClienteResponseDTO cliente = clienteService.cadastrarCliente(dto);
    ApiResponseWrapper<ClienteResponseDTO> response = new ApiResponseWrapper<>(true, cliente,
        "Cliente criado com sucesso");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(summary = "Buscar cliente por ID", description = "Retorna os detalhes de um cliente específico")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
      @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponseWrapper<ClienteResponseDTO>> buscarPorId(@PathVariable Long id) {
    ClienteResponseDTO cliente = clienteService.buscarClientePorId(id);
    ApiResponseWrapper<ClienteResponseDTO> response = new ApiResponseWrapper<>(true, cliente,
        "Cliente encontrado com sucesso");
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Listar clientes ativos", description = "Retorna uma lista de todos os clientes ativos no sistema")
  @GetMapping
  public ResponseEntity<PagedResponseWrapper<ClienteResponseDTO>> listarClientesAtivos(
      @PageableDefault(size = 20) Pageable pageable) {
    Page<ClienteResponseDTO> clientes = clienteService.listarClientesAtivos(pageable);
    PagedResponseWrapper<ClienteResponseDTO> response = new PagedResponseWrapper<>(true, clientes);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Atualizar cliente", description = "Atualiza os dados de um cliente existente")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados inválidos"),
      @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
  })
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponseWrapper<ClienteResponseDTO>> atualizarCliente(
      @PathVariable Long id,
      @Valid @RequestBody ClienteDTO dto) {
    ClienteResponseDTO cliente = clienteService.atualizarCliente(id, dto);
    ApiResponseWrapper<ClienteResponseDTO> response = new ApiResponseWrapper<>(true, cliente,
        "Cliente atualizado com sucesso");
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Ativar/Desativar cliente", description = "Ativa ou desativa um cliente no sistema")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Status do cliente atualizado com sucesso"),
      @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
  })
  @PatchMapping("/{id}/status")
  public ResponseEntity<ApiResponseWrapper<ClienteResponseDTO>> ativarDesativarCliente(@PathVariable Long id) {
    ClienteResponseDTO cliente = clienteService.ativarDesativarCliente(id);
    ApiResponseWrapper<ClienteResponseDTO> response = new ApiResponseWrapper<>(true, cliente,
        "Status do cliente atualizado com sucesso");
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Buscar cliente por email", description = "Retorna os detalhes de um cliente específico pelo email")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
      @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
  })
  @GetMapping("/email/{email}")
  public ResponseEntity<ApiResponseWrapper<ClienteResponseDTO>> buscarPorEmail(@PathVariable String email) {
    ClienteResponseDTO cliente = clienteService.buscarClientePorEmail(email);
    ApiResponseWrapper<ClienteResponseDTO> response = new ApiResponseWrapper<>(true, cliente,
        "Cliente encontrado com sucesso");
    return ResponseEntity.ok(response);
  }
}