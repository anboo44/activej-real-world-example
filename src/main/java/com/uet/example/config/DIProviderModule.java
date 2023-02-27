package com.uet.example.config;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;
import com.uet.example.api.ServletCenter;
import io.activej.config.Config;
import io.activej.http.AsyncServlet;
import io.activej.http.session.SessionServlet;
import io.activej.http.session.SessionStore;
import io.activej.http.session.SessionStoreInMemory;
import io.activej.inject.annotation.Named;
import io.activej.inject.annotation.Provides;
import io.activej.inject.module.AbstractModule;
import org.hibernate.SessionFactory;

import java.time.Duration;
import java.util.concurrent.Executor;

import static com.uet.example.util.StrConst.SESSION_ID;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * @Provides create an instance for DI
 */
public final class DIProviderModule extends AbstractModule {
    private static final String GENERAL_PROPERTIES_FILE = "app.properties";
    private static final String DEV_PROPERTIES_FILE     = "dev.properties";
    private static final String PROD_PROPERTIES_FILE    = "prod.properties";

    //--------------/ AREA: beans about execution context for async processing /--------------//

    @Provides
    @Named("single")
    private Executor singleExecutor() { return newSingleThreadExecutor(); }

    @Provides
    @Named("database")
    private Executor dbExecutor() { return newFixedThreadPool(5); }

    //--------------/ AREA: beans about routing /--------------//
    @Provides
    private SessionStore<String> sessionStore() { return SessionStoreInMemory.<String>create().withLifetime(Duration.ofDays(30)); }

    @Provides
    AsyncServlet servlet(
        DslJson<?> dslJson,
        RouteConfig routeConfig,
        ConfigLoader configLoader,
        SessionStore<String> sessionStore,
        @Named("single") Executor executor
    ) {
        var publicServlet  = routeConfig.publicServlet(executor);
        var privateServlet = routeConfig.privateServlet();

        var nextServlet = SessionServlet.create(sessionStore, SESSION_ID, publicServlet, privateServlet);
        return new ServletCenter(nextServlet, configLoader, dslJson);
    }

    //--------------/ AREA: common beans /--------------//
    @Provides
    private DslJson<?> dslJson() { return new DslJson<>(Settings.withRuntime()); }

    @Provides
    private Config config() {
        var rootConfig = Config.ofClassPathProperties(GENERAL_PROPERTIES_FILE);

        switch (Env.get()) {
            case Dev:
                var devConfig = Config.ofClassPathProperties(DEV_PROPERTIES_FILE);
                return rootConfig.overrideWith(devConfig);
            case Prod:
                var prodConfig = Config.ofClassPathProperties(PROD_PROPERTIES_FILE);
                rootConfig.overrideWith(prodConfig);

            default:
                return rootConfig;
        }
    }

    //--------------/ AREA: database beans /--------------//
    @Provides
    private SessionFactory sessionFactory(ConfigLoader configLoader) {
        return new DBSourceConfig(configLoader).makeSessionFactory();
    }
}
