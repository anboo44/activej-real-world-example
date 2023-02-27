package com.uet.example.api.controller;

import com.dslplatform.json.DslJson;
import com.uet.example.api.record.BaseM;
import com.uet.example.util.JsonUtils;
import io.activej.bytebuf.ByteBuf;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.activej.inject.annotation.Inject;
import io.activej.promise.Promise;
import lombok.SneakyThrows;

import java.io.IOException;

abstract class BaseController {
    protected @Inject DslJson<?> dslJson;

    @SneakyThrows
    protected <T> HttpResponse success(T data) {
        var jsonStr = JsonUtils.writeToStr(dslJson, data);
        return HttpResponse.ok200().withJson(jsonStr);
    }

    protected <T> Promise<HttpResponse> asyncSuccess(T data) { return Promise.of(success(data)); }

    protected <T extends BaseM.VO> T parseJson(HttpRequest request, Class<T> clazz) throws IOException {
        ByteBuf body      = request.getBody();
        byte[]  bodyBytes = body.getArray();
        return dslJson.deserialize(clazz, bodyBytes, bodyBytes.length);
    }
}
