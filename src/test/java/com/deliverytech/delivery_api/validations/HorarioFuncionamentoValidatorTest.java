package com.deliverytech.delivery_api.validations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HorarioFuncionamentoValidatorTest {

  private HorarioFuncionamentoValidator validator;

  @BeforeEach
  void setUp() {
    validator = new HorarioFuncionamentoValidator();
  }

  @Test
  void deveAceitarHorarioValido() {
    assertTrue(validator.isValid("08:00-18:00", null));
    assertTrue(validator.isValid("00:00-23:59", null));
  }

  @Test
  void deveRejeitarFormatoInvalido() {
    assertFalse(validator.isValid("8:00-18:00", null));
    assertFalse(validator.isValid("08:00 até 18:00", null));
  }

  @Test
  void deveRejeitarHorarioInvertido() {
    assertFalse(validator.isValid("18:00-08:00", null));
  }

  @Test
  void deveRejeitarHorarioNuloOuVazio() {
    assertFalse(validator.isValid(null, null));
    assertFalse(validator.isValid("", null));
  }
}
