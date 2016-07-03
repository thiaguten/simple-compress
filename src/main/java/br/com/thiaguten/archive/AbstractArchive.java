/*
 * #%L
 * %%
 * Copyright (C) 2016 Thiago Gutenberg Carvalho da Costa.
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Thiago Gutenberg Carvalho da Costa. nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.file.Files.*;

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

    protected abstract ArchiveInputStream createArchiveInputStream(BufferedInputStream bufferedInputStream) throws IOException;

    protected abstract ArchiveOutputStream createArchiveOutputStream(BufferedOutputStream bufferedOutputStream) throws IOException;

    protected abstract ArchiveEntry createArchiveEntry(String path, long size, byte[] content);

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

        try {
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

            logger.debug("finishing the archive file: " + compress);

        } finally {
            // close streams
            if (archiveOutputStream != null) {
                archiveOutputStream.finish();
                archiveOutputStream.close();
            }
            if (bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }

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
        List<Path> children = listChildrens(dir);
        for (Path child : children) {
            if (isDirectory(child)) {
                compressDirectory(root, child, archiveOutputStream);
            } else {
                compressFile(root, child, archiveOutputStream);
            }
        }
    }

    protected Path removeExtension(Path file) {
        if (!isDirectory(file)) {
            String absPath = file.toAbsolutePath().toString();
            int index = absPath.lastIndexOf('.');
            if (index > 0) {
                file = Paths.get(absPath.substring(0, index));
            }
        }
        return file;
    }

    private List<Path> listChildrens(Path path) throws IOException {
        List<Path> childrens = new ArrayList<>();
        if (isDirectory(path)) {
            try (DirectoryStream<Path> directoryStream = newDirectoryStream(path)) {
                for (Path child : directoryStream) {
                    childrens.add(child);
                }
            }
        }
        return Collections.unmodifiableList(childrens);
    }

}
