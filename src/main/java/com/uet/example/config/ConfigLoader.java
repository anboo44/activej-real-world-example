package com.uet.example.config;

import io.activej.config.Config;
import io.activej.inject.annotation.Inject;

import java.util.Arrays;
import java.util.List;

public final class ConfigLoader {
    private final static String DELIMITER = ",";

    public final DB   db;
    public final Http http;

    @Inject // Inject is on constructor, It isn't necessary to mark on Class name
    public ConfigLoader(Config config) { // Bean Config is created in DIProviderModule
        this.db   = new DB(config);
        this.http = new Http(config);
    }

    public static final class DB {
        public final String url;
        public final String username;
        public final String password;

        public DB(Config config) {
            this.url      = config.get("db.url");
            this.username = config.get("db.username");
            this.password = config.get("db.password");
        }
    }

    public static final class Http {
        public final List<String> allowedOrigins;

        public Http(Config config) {
            var originStr = config.get("http.allow-origins");

            this.allowedOrigins = Arrays.asList(originStr.split(DELIMITER));
        }
    }
}
