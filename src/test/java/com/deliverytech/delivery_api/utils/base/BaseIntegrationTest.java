package com.deliverytech.delivery_api.utils.base;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.deliverytech.delivery_api.dtos.LoginRequestDTO;
import com.deliverytech.delivery_api.repositories.PedidoRepository;
import com.deliverytech.delivery_api.repositories.ProdutoRepository;
import com.deliverytech.delivery_api.repositories.RestauranteRepository;
import com.deliverytech.delivery_api.repositories.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIntegrationTest {

  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected UsuarioRepository usuarioRepository;
  @Autowired
  protected RestauranteRepository restauranteRepository;
  @Autowired
  protected ProdutoRepository produtoRepository;
  @Autowired
  protected PedidoRepository pedidoRepository;

  @BeforeEach
  void setupBanco() {
    limparBanco();
  }

  protected void limparBanco() {
    // 1. Limpar tabelas dependentes primeiro
    pedidoRepository.deleteAll();
    produtoRepository.deleteAll();

    // 2. Limpar usuários (que podem ter FK para restaurante)
    usuarioRepository.deleteAll();

    // 3. Por último, restaurante
    restauranteRepository.deleteAll();
  }

  protected String loginAndGetToken(String email, String senha) throws Exception {
    var credenciais = new LoginRequestDTO(email, senha);

    var resposta = mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(toJson(credenciais)))
        .andExpect(status().isOk())
        .andReturn();

    JsonNode json = objectMapper.readTree(resposta.getResponse().getContentAsString());
    return json.path("data").path("token").asText();
  }

  protected <T> String toJson(T obj) throws Exception {
    return objectMapper.writeValueAsString(obj);
  }

  protected ResultActions getJson(String url, String token, Map<String, String> params, Object... uriVars)
      throws Exception {

    var requestBuilder = get(url, uriVars)
        .header("Authorization", "Bearer " + token);

    if (params != null) {
      params.forEach(requestBuilder::param);
    }

    return mockMvc.perform(requestBuilder);
  }

  // 🔽 Wrappers com header Authorization automaticamente aplicado
  protected ResultActions getJson(String url, String token, Object... uriVars) throws Exception {
    return getJson(url, token, null, uriVars);
  }

  protected ResultActions postJson(String url, String token, Object body) throws Exception {
    return mockMvc.perform(post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + token)
        .content(toJson(body)));
  }

  protected ResultActions putJson(String url, String token, Object body, Object... uriVars) throws Exception {
    return mockMvc.perform(put(url, uriVars)
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + token)
        .content(toJson(body)));
  }

  protected ResultActions patchJson(String url, String token, Object body, Object... uriVars) throws Exception {
    return mockMvc.perform(patch(url, uriVars)
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + token)
        .content(toJson(body)));
  }

  protected ResultActions deleteJson(String url, String token, Object... uriVars) throws Exception {
    return mockMvc.perform(delete(url, uriVars)
        .header("Authorization", "Bearer " + token));
  }
}
