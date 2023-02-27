package com.uet.example.util.model;

import java.io.Serializable;

public interface Identifier<E> extends Serializable {
    long serialVersionUID = 1L;

    Integer getValue();
}
