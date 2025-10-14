package com.deliverytech.delivery_api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deliverytech.delivery_api.dtos.ProdutoDTO;
import com.deliverytech.delivery_api.dtos.ProdutoResponseDTO;
import com.deliverytech.delivery_api.services.ProdutoService;

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
public class ProdutoController {
  @Autowired
  private ProdutoService produtoService;

  @PostMapping("/produtos")
  public ResponseEntity<ProdutoResponseDTO> cadastrarProduto(@RequestBody ProdutoDTO produtoDTO) {
    ProdutoResponseDTO response = produtoService.cadastrarProduto(produtoDTO);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/produtos/{id}")
  public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
    ProdutoResponseDTO response = produtoService.buscarProdutoPorId(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/restaurantes/{restauranteId}/produtos")
  public ResponseEntity<List<ProdutoResponseDTO>> produtosDoRestaurante(@PathVariable Long restauranteId) {
    List<ProdutoResponseDTO> produtos = produtoService.listarPorRestaurante(restauranteId);
    return ResponseEntity.ok(produtos);
  }

  @PutMapping("/produtos/{id}")
  public ResponseEntity<ProdutoResponseDTO> atualizarProduto(@PathVariable Long id,
      @RequestBody ProdutoDTO produtoDTO) {
    ProdutoResponseDTO response = produtoService.atualizarProduto(id, produtoDTO);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/produtos/{id}/disponibilidade")
  public ResponseEntity<ProdutoResponseDTO> alterarDisponibilidade(@PathVariable Long id,
      @RequestParam boolean disponivel) {
    ProdutoResponseDTO response = produtoService.alterarDisponibilidade(id, disponivel);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/produtos/categoria/{categoria}")
  public ResponseEntity<List<ProdutoResponseDTO>> produtosPorCategoria(@PathVariable String categoria) {
    List<ProdutoResponseDTO> produtos = produtoService.buscarPorCategoria(categoria);
    return ResponseEntity.ok(produtos);
  }
}
