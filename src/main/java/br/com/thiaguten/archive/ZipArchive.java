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
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

import static java.nio.file.Files.*;

/**
 * Zip Archive Implementation.
 *
 * @author Thiago Gutenberg Carvalho da Costa
 */
public class ZipArchive extends AbstractArchive implements Archive {

    @Override
    public String getName() {
        return "ZipArchive";
    }

    @Override
    public String getMimeType() {
        return "application/zip";
    }

    @Override
    public String getExtension() {
        return ".zip";
    }

    @Override
    protected ArchiveEntry createArchiveEntry(String path, long size, byte[] content) {
        ZipArchiveEntry zipEntry = new ZipArchiveEntry(path);
        zipEntry.setSize(size);
        return zipEntry;
    }

    @Override
    protected ArchiveOutputStream createArchiveOutputStream(OutputStream outputStream) {
        return new ZipArchiveOutputStream(outputStream);
    }

    protected ArchiveOutputStream createArchiveOutputStream(Path path) throws IOException {
        // for some internal optimizations should use
        // the constructor that accepts a File argument
        return new ZipArchiveOutputStream(path.toFile());
    }

    @Override
    protected ArchiveInputStream createArchiveInputStream(InputStream inputStream) throws IOException {
        return new ZipArchiveInputStream(inputStream);
    }

    /**
     * Override to make use of the ZipArchive#createArchiveOutputStream(Path path) method
     * instead of the method ZipArchive#createArchiveOutputStream(BufferedOutputStream bufferedOutputStream).
     */
    @Override
    public Path compress(Path... paths) throws IOException {
        Path compress = null;
        ArchiveOutputStream archiveOutputStream = null;

        for (Path path : paths) {
            // get path infos
            final Path parent = path.getParent();
            final String name = path.getFileName().toString();
            final boolean isDirectory = isDirectory(path);

            if (compress == null) {
                // create compress file
                String compressName = (paths.length == 1 ? name : getName());
                compress = Paths.get(parent.toString(), compressName + getExtension());

                // creates a new compress file to not override if already exists
                // if you do not want this behavior, just comment this line
                compress = createFile(ArchiveAction.COMPRESS, parent, compress);

                // open compress file stream
                archiveOutputStream = createArchiveOutputStream(compress);

                logger.debug("creating the archive file " + compressName);
            }

            logger.debug("reading path " + path);

            if (isDirectory) {
                compressDirectory(parent, path, archiveOutputStream);
            } else {
                compressFile(parent, path, archiveOutputStream);
            }
        }

        // closing streams
        if (archiveOutputStream != null) {
            archiveOutputStream.finish();
            archiveOutputStream.close();
        }

        logger.debug("finishing the archive file " + compress);

        return compress;
    }

    /**
     * Override to make use of the ZipFile class instead of the ZipArchiveInputStream class.
     * https://commons.apache.org/proper/commons-compress/zip.html
     */
    @Override
    public Path decompress(Path path) throws IOException {
        Path decompressDir = removeExtension(path);

        logger.debug("reading archive file " + path);

        try (ZipFile zipFile = new ZipFile(path.toString())) {

            // creates a new decompress folder to not override if already exists
            // if you do not want this behavior, just comment this line
            decompressDir = createFile(ArchiveAction.DECOMPRESS, decompressDir.getParent(), decompressDir);

            createDirectories(decompressDir);

            logger.debug("creating the decompress destination directory " + decompressDir);

            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                final ZipArchiveEntry zipArchiveEntry = entries.nextElement();

                if (zipFile.canReadEntryData(zipArchiveEntry)) {
                    final String entryName = zipArchiveEntry.getName();
                    final InputStream archiveInputStream = zipFile.getInputStream(zipArchiveEntry);
                    final Path target = Paths.get(decompressDir.toString(), entryName);
                    final Path parent = target.getParent();

                    if (parent != null && !exists(parent)) {
                        createDirectories(parent);
                    }

                    logger.debug("reading compressed path " + entryName);

                    if (!zipArchiveEntry.isDirectory()) {
                        try (OutputStream outputStream = new BufferedOutputStream(newOutputStream(target))) {

                            logger.debug("writting compressed " + entryName + " file in the decompress directory");

//                            byte[] content = new byte[(int) zipArchiveEntry.getSize()];
//                            outputStream.write(content);
                            IOUtils.copy(archiveInputStream, outputStream);
                        }
                    }
                }
            }

            logger.debug("finishing the decompress in the directory: " + decompressDir);

        }

        return decompressDir;
    }
}
