package com.deliverytech.delivery_api.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> { 
 
    @Override 
    public void initialize(ValidTelefone constraintAnnotation) { 
        // Inicialização se necessária 
    } 
 
    @Override 
    public boolean isValid(String telefone, ConstraintValidatorContext context) { 
        if (telefone == null || telefone.trim().isEmpty()) { 
            return false; 
        } 
 
        // Remove caracteres especiais e espaços 
        String cleanTelefone = telefone.replaceAll("\\D", "");
 
        // Verifica se tem 10 ou 11 dígitos 
        return cleanTelefone.length() == 10 || cleanTelefone.length() == 11; 
    } 
}