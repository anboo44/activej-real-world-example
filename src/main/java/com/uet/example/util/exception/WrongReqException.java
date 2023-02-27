package com.uet.example.util.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class WrongReqException extends BaseException {
    private final List<String> messages;

    public WrongReqException(List<String> messages) {
        super(messages);
        this.messages = messages;
    }

    public WrongReqException(String message) {
        super(List.of(message));
        this.messages = List.of(message);
    }
}
