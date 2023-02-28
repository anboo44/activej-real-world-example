package com.uet.example.service;

import io.activej.csp.ChannelConsumer;
import io.activej.csp.ChannelSupplier;
import io.activej.inject.annotation.Inject;

@Inject
public final class CSPService implements BaseService {
    public void runBasic() {
        ChannelSupplier<String> supplier = ChannelSupplier.of("1", "2", "3", "4", "5");
        ChannelConsumer<String> consumer = ChannelConsumer.ofConsumer(System.out::println);

        System.out.println("Stream data from supplier to consumer");
        supplier.streamTo(consumer);
    }
}
