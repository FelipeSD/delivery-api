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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Produtos", description = "API para gerenciamento de produtos")
public class ProdutoController {
  @Autowired
  private ProdutoService produtoService;

  @Operation(summary = "Cadastrar novo produto", description = "Cria um novo produto vinculado a um restaurante")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Produto criado com sucesso", content = @Content(schema = @Schema(implementation = ProdutoResponseDTO.class))),
      @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content),
      @ApiResponse(responseCode = "404", description = "Restaurante não encontrado", content = @Content)
  })
  @PostMapping("/produtos")
  public ResponseEntity<ProdutoResponseDTO> cadastrarProduto(@RequestBody ProdutoDTO produtoDTO) {
    ProdutoResponseDTO response = produtoService.cadastrarProduto(produtoDTO);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Buscar produto por ID", description = "Retorna os detalhes de um produto específico")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Produto encontrado", content = @Content(schema = @Schema(implementation = ProdutoResponseDTO.class))),
      @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
  })
  @GetMapping("/produtos/{id}")
  public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
    ProdutoResponseDTO response = produtoService.buscarProdutoPorId(id);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Listar produtos por restaurante", description = "Retorna todos os produtos disponíveis de um restaurante")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso"),
      @ApiResponse(responseCode = "404", description = "Restaurante não encontrado", content = @Content)
  })
  @GetMapping("/restaurantes/{restauranteId}/produtos")
  public ResponseEntity<List<ProdutoResponseDTO>> listarPorRestaurante(@PathVariable Long restauranteId) {
    List<ProdutoResponseDTO> produtos = produtoService.listarPorRestaurante(restauranteId);
    return ResponseEntity.ok(produtos);
  }

  @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
      @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
  })
  @PutMapping("/produtos/{id}")
  public ResponseEntity<ProdutoResponseDTO> atualizarProduto(@PathVariable Long id,
      @RequestBody ProdutoDTO produtoDTO) {
    ProdutoResponseDTO response = produtoService.atualizarProduto(id, produtoDTO);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Alterar disponibilidade do produto", description = "Marca um produto como disponível ou indisponível")
  @PatchMapping("/produtos/{id}/disponibilidade")
  public ResponseEntity<ProdutoResponseDTO> alterarDisponibilidade(@PathVariable Long id,
      @RequestParam boolean disponivel) {
    ProdutoResponseDTO response = produtoService.alterarDisponibilidade(id, disponivel);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Buscar produtos por categoria", description = "Retorna produtos filtrados por categoria")
  @GetMapping("/produtos/categoria/{categoria}")
  public ResponseEntity<List<ProdutoResponseDTO>> produtosPorCategoria(@PathVariable String categoria) {
    List<ProdutoResponseDTO> produtos = produtoService.buscarPorCategoria(categoria);
    return ResponseEntity.ok(produtos);
  }

  // TODO
  // GET /api/produtos/buscar?nome={nome} - Busca por nome
}
