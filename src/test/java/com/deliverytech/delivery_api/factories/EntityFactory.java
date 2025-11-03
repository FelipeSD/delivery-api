package com.deliverytech.delivery_api.factories;

import com.deliverytech.delivery_api.dtos.ClienteDTO;
import com.deliverytech.delivery_api.dtos.ItemPedidoDTO;
import com.deliverytech.delivery_api.dtos.PedidoDTO;
import com.deliverytech.delivery_api.dtos.ProdutoDTO;
import com.deliverytech.delivery_api.dtos.RestauranteDTO;
import com.deliverytech.delivery_api.dtos.StatusPedidoDTO;
import com.deliverytech.delivery_api.entities.Cliente;
import com.deliverytech.delivery_api.entities.Pedido;
import com.deliverytech.delivery_api.entities.Produto;
import com.deliverytech.delivery_api.entities.Restaurante;
import com.deliverytech.delivery_api.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EntityFactory {

  public static Restaurante criarRestaurante() {
    Restaurante r = new Restaurante();
    r.setNome("Pizzaria Bella");
    r.setCategoria("Italiana");
    r.setEndereco("Rua das Flores, 123");
    r.setEmail("contato@pizzaria.com");
    r.setTelefone("11999999999");
    r.setCnpj("12345678000195");
    r.setTaxaEntrega(new BigDecimal("8.50"));
    r.setTempoEntregaMin(30);
    r.setTempoEntregaMax(45);
    r.setAtivo(true);
    return r;
  }

  public static Produto criarProduto(Restaurante restaurante) {
    Produto p = new Produto();
    p.setNome("Pizza Calabresa");
    p.setDescricao("Molho de tomate, calabresa e cebola");
    p.setPreco(new BigDecimal("42.90"));
    p.setCategoria("Pizzas");
    p.setRestaurante(restaurante);
    p.setDisponivel(true);
    return p;
  }

  public static ProdutoDTO criarProdutoDTO(Long restauranteId) {
    ProdutoDTO dto = new ProdutoDTO();
    dto.setNome("Pizza Margherita");
    dto.setDescricao("Molho de tomate, mussarela e manjericão");
    dto.setPreco(new BigDecimal("45.90"));
    dto.setCategoria("Pizzas");
    dto.setRestauranteId(restauranteId);
    dto.setImagemUrl("https://example.com/pizza.jpg");
    dto.setDisponivel(true);
    return dto;
  }

  // Métodos para Cliente
  public static Cliente criarClienteAtivo() {
    Cliente c = new Cliente();
    c.setNome("Maria Oliveira");
    c.setEmail("maria@email.com");
    c.setTelefone("11999999999");
    c.setEndereco("Rua A, 100, São Paulo, SP");
    c.setAtivo(true);
    return c;
  }

  public static Cliente criarClienteInativo() {
    Cliente c = new Cliente();
    c.setNome("Carlos Souza");
    c.setEmail("carlos@email.com");
    c.setTelefone("11888888888");
    c.setEndereco("Rua B, 200, São Paulo, SP");
    c.setAtivo(false);
    return c;
  }

  public static ClienteDTO criarClienteDTO() {
    ClienteDTO dto = new ClienteDTO();
    dto.setNome("João Silva");
    dto.setEmail("joao@email.com");
    dto.setTelefone("11777777777");
    dto.setEndereco("Rua A, 123, São Paulo, SP");
    return dto;
  }

  public static ClienteDTO criarClienteDTOInvalido() {
    ClienteDTO dto = new ClienteDTO();
    dto.setNome(""); // inválido
    dto.setEmail(null); // inválido
    dto.setTelefone("11777777777");
    dto.setEndereco("Rua A, 123");
    return dto;
  }

  // Métodos para Restaurante
  public static Restaurante criarRestauranteAtivo() {
    Restaurante r = new Restaurante();
    r.setNome("Burger King");
    r.setCategoria("Americana");
    r.setEndereco("Av. Paulista, 1000");
    r.setEmail("burgerking@email.com");
    r.setTelefone("11888888888");
    r.setCnpj("75825600033170");
    r.setTaxaEntrega(new BigDecimal("4.00"));
    r.setTempoEntregaMin(10);
    r.setTempoEntregaMax(20);
    r.setHorarioFuncionamento("09:00-22:00");
    r.setAtivo(true);
    return r;
  }

  public static Restaurante criarRestauranteInativo() {
    Restaurante r = new Restaurante();
    r.setNome("Sushi Bar");
    r.setCategoria("Japonesa");
    r.setEndereco("Rua Augusta, 500");
    r.setEmail("sushi@email.com");
    r.setTelefone("11777777777");
    r.setCnpj("12345678000196");
    r.setTaxaEntrega(new BigDecimal("6.50"));
    r.setTempoEntregaMin(20);
    r.setTempoEntregaMax(35);
    r.setHorarioFuncionamento("11:00-23:00");
    r.setAtivo(false);
    return r;
  }

  public static RestauranteDTO criarRestauranteDTO() {
    RestauranteDTO dto = new RestauranteDTO();
    dto.setNome("Pizza Express");
    dto.setCategoria("Italiana");
    dto.setEndereco("Rua das Flores, 123");
    dto.setEmail("pizzaexpress@email.com");
    dto.setTelefone("11999999999");
    dto.setCnpj("12345678000195");
    dto.setTaxaEntrega(new BigDecimal("5.50"));
    dto.setTempoEntregaMin(10);
    dto.setTempoEntregaMax(20);
    dto.setHorarioFuncionamento("09:00-22:00");
    return dto;
  }

  public static RestauranteDTO criarRestauranteDTOInvalido() {
    RestauranteDTO dto = new RestauranteDTO();
    dto.setNome(""); // inválido
    dto.setCategoria("Italiana");
    dto.setEndereco("Rua das Flores, 123");
    dto.setEmail("invalido@email.com");
    dto.setTelefone("123"); // inválido
    dto.setCnpj("12345678000195");
    dto.setTaxaEntrega(new BigDecimal("5.50"));
    dto.setTempoEntregaMin(10);
    dto.setTempoEntregaMax(20);
    return dto;
  }

  // Métodos para Pedido
  public static Pedido criarPedido(Cliente cliente, Restaurante restaurante) {
    Pedido p = new Pedido();
    p.setCliente(cliente);
    p.setRestaurante(restaurante);
    p.setDataPedido(LocalDateTime.now());
    p.setStatus(StatusPedido.PENDENTE);
    p.setEnderecoEntrega("Rua A, 123 - São Paulo/SP");
    p.setSubtotal(new BigDecimal("91.80"));
    p.setTaxaEntrega(new BigDecimal("8.50"));
    p.setValorTotal(new BigDecimal("100.30"));
    p.setCep("46464584");
    p.setFormaPagamento("PIX");
    return p;
  }

  public static PedidoDTO criarPedidoDTO(Long clienteId, Long restauranteId, Long produtoId) {
    PedidoDTO dto = new PedidoDTO();
    dto.setClienteId(clienteId);
    dto.setRestauranteId(restauranteId);
    dto.setEnderecoEntrega("Rua A, 123 - São Paulo/SP");
    dto.setCep("46464584");
    dto.setObservacoes("Por favor, entregar após as 18h");
    dto.setFormaPagamento("PIX");

    List<ItemPedidoDTO> itens = new ArrayList<>();
    ItemPedidoDTO item = new ItemPedidoDTO();
    item.setProdutoId(produtoId);
    item.setQuantidade(2);
    itens.add(item);
    dto.setItens(itens);

    return dto;
  }

  public static PedidoDTO criarPedidoDTOInvalido() {
    PedidoDTO dto = new PedidoDTO();
    dto.setClienteId(null); // inválido
    dto.setRestauranteId(null); // inválido
    dto.setItens(new ArrayList<>()); // vazio - inválido
    return dto;
  }

  public static ItemPedidoDTO criarItemPedidoDTO(Long produtoId, Integer quantidade) {
    ItemPedidoDTO item = new ItemPedidoDTO();
    item.setProdutoId(produtoId);
    item.setQuantidade(quantidade);
    return item;
  }

  public static StatusPedidoDTO criarStatusPedidoDTO(StatusPedido status) {
    StatusPedidoDTO dto = new StatusPedidoDTO();
    dto.setStatus(status);
    return dto;
  }
}
