package com.uet.example.config;

import io.activej.config.Config;

import java.util.stream.Stream;

public enum Env {
    Local, Dev, Prod;

    // Load variable: `env` from environment
    public static Env get() {
        var systemConfig = Config.ofSystemProperties();
        var envVar       = systemConfig.get("env", "local");
        var envOpt       = Stream.of(Env.values()).filter(e -> e.toString().equalsIgnoreCase(envVar)).findFirst();

        return envOpt.orElse(Local);
    }
}
