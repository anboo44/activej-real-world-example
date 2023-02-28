package com.uet.example.api;

import com.dslplatform.json.DslJson;
import com.uet.example.api.record.dto.ErrorDTO;
import com.uet.example.config.ConfigLoader;
import com.uet.example.util.JsonUtils;
import com.uet.example.util.StrConst;
import com.uet.example.util.exception.EntityNotFoundException;
import com.uet.example.util.exception.WrongReqException;
import io.activej.http.*;
import io.activej.promise.Promisable;
import io.activej.promise.Promise;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class ServletCenter implements AsyncServlet {

    private final AsyncServlet nextServlet;
    private final ConfigLoader configLoader;
    private final DslJson<?>   dslJson;

    @Override
    public @NotNull Promisable<HttpResponse> serve(@NotNull HttpRequest request) {
        var isNotValidOrigin = !isValidOrigin(request);
        if (isNotValidOrigin) { return HttpResponse.ofCode(403).withPlainText("Invalid origin"); }

        try {
            return this.nextServlet.serveAsync(request)
                                   .map(res -> res.withHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"))
                                   .then(Promise::of, this::handleException);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    // reqOrigin = null ~> call api directly without origin
    private boolean isValidOrigin(HttpRequest request) {
        var reqOrigin = request.getHeader(HttpHeaders.ORIGIN);

        return reqOrigin == null ||
               configLoader.http.allowedOrigins.isEmpty() ||
               configLoader.http.allowedOrigins.contains(reqOrigin);
    }

    @SneakyThrows
    private Promise<HttpResponse> handleException(Exception e) {
        e.printStackTrace();

        var isBadRequestException = e instanceof IOException || e instanceof NumberFormatException;

        if (isBadRequestException) {
            var errorDTO = new ErrorDTO(List.of(StrConst.Error.BAD_REQUEST));
            var jsonStr  = JsonUtils.writeToStr(dslJson, errorDTO);
            return Promise.of(HttpResponse.ofCode(400).withJson(jsonStr));
        }

        if (e instanceof WrongReqException ex) {
            var errorDTO = new ErrorDTO(ex.getMessages());
            var jsonStr  = JsonUtils.writeToStr(dslJson, errorDTO);
            return Promise.of(HttpResponse.ofCode(400).withJson(jsonStr));
        }

        if (e instanceof EntityNotFoundException ex) {
            var errorDTO = new ErrorDTO(List.of(ex.getMessage()));
            var jsonStr  = JsonUtils.writeToStr(dslJson, errorDTO);
            return Promise.of(HttpResponse.ofCode(404).withJson(jsonStr));
        }

        if (e instanceof HttpError ex && ex.getCode() == 404) {
            var errorDTO = new ErrorDTO(List.of(StrConst.Error.RESOURCE_NOT_FOUND));
            var jsonStr  = JsonUtils.writeToStr(dslJson, errorDTO);
            return Promise.of(HttpResponse.ofCode(404).withJson(jsonStr));
        }

        log.error("[ERROR] Message -> " + e.getMessage());
        var errorDTO = new ErrorDTO(List.of(StrConst.Error.UNKNOWN));
        var jsonStr  = JsonUtils.writeToStr(dslJson, errorDTO);
        return Promise.of(HttpResponse.ofCode(500).withJson(jsonStr));
    }
}
