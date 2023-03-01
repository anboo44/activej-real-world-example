package com.uet.example;

import com.uet.example.config.DBMigration;
import com.uet.example.config.DIProviderModule;
import com.uet.example.config.Env;
import com.uet.example.rpc.RPCModuleExample;
import com.uet.example.rpc.helper.MultiRpcServerModule;
import io.activej.config.Config;
import io.activej.inject.Injector;
import io.activej.inject.Key;
import io.activej.inject.annotation.Inject;
import io.activej.inject.binding.Multibinders;
import io.activej.inject.module.Module;
import io.activej.inject.module.ModuleBuilder;
import io.activej.launchers.http.HttpServerLauncher;
import io.activej.worker.WorkerPoolModule;

/**
 * HttpServerLauncher: manages application lifecycle
 * AsyncServlet: receives HttpRequests, creates HttpResponses and sends them back to the client
 */
public class AppLauncher extends HttpServerLauncher {

    private @Inject DBMigration      dbMigration;
    private @Inject RPCModuleExample rpcModuleExample;

    @Override
    protected Module getBusinessLogicModule() {
        return ModuleBuilder.create()
                            .multibind(Key.of(Config.class), Multibinders.ofBinaryOperator(Config::overrideWith))
                            .install(new RPCModuleExample(), new DIProviderModule())
                            .install(WorkerPoolModule.create())
                            .install(MultiRpcServerModule.create())
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
        rpcModuleExample.sayHello();
        rpcModuleExample.run();
        super.run();
    }

    public static void main(String[] args) throws Exception {
        // Speeding up dependency injection
        Injector.useSpecializer();
        var launcher = new AppLauncher();
        launcher.launch(args);
    }
}
