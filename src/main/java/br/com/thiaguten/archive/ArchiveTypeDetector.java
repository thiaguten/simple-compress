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
package br.com.thiaguten.archive;

import org.apache.tika.Tika;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;

import static java.nio.file.Files.isDirectory;

/**
 * Tika file type detector implementation to probe the file content type.
 *
 * @author Thiago Gutenberg Carvalho da Costa
 */
public class ArchiveTypeDetector extends FileTypeDetector {

    private final Tika tika = new Tika();

    @Override
    public String probeContentType(Path path) throws IOException {
        if (isDirectory(path)) {
            return "text/directory";
        }
        return tika.detect(path);
    }
}