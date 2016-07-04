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

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * Tar Archive Implementation.
 *
 * @author Thiago Gutenberg Carvalho da Costa
 */
public class TarArchive extends AbstractArchive implements Archive {

    @Override
    public String getMimeType() {
        return "application/x-tar";
    }

    @Override
    public String getExtension() {
        return ".tar";
    }

    @Override
    protected ArchiveEntry createArchiveEntry(String targetPath, long targetSize, byte[] targetBytes) {
        TarArchiveEntry tarEntry = new TarArchiveEntry(targetPath);
        tarEntry.setSize(targetSize);
        return tarEntry;
    }

    @Override
    protected ArchiveOutputStream createArchiveOutputStream(BufferedOutputStream bufferedOutputStream) throws IOException {
        return new TarArchiveOutputStream(bufferedOutputStream);
    }

    @Override
    protected ArchiveInputStream createArchiveInputStream(BufferedInputStream bufferedInputStream) throws IOException {
        return new TarArchiveInputStream(bufferedInputStream);
    }

}
