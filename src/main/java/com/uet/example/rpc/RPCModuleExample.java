package com.uet.example.rpc;

import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;
import com.uet.example.rpc.helper.RpcClientConnectionPoolStub;
import com.uet.example.rpc.helper.RpcSenderStub;
import io.activej.eventloop.Eventloop;
import io.activej.inject.annotation.Eager;
import io.activej.inject.annotation.Inject;
import io.activej.inject.annotation.Provides;
import io.activej.inject.module.AbstractModule;
import io.activej.promise.Promise;
import io.activej.rpc.client.RpcClient;
import io.activej.rpc.client.sender.RpcSender;
import io.activej.rpc.client.sender.RpcStrategies;
import io.activej.rpc.client.sender.RpcStrategy;
import io.activej.rpc.server.RpcServer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.activej.rpc.client.sender.RpcStrategies.roundRobin;
import static io.activej.rpc.client.sender.RpcStrategies.servers;

/**
 * Error: Possible solutions: 1) Eventloop.create().withCurrentThread() ... {your code block} ... eventloop.run()
 * ~> Reason: Start async process without eventLoop
 * ex:
 * Promise.ofBlocking(newSingleThreadExecutor(), () -> {
 * System.out.println("Say Hello every 5 seconds");
 * });
 * <p>
 * Right:
 * eventloop.delay(5000, () -> Promise.ofBlocking(newSingleThreadExecutor(), () -> {
 * System.out.println("Say Hello every 5 seconds");
 * sayHello();
 * }));
 */

@Slf4j
@Inject
public class RPCModuleExample extends AbstractModule {
    private static final int SERVICE_PORT  = 34765;
    private static final int SERVICE_PORT2 = 34766;

    @Inject
    private RpcClient client;

    @Inject
    private Eventloop eventloop;

    @Inject
    private Scheduler scheduler;

    public void sayHello() {
        eventloop.submit(() -> scheduler.schedule(
            "Long running job monitor",
            () -> log.info("Say Hello every 5 seconds"),           // the runnable to be scheduled
            Schedules.fixedDelaySchedule(Duration.ofSeconds(5)) // the schedule associated to the runnable
        ));
    }

    @Provides
    Scheduler scheduler() { return new Scheduler(); }

    @Eager
    @Provides
    public RpcServer rpcServer(Eventloop eventloop) {
        System.out.println("Start server1");
        return RpcServer.create(eventloop)
//                        .withSerializerBuilder(SerializerBuilder.create()) ~> For send type is custom class
                        .withMessageTypes(String.class)
                        .withHandler(String.class, request -> {
                            System.out.println("server1 receive: " + request);
                            return Promise.of("1: Hello " + request);
                        })
                        .withListenPort(SERVICE_PORT);
    }

    @Provides
    public RpcClient rpcClient(Eventloop eventloop) {
        var lst = RpcStrategies.firstValidResult(
            servers(
                new InetSocketAddress(9000),
                new InetSocketAddress(9001),
                new InetSocketAddress(9002)
            )
        );

        System.out.println("Start client1");
        return RpcClient.create(eventloop)
                        .withMessageTypes(String.class)
                        .withStrategy(lst);
        //.withStrategy(server(new InetSocketAddress(SERVICE_PORT)));
    }

//    @ProvidesIntoSet
//    Initializer<ServiceGraphModuleSettings> configureServiceGraph() {
//        // add logical dependency so that service graph starts client only after it started the server
//        return settings -> settings.addDependency(Key.of(RpcClient.class), Key.of(RpcServer.class));
//    }

    public void run() throws ExecutionException, InterruptedException {
        CompletableFuture<Object> future = eventloop.submit(
            () -> client.sendRequest("World", 1000)
        );
        System.out.printf("%nRPC result: %s %n%n", future.get());
    }

    @SneakyThrows
    public void testRpcStrategy() {
        // create a pool
        RpcClientConnectionPoolStub pool = new RpcClientConnectionPoolStub();

        // create sender to consumer
        RpcSenderStub connection1 = new RpcSenderStub(1); //  <~ Pass rpcClient in real-world
        RpcSenderStub connection2 = new RpcSenderStub(2);

        // create address of consumer
        var address1 = new InetSocketAddress(SERVICE_PORT);
        var address2 = new InetSocketAddress(SERVICE_PORT2);

        // register sender with address of consumer
        pool.put(address1, connection1);
        pool.put(address2, connection2);

        int iterations = 5;

        // Read: There are 3 strategies:
        // 1. roundRobin: split items: one by one to per server
        // 2. firstAvailable: send server which is first available and then get result from it
        // 3. firstValidResult: send all items to per server in list, then merge results and get first valid result
        // Read more in: RpcStrategies.java (lib)

        RpcStrategy strategy = roundRobin(servers(address1, address2));
//         RpcStrategy strategy = RpcStrategies.firstAvailable(servers(address1, address2));
//        RpcStrategy strategy = RpcStrategies.firstValidResult(servers(address1, address2));
        RpcSender sender = strategy.createSender(pool);

        for (int i = 0; i < iterations; i++) {
            sender.sendRequest(String.format("Str: %d", i), 50, (result, ex) -> { // 50: timeout second
                System.out.println("Callback Result: " + result);
            });
        }
    }
}
