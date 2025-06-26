package com.quatre.phoenix.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {

    public static List<Path> getFilesFromFolder(Path path) throws IOException {
        try (Stream<Path> stream = Files.walk(path)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .toList();
        }
    }
}
