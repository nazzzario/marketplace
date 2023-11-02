package com.teamchallenge.marketplace.common.validation.phone;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class UkrainianPhoneNumberValidator implements ConstraintValidator<ValidUkrainianPhoneNumber, String> {

    @Override
    public void initialize(ValidUkrainianPhoneNumber constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(phoneNumber)) {
            return true;
        }

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            phoneNumberUtil.parse(phoneNumber, "UA");
            return true;
        } catch (NumberParseException e) {
            return false;
        }
    }
}
