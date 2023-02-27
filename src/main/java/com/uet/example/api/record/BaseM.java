package com.uet.example.api.record;

import java.util.List;

/**
 * BaseM = BaseModel
 * class DTO: all dto classes need to extend this class
 * class VO: all vo classes need to extend this class
 */
public final class BaseM {
    public static abstract class DTO {}
    public static abstract class VO {
        public abstract List<String> validate();
    }
}
