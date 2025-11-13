package com.deliverytech.delivery_api.common.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HorarioFuncionamentoValidator implements ConstraintValidator<ValidHorarioFuncionamento, String> {

  @Override
  public boolean isValid(String horario, ConstraintValidatorContext context) {
    if (horario == null || horario.isEmpty()) {
      return false;
    }
    // Formato esperado: "HH:mm-HH:mm"
    String regex = "^([01]\\d|2[0-3]):([0-5]\\d)-([01]\\d|2[0-3]):([0-5]\\d)$";
    if (!horario.matches(regex)) {
      return false;
    }

    String[] partes = horario.split("-");
    String abertura = partes[0];
    String fechamento = partes[1];

    return abertura.compareTo(fechamento) < 0;
  }

}
