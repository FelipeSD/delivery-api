package com.deliverytech.delivery_api.validations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.deliverytech.delivery_api.common.validations.CEPValidator;

class CEPValidatorTest {

  private CEPValidator validator;

  @BeforeEach
  void setUp() {
    validator = new CEPValidator();
  }

  @Test
  void deveAceitarCEPSemHifen() {
    assertTrue(validator.isValid("12345678", null));
  }

  @Test
  void deveAceitarCEPComHifen() {
    assertTrue(validator.isValid("12345-678", null));
  }

  @Test
  void deveRejeitarCEPInvalido() {
    assertFalse(validator.isValid("1234", null));
    assertFalse(validator.isValid("abcde-678", null));
  }

  @Test
  void deveRejeitarCEPNuloOuVazio() {
    assertFalse(validator.isValid(null, null));
    assertFalse(validator.isValid("", null));
  }
}
