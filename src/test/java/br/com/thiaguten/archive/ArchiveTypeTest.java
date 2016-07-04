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

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArchiveTypeTest {

    @Test
    public void mimetypeTarTest() {
        String mimeType = ArchiveType.TAR.getMimeType();
        assertEquals("application/x-tar", mimeType);
    }

    @Test
    public void mimetypeZipTest() {
        String mimeType = ArchiveType.ZIP.getMimeType();
        assertEquals("application/zip", mimeType);
    }

    @Test
    public void mimetypeGzipTest() {
        String mimeType = ArchiveType.GZIP.getMimeType();
        assertEquals("application/gzip", mimeType);
    }

    @Test
    public void extensionTarTest() {
        String extension = ArchiveType.TAR.getExtension();
        assertEquals(".tar", extension);
    }

    @Test
    public void extensionZipTest() {
        String extension = ArchiveType.ZIP.getExtension();
        assertEquals(".zip", extension);
    }

    @Test
    public void extensionGzipTest() {
        String extension = ArchiveType.GZIP.getExtension();
        assertEquals(".tgz", extension);
    }

    @Test
    public void strategyTarTest() {
        Archive strategy = ArchiveType.TAR.getStrategy();
        assertTrue(strategy instanceof TarArchive);
    }

    @Test
    public void strategyZipTest() {
        Archive strategy = ArchiveType.ZIP.getStrategy();
        assertTrue(strategy instanceof ZipArchive);
    }

    @Test
    public void strategyGzipTest() {
        Archive strategy = ArchiveType.GZIP.getStrategy();
        assertTrue(strategy instanceof GzipArchive);
    }

    @Test
    public void ofMimetypeTarTest() {
        ArchiveType type = ArchiveType.of("application/x-tar");
        assertEquals(type, ArchiveType.TAR);
    }

    @Test
    public void ofMimetypeZipTest() {
        ArchiveType type = ArchiveType.of("application/zip");
        assertEquals(type, ArchiveType.ZIP);
    }

    @Test
    public void ofMimetypeGzipTest() {
        ArchiveType type = ArchiveType.of("application/gzip");
        assertEquals(type, ArchiveType.GZIP);
    }

    @Test(expected = RuntimeException.class)
    public void invalidOfTest() {
        ArchiveType.of("");
    }

    @Test
    public void ofPathTarTest() throws IOException {
        ArchiveType type = ArchiveType.of(Paths.get("src/test/resources/test.tar"));
        assertEquals(type, ArchiveType.TAR);
    }

    @Test
    public void ofPathZipTest() throws IOException {
        ArchiveType type = ArchiveType.of(Paths.get("src/test/resources/test.zip"));
        assertEquals(type, ArchiveType.ZIP);
    }

    @Test
    public void ofPathGzipTest() throws IOException {
        ArchiveType type = ArchiveType.of(Paths.get("src/test/resources/test.tgz"));
        assertEquals(type, ArchiveType.GZIP);
    }

}
