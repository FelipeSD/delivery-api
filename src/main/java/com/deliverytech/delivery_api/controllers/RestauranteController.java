package com.deliverytech.delivery_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.deliverytech.delivery_api.dtos.PagedResponseWrapper;
import com.deliverytech.delivery_api.dtos.RestauranteDTO;
import com.deliverytech.delivery_api.dtos.RestauranteResponseDTO;
import com.deliverytech.delivery_api.dtos.TaxaEntregaResponseDTO;
import com.deliverytech.delivery_api.services.RestauranteServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
@Tag(name = "Restaurantes", description = "API para gerenciamento de restaurantes")
public class RestauranteController {
  @Autowired
  private RestauranteServiceImpl restauranteService;

  // POST /api/restaurantes - Cadastrar restaurante
  @Operation(summary = "Cadastrar restaurante", description = "Cria um novo restaurante no sistema")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso"),
      @ApiResponse(responseCode = "400", description = "Dados inválidos")
  })
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> cadastrarRestaurante(
      @Valid @RequestBody RestauranteDTO restaurante) {
    RestauranteResponseDTO novo = restauranteService.cadastrar(restaurante);
    ApiResponseWrapper<RestauranteResponseDTO> response = new ApiResponseWrapper<>(true, novo,
        "Restaurante criado com sucesso");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // GET /api/restaurantes/{id} - Buscar por ID
  @Operation(summary = "Buscar restaurante por ID", description = "Retorna os detalhes de um restaurante específico")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
      @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> buscarPorId(@PathVariable Long id) {
    RestauranteResponseDTO restaurante = restauranteService.buscarPorId(id);
    ApiResponseWrapper<RestauranteResponseDTO> response = new ApiResponseWrapper<>(true, restaurante,
        "Restaurante encontrado");
    if (restaurante != null) {
      return ResponseEntity.ok(response);
    }
    return ResponseEntity.notFound().build();
  }

  // GET /api/restaurantes - Listar disponíveis
  @Operation(summary = "Listar restaurantes disponíveis", description = "Retorna todos os restaurantes que estão disponíveis para receber pedidos")
  @GetMapping
  public ResponseEntity<PagedResponseWrapper<RestauranteResponseDTO>> listarDisponiveis(
      @PageableDefault(size = 20) Pageable pageable) {
    Page<RestauranteResponseDTO> restaurantes = restauranteService.listarDisponiveis(pageable);
    PagedResponseWrapper<RestauranteResponseDTO> response = new PagedResponseWrapper<>(true, restaurantes);
    return ResponseEntity.ok(response);
  }

  // GET /api/restaurantes/categoria/{categoria} - Por categoria
  @Operation(summary = "Listar restaurantes por categoria", description = "Retorna todos os restaurantes que pertencem a uma categoria específica")
  @GetMapping("/categoria/{categoria}")
  public ResponseEntity<PagedResponseWrapper<RestauranteResponseDTO>> listarPorCategoria(@PathVariable String categoria,
      @PageableDefault(size = 20) Pageable pageable) {
    Page<RestauranteResponseDTO> restaurantes = restauranteService.listarPorCategoria(categoria, pageable);
    PagedResponseWrapper<RestauranteResponseDTO> response = new PagedResponseWrapper<>(true, restaurantes);
    return ResponseEntity.ok(response);
  }

  // PUT /api/restaurantes/{id} - Atualizar restaurante
  @Operation(summary = "Atualizar restaurante", description = "Atualiza os dados de um restaurante existente")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
      @ApiResponse(responseCode = "404", description = "Restaurante não encontrado"),
      @ApiResponse(responseCode = "400", description = "Dados inválidos")
  })
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteService.isOwner(#id))")
  public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> atualizarRestaurante(@PathVariable Long id,
      @Valid @RequestBody RestauranteDTO restaurante) {
    RestauranteResponseDTO atualizado = restauranteService.atualizar(id, restaurante);
    if (atualizado != null) {
      ApiResponseWrapper<RestauranteResponseDTO> response = new ApiResponseWrapper<>(true, atualizado,
          "Restaurante atualizado com sucesso");
      return ResponseEntity.ok(response);
    }
    return ResponseEntity.notFound().build();
  }

  // PUT /api/restaurantes/{id}/status - Ativar/Desativar restaurante
  @Operation(summary = "Ativar/Desativar restaurante", description = "Altera o status de ativo/inativo de um restaurante")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
      @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
  })
  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteService.isOwner(#id))")
  public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> alterarStatusRestaurante(@PathVariable Long id) {
    RestauranteResponseDTO atualizado = restauranteService.alterarStatus(id);
    if (atualizado != null) {
      ApiResponseWrapper<RestauranteResponseDTO> response = new ApiResponseWrapper<>(true, atualizado,
          "Status alterado com sucesso");
      return ResponseEntity.ok(response);
    }
    return ResponseEntity.notFound().build();
  }

  // GET /api/restaurantes/{id}/taxa-entrega/{cep} - Calcular taxa
  @Operation(summary = "Calcular taxa de entrega", description = "Calcula a taxa de entrega para um restaurante com base no CEP fornecido")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Taxa de entrega calculada com sucesso"),
      @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
  })
  @GetMapping("/{id}/taxa-entrega/{cep}")
  public ResponseEntity<TaxaEntregaResponseDTO> calcularTaxaEntrega(@PathVariable Long id, @PathVariable String cep) {
    TaxaEntregaResponseDTO taxa = restauranteService.calcularTaxaEntrega(id, cep);
    if (taxa != null) {
      return ResponseEntity.ok(taxa);
    }
    return ResponseEntity.notFound().build();
  }

}
