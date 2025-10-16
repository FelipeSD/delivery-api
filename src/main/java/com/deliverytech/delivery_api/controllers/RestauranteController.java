package com.deliverytech.delivery_api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.delivery_api.dtos.RestauranteDTO;
import com.deliverytech.delivery_api.dtos.RestauranteResponseDTO;
import com.deliverytech.delivery_api.dtos.TaxaEntregaResponseDTO;
import com.deliverytech.delivery_api.services.RestauranteServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
  public ResponseEntity<RestauranteResponseDTO> cadastrarRestaurante(@RequestBody RestauranteDTO restaurante) {
    RestauranteResponseDTO novo = restauranteService.cadastrar(restaurante);
    return ResponseEntity.status(HttpStatus.CREATED).body(novo);
  }

  // GET /api/restaurantes/{id} - Buscar por ID
  @Operation(summary = "Buscar restaurante por ID", description = "Retorna os detalhes de um restaurante específico")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
      @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
  })
  @GetMapping("/{id}")
  public ResponseEntity<RestauranteResponseDTO> buscarPorId(@PathVariable Long id) {
    RestauranteResponseDTO restaurante = restauranteService.buscarPorId(id);
    if (restaurante != null) {
      return ResponseEntity.ok(restaurante);
    }
    return ResponseEntity.notFound().build();
  }

  // GET /api/restaurantes - Listar disponíveis
  @Operation(summary = "Listar restaurantes disponíveis", description = "Retorna todos os restaurantes que estão disponíveis para receber pedidos")
  @GetMapping
  public ResponseEntity<List<RestauranteResponseDTO>> listarDisponiveis() {
    List<RestauranteResponseDTO> restaurantes = restauranteService.listarDisponiveis();
    return ResponseEntity.ok(restaurantes);
  }

  // GET /api/restaurantes/categoria/{categoria} - Por categoria
  @Operation(summary = "Listar restaurantes por categoria", description = "Retorna todos os restaurantes que pertencem a uma categoria específica")
  @GetMapping("/categoria/{categoria}")
  public ResponseEntity<List<RestauranteResponseDTO>> listarPorCategoria(@PathVariable String categoria) {
    List<RestauranteResponseDTO> restaurantes = restauranteService.listarPorCategoria(categoria);
    return ResponseEntity.ok(restaurantes);
  }

  // PUT /api/restaurantes/{id} - Atualizar restaurante
  @Operation(summary = "Atualizar restaurante", description = "Atualiza os dados de um restaurante existente")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
      @ApiResponse(responseCode = "404", description = "Restaurante não encontrado"),
      @ApiResponse(responseCode = "400", description = "Dados inválidos")
  })
  @PutMapping("/{id}")
  public ResponseEntity<RestauranteResponseDTO> atualizarRestaurante(@PathVariable Long id,
      @RequestBody RestauranteDTO restaurante) {
    RestauranteResponseDTO atualizado = restauranteService.atualizar(id, restaurante);
    if (atualizado != null) {
      return ResponseEntity.ok(atualizado);
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
