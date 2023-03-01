package com.uet.example.rpc.helper;

import io.activej.eventloop.Eventloop;
import io.activej.inject.annotation.Eager;
import io.activej.inject.annotation.Inject;
import io.activej.inject.annotation.Provides;
import io.activej.inject.module.AbstractModule;
import io.activej.promise.Promise;
import io.activej.rpc.server.RpcServer;
import io.activej.worker.WorkerPool;
import io.activej.worker.WorkerPools;
import io.activej.worker.annotation.Worker;
import io.activej.worker.annotation.WorkerId;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

// Note about workers: https://activej.io/boot/workers
// 1. In AppLauncher, add `WorkerPoolModule.create()` to UsedModule
// 2. Create bean `WorkerPool` with size
// 3. Create eventLoop for every worker in pool
// p/s: Mark `@Worker` to use workerPool
// More: Step 2 + 3 ~> Create every independent eventLoop for every worker.

@Slf4j
public class MultiRpcServerModule extends AbstractModule {
    private MultiRpcServerModule() { }

    public static MultiRpcServerModule create() {
        return new MultiRpcServerModule();
    }

    @Inject
    WorkerPool.Instances<RpcServer> instances;

    @Eager
    @Provides
    WorkerPool workerPool(WorkerPools workerPools) {
        return workerPools.createPool(3);
    }

    @Eager
    @Provides
    @Worker
    Eventloop eventloop() { return Eventloop.create(); }

    @Eager
    @Provides
    @Worker
    InetSocketAddress port(@WorkerId int workerId) {
        return new InetSocketAddress("localhost", 9000 + workerId);
    }

    @Eager
    @Provides
    @Worker
    RpcServer server(@WorkerId int workerId, Eventloop eventloop, InetSocketAddress address) { // In every eventLoop, app will find and inject bean
        log.info(String.format("[Worker %d] Start server", workerId));

        return RpcServer.create(eventloop)
//                        .withSerializerBuilder(SerializerBuilder.create())  ~> For serialize custom class
                        .withMessageTypes(String.class)
                        .withHandler(String.class, request -> {
                            String message = String.format("[Worker %d] server at port %d receive: %s", workerId, address.getPort(), request);
                            System.out.println(message);
                            return Promise.of(String.format("[Worker %d]] server: %d ~> Hello %s", workerId, address.getPort(), request));
                        })
                        .withListenAddresses(address);
    }
}
