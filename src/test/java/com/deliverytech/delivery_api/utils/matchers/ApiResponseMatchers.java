package com.deliverytech.delivery_api.utils.matchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.test.web.servlet.ResultMatcher;

public class ApiResponseMatchers {

  public static ResultMatcher sucesso() {
    return jsonPath("$.success").value(true);
  }

  public static ResultMatcher erro(String codigo) {
    return jsonPath("$.error.code").value(codigo);
  }

  public static ResultMatcher mensagem(String msg) {
    return jsonPath("$.message").value(msg);
  }
}
