package com.deliverytech.delivery_api.common.validations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Payload;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER}) 
@Retention(RetentionPolicy.RUNTIME) 
public @interface ValidCategoria { 
    String message() default "Categoria deve ser uma das opções válidas"; 
    Class<?>[] groups() default {}; 
    Class<? extends Payload>[] payload() default {}; 
} 