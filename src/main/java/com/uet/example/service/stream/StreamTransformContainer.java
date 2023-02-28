package com.uet.example.service.stream;

import io.activej.datastream.AbstractStreamConsumer;
import io.activej.datastream.AbstractStreamSupplier;
import io.activej.datastream.StreamConsumer;
import io.activej.datastream.StreamSupplier;
import io.activej.datastream.processor.StreamTransformer;
import io.activej.inject.annotation.Inject;

@Inject
public class StreamTransformContainer {

    public Transformer getTransformer() {
        return new Transformer();
    }

    private class Transformer implements StreamTransformer<String, String> {
        private final AbstractStreamConsumer<String> input = new AbstractStreamConsumer<>() {
            @Override
            protected void onEndOfStream() {
                output.sendEndOfStream();
            }
        };

        private final AbstractStreamSupplier<String> output = new AbstractStreamSupplier<>() {
            @Override
            protected void onResumed() {
                input.resume(item -> output.send(String.format("Transformed: %s", item)));
            }

            @Override
            protected void onSuspended() {
                input.suspend();
            }
        };

        @Override
        public StreamConsumer<String> getInput() {
            return input;
        }

        @Override
        public StreamSupplier<String> getOutput() {
            return output;
        }
    }
}
