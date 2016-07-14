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

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class TikaFileTypeDetectorTest {

    private TikaFileTypeDetector tikaFileTypeDetector;

    @Before
    public void setUp() {
        this.tikaFileTypeDetector = new TikaFileTypeDetector();
    }

    @Test
    public void probeTarContentTypeTest() throws IOException {
        Path tar = Paths.get("src/test/resources/test.tar");
        String type = tikaFileTypeDetector.probeContentType(tar);
        assertEquals("application/x-tar", type);
    }

    @Test
    public void probeZipContentTypeTest() throws IOException {
        Path zip = Paths.get("src/test/resources/test.zip");
        String type = tikaFileTypeDetector.probeContentType(zip);
        assertEquals("application/zip", type);
    }

    @Test
    public void probeGzipContentTypeTest() throws IOException {
        Path gzip = Paths.get("src/test/resources/test.tgz");
        String type = tikaFileTypeDetector.probeContentType(gzip);
        assertEquals("application/gzip", type);
    }

    @Test
    public void probeDirectoryContentTypeTest() throws IOException {
        Path dir = Paths.get("src/test/resources/test.tar").getParent();
        String type = tikaFileTypeDetector.probeContentType(dir);
        assertEquals("text/directory", type);
    }

}
