package com.deliverytech.delivery_api.validations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.deliverytech.delivery_api.common.validations.TelefoneValidator;

class TelefoneValidatorTest {

  private TelefoneValidator validator;

  @BeforeEach
  void setUp() {
    validator = new TelefoneValidator();
  }

  @Test
  void deveAceitarTelefoneCom10ou11Digitos() {
    assertTrue(validator.isValid("11987654321", null)); // celular
    assertTrue(validator.isValid("(11)98765-4321", null)); // com símbolos
    assertTrue(validator.isValid("1132654321", null)); // fixo
  }

  @Test
  void deveRejeitarTelefoneComMenosDe10Digitos() {
    assertFalse(validator.isValid("123456789", null));
  }

  @Test
  void deveRejeitarTelefoneComMaisDe11Digitos() {
    assertFalse(validator.isValid("123456789012", null));
  }

  @Test
  void deveRejeitarTelefoneNuloOuVazio() {
    assertFalse(validator.isValid(null, null));
    assertFalse(validator.isValid("", null));
  }
}
