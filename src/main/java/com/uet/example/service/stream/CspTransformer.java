package com.uet.example.service.stream;

import io.activej.csp.ChannelConsumer;
import io.activej.csp.ChannelInput;
import io.activej.csp.ChannelOutput;
import io.activej.csp.ChannelSupplier;
import io.activej.csp.dsl.WithChannelTransformer;
import io.activej.csp.process.AbstractCommunicatingProcess;
import io.activej.inject.annotation.Inject;

@Inject
public class CspTransformer extends AbstractCommunicatingProcess implements WithChannelTransformer<CspTransformer, String, String> {
    private ChannelSupplier<String> input;
    private ChannelConsumer<String> output;

    @Override
    public ChannelInput<String> getInput() {
        return input -> {
            this.input = input;
            if (this.input != null && this.output != null) startProcess();
            return getProcessCompletion();
        };
    }

    @Override
    public ChannelOutput<String> getOutput() {
        return output -> {
            this.output = output;
            if (this.input != null && this.output != null) startProcess();
        };
    }

    @Override
    protected void doProcess() {
        input.get()
             .whenResult(data -> {
                 if (data == null) {
                     output.acceptEndOfStream()
                           .whenResult(this::completeProcess);
                 } else {
                     data = String.format("raw: %s -> transformed: %s0", data, data);
                     output.accept(data)
                           .whenResult(this::doProcess);
                 }
             })
             .whenException(Throwable::printStackTrace);
    }

    @Override
    protected void doClose(Exception e) {
        System.out.println("Process has been closed with exception: " + e);
        input.closeEx(e);
        output.closeEx(e);
    }
}
