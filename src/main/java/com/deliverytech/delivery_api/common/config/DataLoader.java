package com.deliverytech.delivery_api.common.config;

import java.math.BigDecimal;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.deliverytech.delivery_api.auth.model.Role;
import com.deliverytech.delivery_api.auth.model.Usuario;
import com.deliverytech.delivery_api.auth.repository.UsuarioRepository;
import com.deliverytech.delivery_api.pedido.model.Pedido;
import com.deliverytech.delivery_api.pedido.model.PedidoItem;
import com.deliverytech.delivery_api.pedido.model.PedidoStatus;
import com.deliverytech.delivery_api.pedido.repository.PedidoRepository;
import com.deliverytech.delivery_api.produto.model.Produto;
import com.deliverytech.delivery_api.produto.repository.ProdutoRepository;
import com.deliverytech.delivery_api.restaurante.model.Restaurante;
import com.deliverytech.delivery_api.restaurante.repository.RestauranteRepository;

@Component
@Profile({ "dev" })
public class DataLoader implements CommandLineRunner {
  @Autowired
  private RestauranteRepository restauranteRepository;
  @Autowired
  private ProdutoRepository produtoRepository;
  @Autowired
  private PedidoRepository pedidoRepository;
  @Autowired
  private UsuarioRepository usuarioRepository;

  @Override
  public void run(String... args) {
    System.out.println("=== INICIANDO CARGA DE DADOS DE TESTE ===");
    // Limpar dados existentes
    pedidoRepository.deleteAll();
    produtoRepository.deleteAll();
    restauranteRepository.deleteAll();
    usuarioRepository.deleteAll();
    // Inserir dados de teste
    inserirRestaurantes();
    inserirProdutos();
    inserirUsuarios();
    inserirPedidos();
    // Executar testes das consultas
    testarConsultas();
    System.out.println("=== CARGA DE DADOS CONCLUÍDA ===");
  }

  private void inserirUsuarios() {
    System.out.println("--- Inserindo Usuários ---");
    Usuario admin = new Usuario();
    admin.setNome("Admin");
    admin.setEmail("admin@email.com");
    admin.setSenha("$2a$10$FDTTPx1I87OPeOe2NQLoHu4B7u1LHbEB44.JXl4qY9FDNeMCLz0Ri");
    admin.setRole(Role.ADMIN);
    admin.setAtivo(true);
    Usuario entregador = new Usuario();
    entregador.setNome("Entregador");
    entregador.setEmail("entregador@email.com");
    entregador.setSenha("$2a$10$oYD.JpeBKci6lHoCYDU4BujuqUC73gLScKRBf07VkDVw/XQVNzItq");
    entregador.setRole(Role.ENTREGADOR);
    entregador.setAtivo(true);
    Usuario cliente = new Usuario();
    cliente.setNome("Cliente");
    cliente.setEmail("cliente@email.com");
    cliente.setSenha("$2a$10$IIeMYOYnd2fT3TVsEFZRtOsaCAhqtznnc75CwC8n.CFrUeZIgeHTG");
    cliente.setRole(Role.CLIENTE);
    cliente.setAtivo(true);
    Usuario restaurante = new Usuario();
    Restaurante r = new Restaurante();
    r.setId(1L);
    restaurante.setNome("Restaurante");
    restaurante.setEmail("restaurante@email.com");
    restaurante.setSenha("$2a$10$h3I7g7eU9xdmHXkYNcEmeOKUyCH2LShoR68X9a4PMRNQLgo0skrZ6");
    restaurante.setRestaurante(r);
    restaurante.setRole(Role.RESTAURANTE);
    restaurante.setAtivo(true);
    usuarioRepository.saveAll(Arrays.asList(admin, entregador, cliente, restaurante));
    var quantidadeUsuarios = usuarioRepository.count();
    System.out.println("✓ " + quantidadeUsuarios + " usuários inseridos");
  }

  private void inserirRestaurantes() {
    System.out.println("--- Inserindo Restaurantes ---");
    Restaurante pizzaExpress = new Restaurante();
    pizzaExpress.setNome("Pizza Express");
    pizzaExpress.setCategoria("Italiana");
    pizzaExpress.setCnpj("000000000000000000");
    pizzaExpress.setEmail("pizzaexpress@pizzaria.com");
    pizzaExpress.setEndereco("Av. Principal, 100");
    pizzaExpress.setTelefone("1133333333");
    pizzaExpress.setTaxaEntrega(new BigDecimal("3.50"));
    pizzaExpress.setAtivo(true);
    Restaurante burgerKing = new Restaurante();
    burgerKing.setNome("Burger King");
    burgerKing.setCategoria("Fast Food");
    burgerKing.setCnpj("111111111111111111");
    burgerKing.setEmail("burgerking@rest.com");
    burgerKing.setEndereco("Rua Central, 200");
    burgerKing.setTelefone("1144444444");
    burgerKing.setTaxaEntrega(new BigDecimal("5.00"));
    burgerKing.setAvaliacao(4.2);
    burgerKing.setAtivo(true);
    restauranteRepository.saveAll(Arrays.asList(pizzaExpress, burgerKing));
    var quantidadeRestaurantes = restauranteRepository.count();
    System.out.println("✓ " + quantidadeRestaurantes + " restaurantes inseridos");
  }

  private void inserirProdutos() {
    System.out.println("--- Inserindo Produtos ---");
    var restaurantes = restauranteRepository.findAll();
    if (restaurantes.size() < 2) {
      System.out.println("Erro: Não há restaurantes suficientes para inserir produtos.");
      return;
    }
    Restaurante restaurante1 = restaurantes.getFirst();
    Restaurante restaurante2 = restaurantes.get(1);
    var produto1 = new Produto();
    produto1.setNome("Pizza Margherita");
    produto1.setDescricao("Deliciosa pizza com molho de tomate, mussarela e manjericão.");
    produto1.setPreco(new BigDecimal("25.00"));
    produto1.setCategoria("Pizza");
    produto1.setDisponivel(true);
    produto1.setRestaurante(restaurante1);
    var produto2 = new Produto();
    produto2.setNome("Hambúrguer Clássico");
    produto2.setDescricao("Hambúrguer com carne bovina, queijo, alface, tomate e maionese.");
    produto2.setPreco(new BigDecimal("15.00"));
    produto2.setCategoria("Hambúrguer");
    produto2.setDisponivel(true);
    produto2.setRestaurante(restaurante2);
    var produto3 = new Produto();
    produto3.setNome("Batata Frita");
    produto3.setDescricao("Porção de batatas fritas crocantes.");
    produto3.setPreco(new BigDecimal("8.00"));
    produto3.setCategoria("Acompanhamento");
    produto3.setDisponivel(true);
    produto3.setRestaurante(restaurante2);
    produtoRepository.saveAll(Arrays.asList(produto1, produto2, produto3));
    var quantidadeProdutos = produtoRepository.count();
    System.out.println("✓ " + quantidadeProdutos + " produtos inseridos");
  }

  private void inserirPedidos() {
    System.out.println("--- Inserindo Pedidos ---");
    var usuarios = usuarioRepository.findAll();
    var produtos = produtoRepository.findAll();
    var restaurantes = restauranteRepository.findAll();
    if (usuarios.isEmpty() || produtos.isEmpty() || restaurantes.isEmpty()) {
      System.out.println("Erro: Dados insuficientes para inserir pedidos.");
      return;
    }
    var pedido1 = new Pedido();

    var item1 = new PedidoItem();
    item1.setProduto(produtos.getFirst());
    item1.setQuantidade(1);
    item1.setPrecoUnitario(produtos.getFirst().getPreco());
    item1.setPedido(pedido1);
    var item2 = new PedidoItem();
    item2.setProduto(produtos.get(1));
    item2.setQuantidade(2);
    item2.setPrecoUnitario(produtos.get(1).getPreco());
    item2.setPedido(pedido1);

    pedido1.setUsuario(usuarios.getFirst());
    pedido1.setRestaurante(restaurantes.get(1));
    pedido1.setEnderecoEntrega(usuarios.getFirst().getEndereco());
    pedido1.setStatus(PedidoStatus.CONFIRMADO);
    pedido1.setSubtotal(item1.getPrecoUnitario().add(item2.getPrecoUnitario()));
    pedido1.setTaxaEntrega(restaurantes.getFirst().getTaxaEntrega());
    pedido1.setValorTotal(pedido1.getSubtotal().add(pedido1.getTaxaEntrega()));
    pedido1.setObservacoes("Por favor, entregar rápido.");
    pedido1.adicionarItem(item1);
    pedido1.adicionarItem(item2);
    pedidoRepository.save(pedido1);

    var quantidadePedidos = pedidoRepository.count();
    System.out.println("✓ " + quantidadePedidos + " pedidos inseridos");
  }

  private void testarConsultas() {
    System.out.println("\n=== TESTANDO CONSULTAS DOS REPOSITORIES ===");
    // Teste RestauranteRepository
    System.out.println("\n--- Testes RestauranteRepository ---");
    var restaurantePorNome = restauranteRepository.findByNome("Pizza Express");
    System.out.println("Restaurante por nome: " +
        (restaurantePorNome.isPresent() ? restaurantePorNome.get().getNome() : "Não encontrado"));
    var restaurantesAtivos = restauranteRepository.findByAtivoTrue(Pageable.unpaged());
    System.out.println("Restaurantes ativos: " + restaurantesAtivos.getSize());
    var restaurantesPorCategoria = restauranteRepository.findByCategoriaAndAtivoTrue("Fast Food", Pageable.unpaged());
    System.out.println("Restaurantes na categoria 'Fast Food': " + restaurantesPorCategoria.getSize());
    var restaurantesPorAvaliacao = restauranteRepository
        .findByAvaliacaoGreaterThanEqualAndAtivoTrue(new BigDecimal("4.0"), Pageable.unpaged());
    System.out.println("Restaurantes com avaliação >= 4.0: " + restaurantesPorAvaliacao.getSize());

    var top5Restaurantes = restauranteRepository.findTop5ByOrderByNomeAsc(Pageable.unpaged());
    System.out.println("Top 5 restaurantes por nome:");
    top5Restaurantes.forEach(r -> System.out.println(" - " + r.getNome()));

    // Teste ProdutoRepository
    System.out.println("\n--- Testes ProdutoRepository ---");
    var produtosPorNome = produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue("pizza",
        Pageable.unpaged());
    System.out.println("Produtos com 'pizza' no nome: " + produtosPorNome.getSize());
    var produtosPorCategoria = produtoRepository.findByCategoriaAndDisponivelTrue("Hambúrguer", Pageable.unpaged());
    System.out.println("Produtos na categoria 'Hambúrguer': " + produtosPorCategoria.getSize());
    // Teste PedidoRepository
    System.out.println("\n--- Testes PedidoRepository ---");
    var pedidosPorUsuario = pedidoRepository.findByUsuarioIdOrderByDataPedidoDesc(1L, Pageable.unpaged());
    System.out.println("Pedidos do cliente ID 1: " + pedidosPorUsuario.getSize());
    var pedidosPendentes = pedidoRepository.findPedidosPendentes(Pageable.unpaged());
    System.out.println("Pedidos pendentes: " + pedidosPendentes.getSize());
    var vendasPorRestaurante = pedidoRepository.calcularTotalVendasPorRestaurante(Pageable.unpaged());
    System.out.println("Total de vendas por restaurante:");
    vendasPorRestaurante.forEach(v -> System.out.println(" - " + v[0] + ": R$ " + v[1]));

    // Relatório de vendas por restaurante
    var relatorioVendas = restauranteRepository.relatorioVendasPorRestaurante();
    System.out.println("\nRelatório de Vendas por Restaurante:");
    relatorioVendas.forEach(r -> System.out.println(" - " + r.getNomeRestaurante() +
        ": R$ " + r.getTotalVendas() + " em " + r.getQuantidadePedidos() + " pedidos"));

    // Produtos mais vendidos
    var produtosMaisVendidos = produtoRepository.produtosMaisVendidos();
    System.out.println("\nProdutos mais vendidos:");
    produtosMaisVendidos.forEach(p -> System.out.println(" - " + p[0] + ": " + p[1] + " vendidos"));
  }
}
