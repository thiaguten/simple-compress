package br.com.thiaguten.archive.utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.nio.file.Files.*;

/**
 * File Utility class
 *
 * @author Thiago Gutenberg Carvalho da Costa
 */
public final class FileUtils {

    public static void deleteNotEmptyFolder(Path dir) throws IOException {
        deleteFolder(dir, true);
    }

    public static void deleteFolder(Path path, boolean bypassNotEmptyDirectory) throws IOException {
        if (isDirectory(path)) {
            if (bypassNotEmptyDirectory) {
                List<Path> children = listChildren(path);
                for (Path child : children) {
                    if (isDirectory(child)) {
                        deleteFolder(child, bypassNotEmptyDirectory);
                    } else {
                        deleteIfExists(child);
                    }
                }
            }
            deleteIfExists(path);
        }
    }

    public static List<Path> listChildren(Path path) throws IOException {
        List<Path> children = new ArrayList<>();
        if (isDirectory(path)) {
            try (DirectoryStream<Path> childrenStream = newDirectoryStream(path)) {
                for (Path child : childrenStream) {
                    children.add(child);
                }
            }
        }
        return Collections.unmodifiableList(children);
    }

    public static Path removeExtension(Path file) {
        if (!isRegularFile(file)) {
            String absPath = file.toAbsolutePath().toString();
            int index = absPath.lastIndexOf('.');
            if (index > 0) {
                file = Paths.get(absPath.substring(0, index));
            }
        }
        return file;
    }

}
