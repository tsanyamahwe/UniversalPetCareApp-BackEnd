package com.dailycodework.universalpetcare.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {
    private final static int MIN_LENGTH = 8;
    private final static Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private final static Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private final static Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private final static Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?~`].*");

    public ValidationResult validatePassword(String password){
        List<String> errors = new ArrayList<>();
        if(password == null || password.isEmpty()){
            return new ValidationResult(false, "Password cannot be empty");
        }
        if(password.length() < MIN_LENGTH){
            errors.add("Password must be at least "+MIN_LENGTH+" characters long");
        }
        if(!UPPERCASE_PATTERN.matcher(password).matches()){
            errors.add("Password must contain at least one uppercase letter (A-Z)");
        }
        if(!LOWERCASE_PATTERN.matcher(password).matches()){
            errors.add("Password must contain at least one lowercase letter (a-z)");
        }
        if(!DIGIT_PATTERN.matcher(password).matches()){
            errors.add("Password must contain at least one numerical number (0-9");
        }
        if(!SPECIAL_CHAR_PATTERN.matcher(password).matches()){
            errors.add("Password must contain at least one special character (!@#$%^&*()_+-=[]{}|;':\\\",./<>?~`)\")");
        }
        if(errors.isEmpty()){
            return new ValidationResult(true, "Password is Valid");
        }else{
            return new ValidationResult(false, String.join(";", errors));
        }
    }

    @Setter
    @Getter
    public static  class ValidationResult{
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message){
            this.valid = valid;
            this.message = message;
        }

    }
}
