package com.dyrmig.banking.classes;

public class MyCustomException extends RuntimeException {
    public MyCustomException(String message) {
        super(message);
    }
}