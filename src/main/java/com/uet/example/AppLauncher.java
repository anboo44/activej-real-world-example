package com.uet.example;

import com.uet.example.config.DBMigration;
import com.uet.example.config.DIProviderModule;
import com.uet.example.config.Env;
import io.activej.config.Config;
import io.activej.inject.Injector;
import io.activej.inject.Key;
import io.activej.inject.annotation.Inject;
import io.activej.inject.binding.Multibinders;
import io.activej.inject.module.Module;
import io.activej.inject.module.ModuleBuilder;
import io.activej.launchers.http.MultithreadedHttpServerLauncher;

/**
 * HttpServerLauncher: manages application lifecycle
 * AsyncServlet: receives HttpRequests, creates HttpResponses and sends them back to the client
 * <p>
 * extends HttpServerLauncher ~> Single core
 * extends MultithreadedHttpServerLauncher ~> multi core
 */
public class AppLauncher extends MultithreadedHttpServerLauncher {

    private @Inject DBMigration dbMigration;

    @Override
    protected Module getBusinessLogicModule() {
        return ModuleBuilder.create()
                            .multibind(Key.of(Config.class), Multibinders.ofBinaryOperator(Config::overrideWith))
                            .install(new DIProviderModule())
                            .build();
    }

    @Override
    protected void onStart() {
        this.logger.info("Execute action before app started");
        dbMigration.migrate();
    }

    @Override
    protected void run() throws Exception {
        this.logger.info("===[ Use profile: {} ]===", Env.get());
        super.run();
    }

    public static void main(String[] args) throws Exception {
        // Speeding up dependency injection
        Injector.useSpecializer();
        var launcher = new AppLauncher();
        launcher.launch(args);
    }
}
