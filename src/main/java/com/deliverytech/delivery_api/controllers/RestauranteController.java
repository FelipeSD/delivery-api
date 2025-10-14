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

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
public class RestauranteController {
  @Autowired
  private RestauranteServiceImpl restauranteService;
  
  
  // POST /api/restaurantes - Cadastrar restaurante
  @PostMapping
  public ResponseEntity<RestauranteResponseDTO> cadastrarRestaurante(@RequestBody RestauranteDTO restaurante) {
    RestauranteResponseDTO novo = restauranteService.cadastrar(restaurante);
    return ResponseEntity.status(HttpStatus.CREATED).body(novo);
  }

  // GET /api/restaurantes/{id} - Buscar por ID
  @GetMapping("/{id}")
  public ResponseEntity<RestauranteResponseDTO> buscarPorId(@PathVariable Long id) {
    RestauranteResponseDTO restaurante = restauranteService.buscarPorId(id);
    if (restaurante != null) {
      return ResponseEntity.ok(restaurante);
    }
    return ResponseEntity.notFound().build();
  }

  // GET /api/restaurantes - Listar disponíveis
  @GetMapping
  public ResponseEntity<List<RestauranteResponseDTO>> listarDisponiveis() {
    List<RestauranteResponseDTO> restaurantes = restauranteService.listarDisponiveis();
    return ResponseEntity.ok(restaurantes);
  }

  // GET /api/restaurantes/categoria/{categoria} - Por categoria
  @GetMapping("/categoria/{categoria}")
  public ResponseEntity<List<RestauranteResponseDTO>> listarPorCategoria(@PathVariable String categoria) {
    List<RestauranteResponseDTO> restaurantes = restauranteService.listarPorCategoria(categoria);
    return ResponseEntity.ok(restaurantes);
  }

  // PUT /api/restaurantes/{id} - Atualizar restaurante
  @PutMapping("/{id}")
  public ResponseEntity<RestauranteResponseDTO> atualizarRestaurante(@PathVariable Long id, @RequestBody RestauranteDTO restaurante) {
    RestauranteResponseDTO atualizado = restauranteService.atualizar(id, restaurante);
    if (atualizado != null) {
      return ResponseEntity.ok(atualizado);
    }
    return ResponseEntity.notFound().build();
  }

  // GET /api/restaurantes/{id}/taxa-entrega/{cep} - Calcular taxa
  @GetMapping("/{id}/taxa-entrega/{cep}")
  public ResponseEntity<TaxaEntregaResponseDTO> calcularTaxaEntrega(@PathVariable Long id, @PathVariable String cep) {
    TaxaEntregaResponseDTO taxa = restauranteService.calcularTaxaEntrega(id, cep);
    if (taxa != null) {
      return ResponseEntity.ok(taxa);
    }
    return ResponseEntity.notFound().build();
  }
  
}
