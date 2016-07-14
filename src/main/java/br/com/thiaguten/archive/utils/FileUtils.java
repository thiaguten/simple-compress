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

    public static void deleteNotEmptyFolder(final Path dir) throws IOException {
        deleteFolder(dir, true);
    }

    public static void deleteFolder(final Path path, final boolean bypassNotEmptyDirectory) throws IOException {
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

    public static List<Path> listChildren(final Path path) throws IOException {
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
