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
