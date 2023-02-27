package com.uet.example.util.exception;

import java.util.List;

public class BusinessException extends BaseException {
    private final String message;

    public BusinessException(String message) {
        super(List.of(message));
        this.message = message;
    }
}
