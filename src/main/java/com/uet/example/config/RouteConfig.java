package com.uet.example.config;

import com.uet.example.api.controller.HealthController;
import com.uet.example.api.controller.UserController;
import io.activej.http.AsyncServlet;
import io.activej.http.RoutingServlet;
import io.activej.http.StaticServlet;
import io.activej.inject.annotation.Inject;

import java.util.concurrent.Executor;

import static io.activej.http.HttpMethod.GET;
import static io.activej.http.HttpMethod.POST;

@Inject
public final class RouteConfig {
    //------------/ AREA: Injected fields /--------------//
    private @Inject HealthController healthController;
    private @Inject UserController   userController;
    private @Inject ConfigLoader     configLoader;

    //------------/ AREA: Router Setting /--------------//
    public AsyncServlet publicServlet(Executor executor) {
        return RoutingServlet.create()
                             // Using 1 thread for serving UI requests from client
                             .map("/*", StaticServlet.ofClassPath(executor, "static/").withIndexHtml())
                             // HelloController routers
                             .map(GET, withPath("/health-check"), healthController::check)
                             // UserController routers
                             .map(GET, withPath("/login"), userController::login)
                             .map(POST, withPath("/users"), userController::register);
    }

    public AsyncServlet privateServlet() {
        return RoutingServlet.create()
                             // UserController routers
                             .map(GET, withPath("/users"), userController::list)
                             .map(GET, withPath("/users/:userId"), userController::getById);

    }

    //------------/ AREA: Private methods /--------------//
    private String withPath(String path) { return "/api/v1" + path; }
}
