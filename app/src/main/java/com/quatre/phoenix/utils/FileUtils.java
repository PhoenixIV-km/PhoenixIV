package com.quatre.phoenix.utils;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

    public static List<Path> getFilesFromFolder(Path path) throws IOException {
        try (Stream<Path> stream = Files.walk(path)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .collect(Collectors.toList());
        }
    }

    // specify T when using
    // final Retryer<List<Element>> loadingRetryer = FileUtils.getRetryer();
    public static <T> Retryer<T> getRetryer() {
        return RetryerBuilder.<T>newBuilder()
                .retryIfExceptionOfType(ExecutionException.class) // retry if Exception thrown
                .retryIfExceptionOfType(InterruptedException.class) // retry if Exception thrown
                .retryIfResult(Objects::isNull) // or retry if result is false
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS)) // wait 1 sec between tries
                .withStopStrategy(StopStrategies.stopAfterAttempt(5)) // max 5 attempts
                .build();
    }
}
