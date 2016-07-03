package br.com.thiaguten.archive;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class ArchiveDetectorTest {

    private ArchiveDetector archiveDetector;

    @Before
    public void setUp() {
        this.archiveDetector = new ArchiveDetector();
    }

    @Test
    public void probeTarContentTypeTest() throws IOException {
        Path tar = Paths.get("src/test/resources/test.tar");
        String type = archiveDetector.probeContentType(tar);
        assertEquals("application/x-tar", type);
    }

    @Test
    public void probeZipContentTypeTest() throws IOException {
        Path zip = Paths.get("src/test/resources/test.zip");
        String type = archiveDetector.probeContentType(zip);
        assertEquals("application/zip", type);
    }

    @Test
    public void probeGzipContentTypeTest() throws IOException {
        Path gzip = Paths.get("src/test/resources/test.tgz");
        String type = archiveDetector.probeContentType(gzip);
        assertEquals("application/gzip", type);
    }

    @Test
    public void probeDirectoryContentTypeTest() throws IOException {
        Path dir = Paths.get("src/test/resources/test.tar").getParent();
        String type = archiveDetector.probeContentType(dir);
        assertEquals("text/directory", type);
    }

}
