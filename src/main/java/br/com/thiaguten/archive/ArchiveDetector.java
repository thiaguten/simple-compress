package br.com.thiaguten.archive;

import org.apache.tika.Tika;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;

public class ArchiveDetector extends FileTypeDetector {

    private Tika tika = new Tika();

    @Override
    public String probeContentType(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            return "text/directory";
        }
        return tika.detect(path);
    }
}
