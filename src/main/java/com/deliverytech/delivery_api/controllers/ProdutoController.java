package com.deliverytech.delivery_api.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.deliverytech.delivery_api.dtos.ProdutoDTO;
import com.deliverytech.delivery_api.dtos.ProdutoFiltroDTO;
import com.deliverytech.delivery_api.dtos.ProdutoPatchDTO;
import com.deliverytech.delivery_api.dtos.ProdutoResponseDTO;
import com.deliverytech.delivery_api.entities.Usuario;
import com.deliverytech.delivery_api.monitoring.audit.AuditService;
import com.deliverytech.delivery_api.services.ProdutoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Produtos", description = "API para gerenciamento de produtos")
public class ProdutoController {
  @Autowired
  private ProdutoService produtoService;

  @Autowired
  private AuditService auditService;

  @Operation(summary = "Cadastrar novo produto", description = "Cria um novo produto vinculado a um restaurante")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Produto criado com sucesso", content = @Content(schema = @Schema(implementation = ProdutoResponseDTO.class))),
      @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos", content = @Content),
      @ApiResponse(responseCode = "404", description = "Restaurante não encontrado", content = @Content)
  })
  @PostMapping("/produtos")
  @PreAuthorize("hasRole('RESTAURANTE') or hasRole('ADMIN')")
  public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> cadastrarProduto(
      @Valid @RequestBody ProdutoDTO produtoDTO, @AuthenticationPrincipal Usuario usuarioLogado) {
    ProdutoResponseDTO produto = produtoService.cadastrarProduto(produtoDTO);

    String userId = String.valueOf(usuarioLogado.getId());

    auditService.logUserAction(
        userId,
        "CRIAR_PRODUTO",
        "Produto",
        Map.of("produtoId", produto.getId(), "nome", produto.getNome()));

    ApiResponseWrapper<ProdutoResponseDTO> response = new ApiResponseWrapper<>(true, produto,
        "Produto criado com sucesso");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(summary = "Buscar produto por ID", description = "Retorna os detalhes de um produto específico")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Produto encontrado", content = @Content(schema = @Schema(implementation = ProdutoResponseDTO.class))),
      @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
  })
  @GetMapping("/produtos/{id}")
  public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> buscarPorId(@PathVariable Long id) {
    ProdutoResponseDTO produto = produtoService.buscarProdutoPorId(id);
    ApiResponseWrapper<ProdutoResponseDTO> response = new ApiResponseWrapper<>(true, produto,
        "Produto encontrado com sucesso");
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Listar produtos por restaurante", description = "Retorna todos os produtos disponíveis de um restaurante")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso"),
      @ApiResponse(responseCode = "404", description = "Restaurante não encontrado", content = @Content)
  })
  @GetMapping("/restaurantes/{restauranteId}/produtos")
  public ResponseEntity<PagedResponseWrapper<ProdutoResponseDTO>> listarPorRestaurante(@PathVariable Long restauranteId,
      @PageableDefault(size = 20) Pageable pageable) {
    Page<ProdutoResponseDTO> produtos = produtoService.listarPorRestaurante(restauranteId, pageable);
    PagedResponseWrapper<ProdutoResponseDTO> response = new PagedResponseWrapper<>(true, produtos);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
      @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
  })
  @PutMapping("/produtos/{id}")
  @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @produtoService.isOwner(#id))")
  public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> atualizarProduto(@PathVariable Long id,
      @Valid @RequestBody ProdutoDTO produtoDTO) {
    ProdutoResponseDTO produto = produtoService.atualizarProduto(id, produtoDTO);
    ApiResponseWrapper<ProdutoResponseDTO> response = new ApiResponseWrapper<>(true, produto,
        "Produto atualizado com sucesso");
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Alterar disponibilidade do produto", description = "Marca um produto como disponível ou indisponível")
  @PatchMapping("/produtos/{id}/disponibilidade")
  @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @produtoService.isOwner(#id))")
  public ResponseEntity<ApiResponseWrapper<ProdutoResponseDTO>> alterarDisponibilidade(@PathVariable Long id,
      @RequestBody ProdutoPatchDTO dto) {
    ProdutoResponseDTO produto = produtoService.alterarDisponibilidade(id, dto.isDisponivel());
    ApiResponseWrapper<ProdutoResponseDTO> response = new ApiResponseWrapper<>(true, produto,
        "Produto atualizado com sucesso");
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Buscar produtos por categoria", description = "Retorna produtos filtrados por categoria")
  @GetMapping("/produtos/categoria/{categoria}")
  public ResponseEntity<PagedResponseWrapper<ProdutoResponseDTO>> produtosPorCategoria(@PathVariable String categoria,
      @PageableDefault(size = 20) Pageable pageable) {
    Page<ProdutoResponseDTO> produtos = produtoService.buscarPorCategoria(categoria, pageable);
    PagedResponseWrapper<ProdutoResponseDTO> response = new PagedResponseWrapper<>(true, produtos);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Buscar produtos com filtros", description = "Filtra produtos por nome, categoria, preço, disponibilidade e restaurante")
  @GetMapping("/produtos/buscar")
  public ResponseEntity<PagedResponseWrapper<ProdutoResponseDTO>> buscarProdutos(
      ProdutoFiltroDTO filtro,
      @PageableDefault(size = 20) Pageable pageable) {

    Page<ProdutoResponseDTO> produtos = produtoService.buscarComFiltros(filtro, pageable);
    PagedResponseWrapper<ProdutoResponseDTO> response = new PagedResponseWrapper<>(true, produtos);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Deletar produto", description = "Remove um produto existente")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Produto deletado com sucesso"),
      @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content)
  })
  @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @produtoService.isOwner(#id))")
  @DeleteMapping("/produtos/{id}")
  public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
    produtoService.deletarProduto(id);
    return ResponseEntity.noContent().build();
  }
}
