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
package br.com.thiaguten.archive.support;

import org.apache.commons.compress.utils.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static br.com.thiaguten.archive.support.FileUtils.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class FileUtilsTest {

    @Test
    public void illegalInstatiationTest() throws IllegalAccessException, InstantiationException, NoSuchMethodException {
        try {
            Constructor c = FileUtils.class.getDeclaredConstructor();
            assertTrue(Modifier.isPrivate(c.getModifiers()));
            c.setAccessible(true);
            c.newInstance();
        } catch (InvocationTargetException e) {
            assertThat(e.getCause(), is(instanceOf(AssertionError.class)));
        }
    }

    @Test
    public void deleteEmptyDirectoryTest() throws IOException {
        Path tempDir = createDirectories(Paths.get("src/test/resources", UUID.randomUUID().toString()));
        assertTrue(exists(tempDir));
        deleteDirectory(tempDir);
        assertTrue(!exists(tempDir));
    }

    @Test
    public void deleteNotEmptyDirectoryTest() throws IOException {
        Path tempDir = createDirectories(Paths.get("src/test/resources", UUID.randomUUID().toString()));
        Path tempFile = createFile(Paths.get(tempDir.toString(), "tempFile.txt"));
        assertTrue(exists(tempFile));
        deleteNotEmptyDirectory(tempDir);
        assertTrue(!exists(tempDir));
    }

    @Test(expected = DirectoryNotEmptyException.class)
    public void deleteNotEmptyDirectoryExceptionTest() throws IOException {
        Path tempDir = createDirectories(Paths.get("src/test/resources"));
        Path tempFile = createFile(Paths.get(tempDir.toString(), "tempFile.txt"));
        tempFile.toFile().deleteOnExit();
        assertTrue(exists(tempFile));
        deleteDirectory(tempDir);
    }

    @Test
    public void listChildrenTest() throws IOException {
        assertEquals(2, listChildren(Paths.get("src/test/resources/data")).size());
        assertEquals(1, listChildren(Paths.get("src/test/resources/data/dir")).size());
        assertEquals(2, listChildren(Paths.get("src/test/resources/data/dir2/")).size());
        assertEquals(1, listChildren(Paths.get("src/test/resources/data/dir2/subdir2")).size());
        assertEquals(0, listChildren(Paths.get("src/test/resources/data/dir2/subdir2/text3.txt")).size());
    }

    @Test
    public void probeContentTypeTest() throws IOException {
        assertEquals("application/x-tar", probeContentType(Paths.get("src/test/resources/test.tar")));
        assertEquals("application/gzip", probeContentType(Paths.get("src/test/resources/test.tgz")));
        assertEquals("application/zip", probeContentType(Paths.get("src/test/resources/test.zip")));
        assertEquals("text/plain", probeContentType(Paths.get("src/test/resources/data/dir/test.txt")));
        assertEquals("text/plain", probeContentType(Paths.get("src/test/resources/data/dir2/test2.txt")));
        assertEquals("text/plain", probeContentType(Paths.get("src/test/resources/data/dir2/subdir2/test3.txt")));
        assertEquals("text/directory", probeContentType(Paths.get("src/test/resources/data/dir")));
        assertEquals("text/directory", probeContentType(Paths.get("src/test/resources/data")));
    }

    @Test
    public void existsTest() {
        assertTrue(exists(Paths.get("src/test/resources/test.tar")));
        assertTrue(exists(Paths.get("src/test/resources/test.tgz")));
        assertTrue(exists(Paths.get("src/test/resources/test.zip")));
        assertTrue(exists(Paths.get("src/test/resources/data/dir/test.txt")));
        assertTrue(exists(Paths.get("src/test/resources/data/dir2/test2.txt")));
        assertTrue(exists(Paths.get("src/test/resources/data/dir2/subdir2/test3.txt")));
        assertTrue(exists(Paths.get("src/test/resources/data/dir")));
        assertTrue(exists(Paths.get("src/test/resources/data")));
    }

    @Test
    public void createFileTest() throws IOException {
        Path tempDir = createDirectories(Paths.get("src/test/resources", UUID.randomUUID().toString()));
        Path tempFile = createFile(Paths.get(tempDir.toString(), "tempFile.txt"));
//        tempFile.toFile().deleteOnExit();
        assertTrue(exists(tempFile));
        deleteNotEmptyDirectory(tempDir);
    }

    @Test
    public void createDirectoriesTest() throws IOException {
        Path tempDir = createDirectories(Paths.get("src/test/resources/createDirectoriesTest/tempSubDir"));
        assertTrue(exists(tempDir));
        deleteNotEmptyDirectory(tempDir.getParent());
    }

    @Test
    public void isDirectoryTest() {
        assertTrue(isDirectory(Paths.get("src/test/resources/data")));
        assertTrue(isDirectory(Paths.get("src/test/resources/data/dir")));
        assertTrue(isDirectory(Paths.get("src/test/resources/data/dir2")));
        assertTrue(isDirectory(Paths.get("src/test/resources/data/dir2/subdir2")));
        assertFalse(isDirectory(Paths.get("src/test/resources/data/dir/test.txt")));
        assertFalse(isDirectory(Paths.get("src/test/resources/data/dir2/test2.txt")));
        assertFalse(isDirectory(Paths.get("src/test/resources/data/dir2/subdir2/test3.txt")));
        assertFalse(isDirectory(Paths.get("src/test/resources/test.tar")));
        assertFalse(isDirectory(Paths.get("src/test/resources/test.tgz")));
        assertFalse(isDirectory(Paths.get("src/test/resources/test.zip")));
    }

    @Test
    public void removeExtensionTest() {
        assertEquals("src/test/resources/data", removeExtension(Paths.get("src/test/resources/data")).toString());
        assertEquals("src/test/resources/test", removeExtension(Paths.get("src/test/resources/test.tar")).toString());
        assertEquals("src/test/resources/test", removeExtension(Paths.get("src/test/resources/test.tgz")).toString());
        assertEquals("src/test/resources/test", removeExtension(Paths.get("src/test/resources/test.zip")).toString());
        assertEquals("src/test/resources/data/dir/test", removeExtension(Paths.get("src/test/resources/data/dir/test.txt")).toString());
        assertEquals("src/test/resources/data/dir2/test2", removeExtension(Paths.get("src/test/resources/data/dir2/test2.txt")).toString());
        assertEquals("src/test/resources/data/dir2/subdir2/test3", removeExtension(Paths.get("src/test/resources/data/dir2/subdir2/test3.txt")).toString());
    }

    @Test
    public void sizeTest() throws IOException {
        assertEquals(21, size(Paths.get("src/test/resources/data/dir/test.txt")));
        assertEquals(22, size(Paths.get("src/test/resources/data/dir2/test2.txt")));
        assertEquals(17, size(Paths.get("src/test/resources/data/dir2/subdir2/test3.txt")));
        assertEquals(10240, size(Paths.get("src/test/resources/test.tar")));
        assertEquals(45, size(Paths.get("src/test/resources/test.tgz")));
        assertEquals(22, size(Paths.get("src/test/resources/test.zip")));
        assertEquals(4096, size(Paths.get("src/test/resources/data")));
        assertEquals(4096, size(Paths.get("src/test/resources/data/dir")));
        assertEquals(4096, size(Paths.get("src/test/resources/data/dir2")));
        assertEquals(4096, size(Paths.get("src/test/resources/data/dir2/subdir2")));
    }

    @Test
    public void totalSizeTest() throws IOException {
        assertEquals(21, totalSize(Paths.get("src/test/resources/data/dir/test.txt")));
        assertEquals(22, totalSize(Paths.get("src/test/resources/data/dir2/test2.txt")));
        assertEquals(17, totalSize(Paths.get("src/test/resources/data/dir2/subdir2/test3.txt")));
        assertEquals(10240, totalSize(Paths.get("src/test/resources/test.tar")));
        assertEquals(45, totalSize(Paths.get("src/test/resources/test.tgz")));
        assertEquals(22, totalSize(Paths.get("src/test/resources/test.zip")));
        assertEquals(16444, totalSize(Paths.get("src/test/resources/data")));
        assertEquals(4117, totalSize(Paths.get("src/test/resources/data/dir")));
        assertEquals(8231, totalSize(Paths.get("src/test/resources/data/dir2")));
        assertEquals(4113, totalSize(Paths.get("src/test/resources/data/dir2/subdir2")));
    }

    @Test
    public void newInputStreamTestAndnewOutputStreamTest() throws IOException {
        Path inFile = Paths.get("src/test/resources/data/dir/test.txt");
        Path outFile = createFile(Paths.get("src/test/resources/test.txt"));
        outFile.toFile().deleteOnExit();
        try (InputStream is = newInputStream(inFile)) {
            assertNotNull(is);
            try (OutputStream out = newOutputStream(outFile)) {
                assertNotNull(out);
                long outSize = IOUtils.copy(is, out);
                assertEquals(size(inFile), outSize);
            }
        }
    }

}
