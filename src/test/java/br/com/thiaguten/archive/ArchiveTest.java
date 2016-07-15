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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.Files.*;
import static org.junit.Assert.*;

public class ArchiveTest {

    private static final Logger log = LoggerFactory.getLogger(ArchiveTest.class);

    @Test
    public void zipArchiveCompressAndDecompressDirectory() throws IOException {
        compressAndDecompressDirectory(ArchiveType.ZIP);
    }

    @Test
    public void tarArchiveCompressAndDecompressDirectory() throws IOException {
        compressAndDecompressDirectory(ArchiveType.TAR);
    }

    @Test
    public void gzipArchiveCompressAndDecompressDirectory() throws IOException {
        compressAndDecompressDirectory(ArchiveType.GZIP);
    }

    @Test
    public void zipArchiveCompressDecompressFile() throws IOException {
        compressAndDecompressFile(ArchiveType.ZIP);
    }

    @Test
    public void tarArchiveCompressDecompressFile() throws IOException {
        compressAndDecompressFile(ArchiveType.TAR);
    }

    @Test
    public void gzipArchiveCompressDecompressFile() throws IOException {
        compressAndDecompressFile(ArchiveType.GZIP);
    }

    @Test
    public void createNewArchiveCompressIfAlreadyExists() throws IOException {
        Path path = Paths.get("src/test/resources/data/dir/test.txt");
        Path compress = ArchiveType.ZIP.getStrategy().compress(path);
        Path compress1 = ArchiveType.ZIP.getStrategy().compress(path);

        deleteIfExists(compress);
        deleteIfExists(compress1);
    }

    @Test
    public void zipAbstractArchiveCompressDecompress() throws IOException {
        Archive archive = new ZipArchive2();
        Path compress = archive.compress(Paths.get("src/test/resources/data/dir/test.txt"));
        assertTrue(exists(compress));
        assertEquals(archive.getMimeType(), ArchiveType.of(compress).getMimeType());

        Path decompress = archive.decompress(compress);
        assertTrue(exists(decompress));

        deleteIfExists(compress);
        deleteNotEmptyDirectory(decompress);
    }

    @Test
    public void abstractArchiveRemoveExtensionTest() {
        assertEquals("src/test/resources/data", AbstractArchive.removeExtension(Paths.get("src/test/resources/data")).toString());
        assertEquals("src/test/resources/test", AbstractArchive.removeExtension(Paths.get("src/test/resources/test.tar")).toString());
        assertEquals("src/test/resources/test", AbstractArchive.removeExtension(Paths.get("src/test/resources/test.tgz")).toString());
        assertEquals("src/test/resources/test", AbstractArchive.removeExtension(Paths.get("src/test/resources/test.zip")).toString());
        assertEquals("src/test/resources/data/dir/test", AbstractArchive.removeExtension(Paths.get("src/test/resources/data/dir/test.txt")).toString());
    }

    @Test
    public void abstractArchiveListChildrenTest() throws IOException {
        assertEquals(2, AbstractArchive.listChildren(Paths.get("src/test/resources/data")).size());
        assertEquals(1, AbstractArchive.listChildren(Paths.get("src/test/resources/data/dir")).size());
        assertEquals(2, AbstractArchive.listChildren(Paths.get("src/test/resources/data/dir2/")).size());
        assertEquals(1, AbstractArchive.listChildren(Paths.get("src/test/resources/data/dir2/subdir2")).size());
        assertEquals(0, AbstractArchive.listChildren(Paths.get("src/test/resources/data/dir2/subdir2/text3.txt")).size());
    }

    @Test
    public void abstractArchiveArchiveActionEnumTest() {
        assertEquals("COMPRESS", AbstractArchive.ArchiveAction.COMPRESS.name());
        assertEquals("DECOMPRESS", AbstractArchive.ArchiveAction.DECOMPRESS.name());
        assertEquals(0, AbstractArchive.ArchiveAction.COMPRESS.ordinal());
        assertEquals(1, AbstractArchive.ArchiveAction.DECOMPRESS.ordinal());
        assertEquals(2, AbstractArchive.ArchiveAction.values().length);
        assertEquals(AbstractArchive.ArchiveAction.COMPRESS, AbstractArchive.ArchiveAction.valueOf("COMPRESS"));
        assertEquals(AbstractArchive.ArchiveAction.DECOMPRESS, AbstractArchive.ArchiveAction.valueOf("DECOMPRESS"));
    }

    private void compressAndDecompressDirectory(ArchiveType type) throws IOException {
        compressAndDecompress(type, Paths.get("src/test/resources/data"));
        compressAndDecompress(type, Paths.get("src/test/resources/data/dir"), Paths.get("src/test/resources/data/dir2"));
    }

    private void compressAndDecompressFile(ArchiveType type) throws IOException {
        compressAndDecompress(type, Paths.get("src/test/resources/data/dir/test.txt"));
        compressAndDecompress(type, Paths.get("src/test/resources/data/dir/test.txt"), Paths.get("src/test/resources/data/dir2/test2.txt"));
    }

    private void compressAndDecompress(ArchiveType type, Path... path) throws IOException {
        Archive archive = type.getStrategy();
        assertNotNull(archive);

        log.info("COMPRESS {}", type);
        Path compress = archive.compress(path);
        assertTrue(exists(compress));
        assertEquals(type.getMimeType(), ArchiveType.of(compress).getMimeType());

        log.info("DECOMPRESS {}", type);
        Path decompress = archive.decompress(compress);
        assertTrue(exists(decompress));

        deleteIfExists(compress);
        deleteNotEmptyDirectory(decompress);
    }

    private class ZipArchive2 extends AbstractArchive implements Archive {

        private final ZipArchive zipArchive = new ZipArchive();

        @Override
        public String getMimeType() {
            return zipArchive.getMimeType();
        }

        @Override
        public String getExtension() {
            return zipArchive.getExtension();
        }

        @Override
        protected ArchiveEntry createArchiveEntry(String targetPath, long targetSize, byte[] targetBytes) {
            return zipArchive.createArchiveEntry(targetPath, targetSize, targetBytes);
        }

        @Override
        protected ArchiveOutputStream createArchiveOutputStream(OutputStream outputStream) {
            return zipArchive.createArchiveOutputStream(outputStream);
        }

        @Override
        protected ArchiveInputStream createArchiveInputStream(InputStream inputStream) throws IOException {
            return zipArchive.createArchiveInputStream(inputStream);
        }

    }

    private static void deleteNotEmptyDirectory(final Path dir) throws IOException {
        List<Path> children = AbstractArchive.listChildren(dir);
        for (Path child : children) {
            if (isDirectory(child)) {
                deleteNotEmptyDirectory(child);
            } else {
                deleteIfExists(child);
            }
        }
        deleteIfExists(dir);
    }

}
