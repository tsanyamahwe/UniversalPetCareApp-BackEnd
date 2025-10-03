package com.dailycodework.universalpetcare.exception;

public class PasswordChangeNotAllowedException extends RuntimeException {
    public PasswordChangeNotAllowedException(String message) {
        super(message);
    }
}
