package com.deliverytech.delivery_api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.deliverytech.delivery_api.dtos.ClienteDTO;
import com.deliverytech.delivery_api.entities.Cliente;
import com.deliverytech.delivery_api.repositories.ClienteRepository;
import com.deliverytech.delivery_api.repositories.PedidoRepository;
import com.deliverytech.delivery_api.repositories.ProdutoRepository;
import com.deliverytech.delivery_api.repositories.RestauranteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private PedidoRepository pedidoRepository;

  @Autowired
  private RestauranteRepository restauranteRepository;

  @Autowired
  private ProdutoRepository produtoRepository;

  private Cliente clienteAtivo;
  private Cliente clienteInativo;
  private ClienteDTO clienteDTO;

  @BeforeEach
  void setUp() {
    // Limpar dados na ordem correta
    pedidoRepository.deleteAll();
    produtoRepository.deleteAll();
    clienteRepository.deleteAll();
    restauranteRepository.deleteAll();

    // Cliente ativo
    clienteAtivo = new Cliente();
    clienteAtivo.setNome("Maria Oliveira");
    clienteAtivo.setEmail("maria@email.com");
    clienteAtivo.setTelefone("11999999999");
    clienteAtivo.setEndereco("Rua A");
    clienteAtivo.setAtivo(true);
    clienteAtivo = clienteRepository.save(clienteAtivo);

    // Cliente inativo
    clienteInativo = new Cliente();
    clienteInativo.setNome("Carlos Souza");
    clienteInativo.setEmail("carlos@email.com");
    clienteInativo.setTelefone("11888888888");
    clienteInativo.setEndereco("Rua A");
    clienteInativo.setAtivo(false);
    clienteInativo = clienteRepository.save(clienteInativo);

    // DTO base
    clienteDTO = new ClienteDTO();
    clienteDTO.setNome("João Silva");
    clienteDTO.setEmail("joao@email.com");
    clienteDTO.setTelefone("11777777777");
    clienteDTO.setEndereco("Rua A, 123, São Paulo, SP");
  }

  @Test
  void deveCadastrarClienteComSucesso() throws Exception {
    mockMvc.perform(post("/api/clientes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(clienteDTO)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").exists())
        .andExpect(jsonPath("$.data.nome").value("João Silva"))
        .andExpect(jsonPath("$.data.email").value("joao@email.com"))
        .andExpect(jsonPath("$.data.telefone").value("11777777777"))
        .andExpect(jsonPath("$.message").value("Cliente criado com sucesso"));
  }

  @Test
  void deveRejeitarCadastroComDadosInvalidos() throws Exception {
    clienteDTO.setEmail(null); // inválido
    clienteDTO.setNome(""); // inválido

    mockMvc.perform(post("/api/clientes")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(clienteDTO)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
  }

  @Test
  void deveBuscarClientePorIdComSucesso() throws Exception {
    mockMvc.perform(get("/api/clientes/{id}", clienteAtivo.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(clienteAtivo.getId()))
        .andExpect(jsonPath("$.data.nome").value("Maria Oliveira"))
        .andExpect(jsonPath("$.message").value("Cliente encontrado com sucesso"));
  }

  @Test
  void deveRetornar404AoBuscarClienteInexistente() throws Exception {
    mockMvc.perform(get("/api/clientes/{id}", 999L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveListarClientesAtivos() throws Exception {
    mockMvc.perform(get("/api/clientes")
        .param("page", "0")
        .param("size", "10"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].ativo").value(true))
        .andExpect(jsonPath("$.page.number").value(0))
        .andExpect(jsonPath("$.page.totalElements").value(1));
  }

  @Test
  void deveAtualizarClienteComSucesso() throws Exception {
    clienteDTO.setNome("Maria Oliveira Atualizada");

    mockMvc.perform(put("/api/clientes/{id}", clienteAtivo.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(clienteDTO)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.nome").value("Maria Oliveira Atualizada"))
        .andExpect(jsonPath("$.message").value("Cliente atualizado com sucesso"));
  }

  @Test
  void deveRetornar404AoAtualizarClienteInexistente() throws Exception {
    mockMvc.perform(put("/api/clientes/{id}", 999L)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(clienteDTO)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveAtivarOuDesativarClienteComSucesso() throws Exception {
    mockMvc.perform(patch("/api/clientes/{id}/status", clienteInativo.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.ativo").value(true))
        .andExpect(jsonPath("$.message").value("Status do cliente atualizado com sucesso"));
  }

  @Test
  void deveRetornar404AoAtivarDesativarClienteInexistente() throws Exception {
    mockMvc.perform(patch("/api/clientes/{id}/status", 999L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("ENTITY_NOT_FOUND"));
  }

  @Test
  void deveBuscarClientePorEmailComSucesso() throws Exception {
    mockMvc.perform(get("/api/clientes/email/{email}", clienteAtivo.getEmail()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.email").value(clienteAtivo.getEmail()))
        .andExpect(jsonPath("$.message").value("Cliente encontrado com sucesso"));
  }

  @Test
  void deveRetornar404AoBuscarClientePorEmailInexistente() throws Exception {
    mockMvc.perform(get("/api/clientes/email/{email}", "naoexiste@email.com"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error.code").value("ENTITY_NOT_FOUND"));
  }
}
