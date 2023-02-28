package com.uet.example.service;

import io.activej.async.file.AsyncFileService;
import io.activej.async.file.ExecutorAsyncFileService;
import io.activej.bytebuf.ByteBuf;
import io.activej.inject.annotation.Inject;
import io.activej.promise.Promise;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static io.activej.common.Utils.setOf;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.*;
import static java.util.concurrent.Executors.newCachedThreadPool;

@Inject
public final class FileService implements BaseService {

    private static final ExecutorService  executorService = newCachedThreadPool();
    private static final AsyncFileService fileService     = new ExecutorAsyncFileService(executorService);
    private static final Path             PATH;

    static {
        try {
            PATH = Files.createFile(Paths.get("temp.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Promise<Void> writeToFile() {
        try {
            FileChannel channel = FileChannel.open(PATH, Set.of(WRITE, APPEND));

            byte[] message1 = "Hello\n".getBytes();
            byte[] message2 = "This is test file\n".getBytes();
            byte[] message3 = "This is the 3rd line in file".getBytes();

            return fileService.write(channel, 0, message1, 0, message1.length)
                              .then(() -> fileService.write(channel, message1.length, message2, 0, message2.length))
                              .then(() -> fileService.write(channel, message1.length + message2.length, message3, 0, message3.length))
                              .toVoid();
        } catch (IOException e) {
            return Promise.ofException(e);
        }
    }

    public static Promise<ByteBuf> readFromFile() {
        byte[]      array = new byte[1024];
        FileChannel channel;

        try {
            channel = FileChannel.open(PATH, setOf(READ));
        } catch (IOException e) {
            return Promise.ofException(e);
        }

        return fileService.read(channel, 0, array, 0, array.length)
                          .map(bytesRead -> {
                              ByteBuf buf = ByteBuf.wrap(array, 0, bytesRead);
                              System.out.println(buf.getString(UTF_8));
                              return buf;
                          });
    }


}
