package com.uet.example.util.exception;

import java.util.List;

public abstract class BaseException extends Exception {
    public List<String> messages;

    public BaseException(List<String> messages) {
        super(String.join(",", messages));
        this.messages = messages;
    }
}
