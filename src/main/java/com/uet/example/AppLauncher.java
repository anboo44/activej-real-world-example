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
import org.eclipse.collections.impl.list.mutable.FastList;

import static java.util.concurrent.Executors.newFixedThreadPool;

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
        // View benchmark: https://www.baeldung.com/jdk-collections-vs-eclipse-collections
        var ec     = newFixedThreadPool(2);
        var numLst = FastList.newListWith(1, 2, 3, 4, 5);
        var r      = numLst.allSatisfy(v -> v > 5);
        var m = numLst.collect(v -> v + 1)
                      .collectIf(v -> v > 1, v -> v + 2);
        var sum = m.asParallel(ec, 2)
                   .collect(v -> v + 10)
                   .sumOfInt(v -> v);
        var temp = m.asParallel(ec, 2)
                    .collect(v -> v + 10)
                    .select(v -> v % 2 == 0)
                    .toList();
        temp.contains(1);

        System.out.println(sum);
        System.out.println(r);
        System.out.println(m.fastListEquals(m.clone()));

        // Speeding up dependency injection
        Injector.useSpecializer();
        var launcher = new AppLauncher();
        launcher.launch(args);
    }
}
