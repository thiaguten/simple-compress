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
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static br.com.thiaguten.archive.support.FileUtils.*;

/**
 * Abstract Archive defines some archive behaviors and this class has some
 * convenient methods. This class must be extended by concrete archive
 * implementations.
 *
 * @author Thiago Gutenberg Carvalho da Costa
 */
public abstract class AbstractArchive implements Archive {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private AtomicInteger count = new AtomicInteger(1);

    protected abstract ArchiveEntry createArchiveEntry(String path, long size, byte[] content);

    protected abstract ArchiveInputStream createArchiveInputStream(BufferedInputStream bufferedInputStream) throws IOException;

    protected abstract ArchiveOutputStream createArchiveOutputStream(BufferedOutputStream bufferedOutputStream) throws IOException;

    /**
     * Archive actions
     */
    public enum ArchiverAction {
        COMPRESS, DECOMPRESS
    }

    @Override
    public String getName() {
        return "Archive";
    }

    /**
     * Generic compress implementation
     */
    @Override
    public Path compress(Path... paths) throws IOException {
        Path compress = null;
        OutputStream outputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
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
                compress = createFile(ArchiverAction.COMPRESS, parent, compress);

                // open compress file stream
                outputStream = newOutputStream(compress);
                bufferedOutputStream = new BufferedOutputStream(outputStream);
                archiveOutputStream = createArchiveOutputStream(bufferedOutputStream);

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
        archiveOutputStream.finish();
        archiveOutputStream.close();
        bufferedOutputStream.close();
        outputStream.close();

        logger.debug("finishing the archive file: " + compress);

        return compress;
    }

    /**
     * Generic decompress implemetation
     */
    @Override
    public Path decompress(Path path) throws IOException {
        Path decompressDir = removeExtension(path);

        logger.debug("reading archive file " + path);

        try (ArchiveInputStream archiveInputStream = createArchiveInputStream(new BufferedInputStream(newInputStream(path)))) {

            // creates a new decompress folder to not override if already exists
            // if you do not want this behavior, just comment this line
            decompressDir = createFile(ArchiverAction.DECOMPRESS, decompressDir.getParent(), decompressDir);

            createDirectories(decompressDir);

            logger.debug("creating the decompress destination directory " + decompressDir);

            ArchiveEntry entry;
            while ((entry = archiveInputStream.getNextEntry()) != null) {
                if (archiveInputStream.canReadEntryData(entry)) {

                    final String entryName = entry.getName();
                    final Path target = Paths.get(decompressDir.toString(), entryName);
                    final Path parent = target.getParent();

                    if (parent != null && !exists(parent)) {
                        createDirectories(parent);
                    }

                    logger.debug("reading compressed path " + entryName);

                    if (!entry.isDirectory()) {
                        try (OutputStream outputStream = new BufferedOutputStream(newOutputStream(target))) {

                            logger.debug("writting compressed " + entryName + " file in the decompress directory");

//                            byte[] content = new byte[(int) entry.getSize()];
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

    protected Path createFile(ArchiverAction archiverAction, Path parent, Path path) {
        Path archiveFile = path;
        if (exists(archiveFile)) {
            String archiveName = getName() + count.getAndIncrement();
            if (ArchiverAction.COMPRESS.equals(archiverAction)) {
                archiveName += getExtension();
            }
            archiveFile = createFile(archiverAction, parent, Paths.get(parent.toString(), archiveName));
        }
        return archiveFile;
    }

    protected void compressFile(Path root, Path file, ArchiveOutputStream archiveOutputStream) throws IOException {
        try (InputStream inputStream = newInputStream(file)) {
            final long size = size(file);
            final byte[] content = new byte[(int) size];
            final String relativePath = root.relativize(file).toString();

            logger.debug("writting " + relativePath + " path in the archive output stream");

            ArchiveEntry entry = createArchiveEntry(relativePath, size, content);
            archiveOutputStream.putArchiveEntry(entry);
            IOUtils.copy(inputStream, archiveOutputStream); //archiveOutputStream.write(content);
            archiveOutputStream.closeArchiveEntry();
        }
    }

    protected void compressDirectory(Path root, Path dir, ArchiveOutputStream archiveOutputStream) throws IOException {
        List<Path> children = listChildren(dir);
        for (Path child : children) {
            if (isDirectory(child)) {
                compressDirectory(root, child, archiveOutputStream);
            } else {
                compressFile(root, child, archiveOutputStream);
            }
        }
    }

}
