package com.uet.example.util;

import com.dslplatform.json.DslJson;

import java.io.IOException;

public class JsonUtils {
    public static <T> String writeToStr(DslJson<?> dslJson, T data) throws IOException {
        var writer = dslJson.newWriter();
        dslJson.serialize(writer, data);
        return writer.toString();
    }
}
