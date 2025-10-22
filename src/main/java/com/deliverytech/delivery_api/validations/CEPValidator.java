package com.deliverytech.delivery_api.validations;

public class CEPValidator implements jakarta.validation.ConstraintValidator<ValidCEP, String> {

  @Override
  public boolean isValid(String cep, jakarta.validation.ConstraintValidatorContext context) {
    if (cep == null || cep.isEmpty()) {
      return false;
    }
    return cep.matches("\\d{8}") || cep.matches("\\d{5}-\\d{3}");
  }

}
