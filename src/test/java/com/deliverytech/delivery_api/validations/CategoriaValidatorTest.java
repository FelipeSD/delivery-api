package com.deliverytech.delivery_api.validations;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CategoriaValidatorTest {

  private CategoriaValidator validator;

  @BeforeEach
  void setUp() {
    validator = new CategoriaValidator();
  }

  @Test
  void deveAceitarCategoriaValida() {
    assertTrue(validator.isValid("brasileira", null));
    assertTrue(validator.isValid("JAPONESA", null));
  }

  @Test
  void deveRejeitarCategoriaInvalida() {
    assertFalse(validator.isValid("coreana", null));
    assertFalse(validator.isValid("qualquer", null));
  }

  @Test
  void deveRejeitarCategoriaNulaOuVazia() {
    assertFalse(validator.isValid(null, null));
    assertFalse(validator.isValid(" ", null));
  }
}
