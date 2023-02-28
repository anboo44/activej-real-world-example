package com.uet.example.rpc.helper;

import io.activej.async.callback.Callback;
import io.activej.rpc.client.RpcClient;
import io.activej.rpc.client.sender.RpcSender;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public final class RpcSenderStub implements RpcSender {
    private int requests;

    private RpcClient rpcClient;

    private int index;

    public RpcSenderStub(int index) {
        this.index = index;
    }

    public int getRequests() {
        return requests;
    }

    public RpcSenderStub(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public <I, O> void sendRequest(I request, int timeout, @NotNull Callback<O> cb) {
        if (this.rpcClient != null) {
            this.rpcClient.sendRequest(request, cb);
        } else {
            if (request instanceof String str)
                System.out.printf("\nServer fake: %s receive: %s", index, str);
            requests++;
        }
    }
}
