package com.uet.example.util.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class EntityNotFoundException extends BaseException {

    private final String message;

    public EntityNotFoundException(String message) {
        super(List.of(message));
        this.message = message;
    }
}
