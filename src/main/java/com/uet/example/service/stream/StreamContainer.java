package com.uet.example.service.stream;

import io.activej.datastream.AbstractStreamConsumer;
import io.activej.datastream.StreamConsumerToList;
import io.activej.datastream.StreamSupplier;
import io.activej.datastream.processor.StreamFilter;
import io.activej.datastream.processor.StreamUnion;
import io.activej.inject.annotation.Inject;

@Inject
public class StreamContainer {

    public DataStream getConsumer() { return new DataStream(); }

    public static void mapper() {
        //creating a supplier of 10 numbers
        StreamSupplier<Integer> supplier = StreamSupplier.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        //creating a mapper for the numbers
        StreamFilter<Integer, String> simpleMap = StreamFilter.mapper(x -> x + " times ten = " + x * 10);

        //creating a consumer which converts received values to list
        StreamConsumerToList<String> consumer = StreamConsumerToList.create();

        //applying the mapper to supplier and streaming the result to consumer
        supplier.transformWith(simpleMap).streamTo(consumer);

        //when consumer completes receiving values, the result is printed out
        consumer.getResult().whenResult(v -> System.out.println(v));
    }

    private static void union() {
        //creating three suppliers of numbers
        StreamSupplier<Integer> source0 = StreamSupplier.of(1, 2);
        StreamSupplier<Integer> source1 = StreamSupplier.of();
        StreamSupplier<Integer> source2 = StreamSupplier.of(3, 4, 5);

        //creating a unifying transformer
        StreamUnion<Integer> streamUnion = StreamUnion.create();

        //creating a consumer which converts received values to list
        StreamConsumerToList<Integer> consumer = StreamConsumerToList.create();

        //stream the sources into new inputs of the unifier
        source0.streamTo(streamUnion.newInput());
        source1.streamTo(streamUnion.newInput());
        source2.streamTo(streamUnion.newInput());

        //and stream the output of the unifier into the consumer
        streamUnion.getOutput().streamTo(consumer);

        //when consumer completes receiving values, the result is printed out
        consumer.getResult().whenResult(v -> System.out.println(v));
    }

    public static final class DataStream extends AbstractStreamConsumer<String> {
        @Override
        protected void onStarted() {
            resume(x -> System.out.println("StreamConsumer received: " + x));
        }

        @Override
        protected void onEndOfStream() {
            System.out.println("End of stream received");
            acknowledge();
        }

        @Override
        protected void onError(Exception t) {
            System.out.println("Error handling logic must be here. No confirmation to upstream is needed");
        }
    }
}
