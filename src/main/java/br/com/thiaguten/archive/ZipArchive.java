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
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

/**
 * Zip Archive Implementation.
 *
 * @author Thiago Gutenberg Carvalho da Costa
 */
public class ZipArchive extends AbstractArchive implements Archive {

    private CRC32 crc32 = new CRC32();

    private long crc32Checksum(byte[] bytes) {
        crc32.update(bytes);
        long checksum = crc32.getValue();
        crc32.reset();
        return checksum;
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
    protected ArchiveEntry createArchiveEntry(String targetPath, long targetSize, byte[] targetBytes) {
        ZipArchiveEntry zipEntry = new ZipArchiveEntry(targetPath);
        zipEntry.setSize(targetSize);
        zipEntry.setMethod(ZipEntry.STORED);
        if (targetBytes != null) {
            zipEntry.setCrc(crc32Checksum(targetBytes));
        }
        return zipEntry;
    }

    @Override
    protected ArchiveOutputStream createArchiveOutputStream(BufferedOutputStream bufferedOutputStream) {
        return new ZipArchiveOutputStream(bufferedOutputStream);
    }

    // for some internal optimizations should use the constructor that accepts a File argument
    protected ArchiveOutputStream createArchiveOutputStream(Path path) throws IOException {
        return new ZipArchiveOutputStream(path.toFile());
    }

    @Override
    protected ArchiveInputStream createArchiveInputStream(BufferedInputStream bufferedInputStream) throws IOException {
        return new ZipArchiveInputStream(bufferedInputStream);
    }

    @Override
    public Path compress(Path... paths) throws IOException {
        Path compress = null;
        ArchiveOutputStream archiveOutputStream = null;

        try {
            for (Path path : paths) {
                // get path infos
                final Path parent = path.getParent();
                final String name = path.getFileName().toString();
                final boolean isDirectory = Files.isDirectory(path);

                if (compress == null) {
                    // create compress file
                    String compressName = (paths.length == 1 ? name : getName());
                    compress = Paths.get(parent.toString(), compressName + getExtension());
                    // creates a new compress file to not override if already exists
                    // if you do not want this behavior, just comment this line
                    compress = createFile(ArchiverAction.COMPRESS, parent, compress);

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

            logger.debug("finishing the archive file " + compress);

        } finally {
            // close streams
            if (archiveOutputStream != null) {
                archiveOutputStream.finish();
                archiveOutputStream.close();
            }
        }

        return compress;
    }

    @Override
    public Path decompress(Path path) throws IOException {
        Path decompressDir = removeExtension(path);

        logger.debug("reading archive file " + path);

        try (ZipFile zipFile = new ZipFile(path.toAbsolutePath().toString())) {

            // creates a new decompress folder to not override if already exists
            // if you do not want this behavior, just comment this line
            decompressDir = createFile(ArchiverAction.DECOMPRESS, decompressDir.getParent(), decompressDir);

            Files.createDirectories(decompressDir);

            logger.debug("creating the decompress destination directory " + decompressDir);

            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            while (entries.hasMoreElements()) {
                final ZipArchiveEntry zipArchiveEntry = entries.nextElement();

                if (zipFile.canReadEntryData(zipArchiveEntry)) {
                    final String entryName = zipArchiveEntry.getName();
                    final InputStream archiveInputStream = zipFile.getInputStream(zipArchiveEntry);
                    final Path target = Paths.get(decompressDir.toString(), entryName);
                    final Path parent = target.getParent();

                    if (parent != null && !Files.exists(parent)) {
                        Files.createDirectories(parent);
                    }

                    logger.debug("reading compressed path " + entryName);

                    if (!zipArchiveEntry.isDirectory()) {
                        try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(target))) {

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