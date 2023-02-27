package com.uet.example.api.controller;

import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.activej.inject.annotation.Inject;

@Inject
public final class HealthController extends BaseController {

    public HttpResponse check(HttpRequest request) {
        return success("OK");
    }
}
