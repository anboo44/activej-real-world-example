package com.uet.example.api.controller;

import com.uet.example.rpc.RPCModuleExample;
import com.uet.example.service.FileService;
import com.uet.example.service.stream.ChannelConsumerService;
import com.uet.example.service.stream.CspTransformer;
import com.uet.example.service.stream.StreamContainer;
import com.uet.example.service.stream.StreamTransformContainer;
import io.activej.async.function.AsyncConsumer;
import io.activej.csp.ChannelConsumer;
import io.activej.csp.ChannelSupplier;
import io.activej.csp.process.ChannelSplitter;
import io.activej.datastream.StreamConsumerToList;
import io.activej.datastream.StreamSupplier;
import io.activej.datastream.processor.StreamSplitter;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.activej.inject.annotation.Inject;
import io.activej.promise.Promise;
import io.activej.promise.Promises;

import java.util.function.ToIntFunction;

@Inject
public class AsyncSampleController extends BaseController {
    private @Inject ChannelConsumerService   consumerService;
    private @Inject CspTransformer           cspTransformer;
    private @Inject StreamContainer          streamContainer;
    private @Inject StreamTransformContainer sTransContainer;
    private @Inject RPCModuleExample         rpcModuleExample;

    // Read more: https://activej.io/async-io/csp
    public HttpResponse testCSP(HttpRequest request) {
        // Run Example for CSP(Communicating Sequential Process)
        ChannelSupplier<String> supplier = ChannelSupplier.of("1", "2", "3", "4", "5");
        ChannelSplitter<String> splitter = ChannelSplitter.create(supplier);
        ChannelConsumer<String> localConsumer = ChannelConsumer.of(
            AsyncConsumer.of(v -> System.out.println("Local Consumer: " + v))
        );

        // Splitter will send data to 2 consumers
        splitter.addOutput().set(localConsumer);
        splitter.addOutput().getSupplier().transformWith(cspTransformer).streamTo(consumerService);

        return success("DONE");
    }

    // Read more: https://activej.io/async-io/promise
    public Promise<HttpResponse> testAsyncFile(HttpRequest request) {
        // This api is for testing async file service
        return Promises.sequence(
            () -> FileService.writeToFile(),
            () -> FileService.readFromFile().toVoid()
        ).map(v -> HttpResponse.ofCode(201));
    }

    // Read more: https://activej.io/async-io/datastream
    public HttpResponse testDataStream(HttpRequest request) {
        //creating a sharder of three parts for three consumers
        ToIntFunction<Object> hashSharder = item -> (item.hashCode() & Integer.MAX_VALUE) % 2; // ~> Strategy
        StreamSplitter<String, String> sharder = StreamSplitter.create(
            (item, acceptors) -> acceptors[hashSharder.applyAsInt(item)].accept(item)
            //  (item, acceptors) -> Stream.of(acceptors).forEach(ac -> ac.accept(item)) ~> Send item to all consumer
        );
        StreamSupplier<String>       supplier = StreamSupplier.of("0", "1", "2", "3", "4");
        StreamConsumerToList<String> first    = StreamConsumerToList.create();

        // Set consumer to splitter
        sharder.newOutput().streamTo(first);
        sharder.newOutput().streamTo(streamContainer.getConsumer());

        supplier.streamTo(sharder.getInput());

        StreamSupplier.of("Welcome to Data Stream")
                      .transformWith(sTransContainer.getTransformer())
                      .streamTo(streamContainer.getConsumer());

        first.getResult().whenResult(result -> System.out.println("First Consumer received: " + result));
        return success("DONE");
    }

    public HttpResponse testRpcRoundRobin(HttpRequest request) {
        rpcModuleExample.testRpcStrategy();
        return success("DONE");
    }
}
