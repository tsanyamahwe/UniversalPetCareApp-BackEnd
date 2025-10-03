package com.dailycodework.universalpetcare.validation;

import com.dailycodework.universalpetcare.utils.PasswordValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    @Autowired
    private PasswordValidator passwordValidator;

    @Override
    public void initialize(ValidPassword constrainAnnotation){}

    @Override
    public  boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext){
        if(passwordValidator == null){
            passwordValidator = new PasswordValidator();
        }
        PasswordValidator.ValidationResult result = passwordValidator.validatePassword(password);
        if(!result.isValid()){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(result.getMessage()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
