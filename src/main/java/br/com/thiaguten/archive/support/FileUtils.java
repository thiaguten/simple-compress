/*-
 * #%L
 * simple-compress
 * %%
 * Copyright (C) 2016 Thiago Gutenberg Carvalho da Costa
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package br.com.thiaguten.archive.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class to handle operations involving files.
 *
 * @author Thiago Gutenberg Carvalho da Costa
 */
public final class FileUtils {

    private FileUtils() {
        throw new AssertionError();
    }

    public static void deleteDirectory(final Path dir) throws IOException {
        deleteDirectory(dir, false);
    }

    public static void deleteNotEmptyDirectory(final Path dir) throws IOException {
        deleteDirectory(dir, true);
    }

    private static void deleteDirectory(final Path path, final boolean bypassNotEmptyDirectory) throws IOException {
        if (bypassNotEmptyDirectory) {
            List<Path> children = listChildren(path);
            for (Path child : children) {
                if (isDirectory(child)) {
                    deleteDirectory(child, bypassNotEmptyDirectory);
                } else {
                    Files.deleteIfExists(child);
                }
            }
        }
        Files.deleteIfExists(path);
    }

    public static List<Path> listChildren(final Path path) throws IOException {
        final List<Path> children = new ArrayList<>();
        if (isDirectory(path)) {
            try (DirectoryStream<Path> childrenStream = Files.newDirectoryStream(path)) {
                for (Path child : childrenStream) {
                    children.add(child);
                }
            }
        }
        Collections.sort(children);
        return Collections.unmodifiableList(children);
//        return Collections.unmodifiableList(new ArrayList<>(children));
    }

    public static String probeContentType(Path path) throws IOException {
        // An implementation using "Java Service Provider Interface (SPI)" is
        // registered in /META-INF/services/java.nio.file.spi.TikaFileTypeDetector,
        // improving the standard default NIO implementation with the Apache Tika API.
        return Files.probeContentType(path);
    }

    public static boolean exists(final Path path) {
        return Files.exists(path);
    }

    public static Path createFile(final Path path) throws IOException {
        return Files.createFile(path);
    }

    public static Path createDirectories(final Path path) throws IOException {
        return Files.createDirectories(path);
    }

    public static boolean isDirectory(final Path path) {
        return Files.isDirectory(path);
    }

    public static Path removeExtension(Path file) {
        String str = file.toString();
        int index = str.lastIndexOf('.');
        if (index > 0) {
            file = Paths.get(str.substring(0, index));
        }
        return file;
    }

    /**
     * Inner class to browse the directory tree summarizing the size of each of them.
     *
     * @author Thiago Gutenberg Carvalho da Costa
     */
    private static class FileTreeSize extends SimpleFileVisitor<Path> {

        private long totalSize;

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
            totalSize += attrs.size();
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            totalSize += attrs.size();
            return FileVisitResult.CONTINUE;
        }

        public long getTotalSize() {
            return totalSize;
        }
    }

    public static long size(final Path path) throws IOException {
        return size(path, false);
    }

    public static long totalSize(final Path path) throws IOException {
        return size(path, true);
    }

    private static long size(final Path path, final boolean recursive) throws IOException {
        if (isDirectory(path) && recursive) {
            FileTreeSize fileTreeSize = new FileTreeSize();
            Files.walkFileTree(path, fileTreeSize);
            return fileTreeSize.getTotalSize();
        }
        return Files.size(path);
    }

    public static OutputStream newOutputStream(final Path path) throws IOException {
        return Files.newOutputStream(path);
    }

    public static InputStream newInputStream(final Path path) throws IOException {
        return Files.newInputStream(path);
    }
}
