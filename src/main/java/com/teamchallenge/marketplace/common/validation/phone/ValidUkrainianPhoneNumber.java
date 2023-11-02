package com.teamchallenge.marketplace.common.validation.phone;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UkrainianPhoneNumberValidator.class)
public @interface ValidUkrainianPhoneNumber {
    String message() default "Invalid Ukrainian phone number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
