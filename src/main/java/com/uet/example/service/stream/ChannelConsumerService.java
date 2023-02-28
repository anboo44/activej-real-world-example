package com.uet.example.service.stream;

import io.activej.csp.ChannelConsumer;
import io.activej.inject.annotation.Inject;
import io.activej.promise.Promise;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Csp channel consumer
@Inject
public class ChannelConsumerService implements ChannelConsumer<String> {
    @Override
    public @NotNull Promise<Void> accept(@Nullable String s) {
        if (s != null)
            System.out.println("ChannelConsumerService receive: " + s);
        return Promise.complete();
    }

    @Override
    public void closeEx(@NotNull Exception e) { }
}
