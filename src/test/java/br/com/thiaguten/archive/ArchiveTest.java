package br.com.thiaguten.archive;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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
        compress.toFile().deleteOnExit();
        compress1.toFile().deleteOnExit();
    }

    private void compressAndDecompressDirectory(ArchiveType type) throws IOException {
        compressAndDecompress(type, Paths.get("src/test/resources/data"));
    }

    private void compressAndDecompressFile(ArchiveType type) throws IOException {
        compressAndDecompress(type, Paths.get("src/test/resources/data/dir/test.txt"));
    }

    private void compressAndDecompress(ArchiveType type, Path path) throws IOException {
        Archive archive = type.getStrategy();
        assertNotNull(archive);

        log.info("COMPRESS {}", type);
        Path compress = archive.compress(path);
        assertTrue(exists(compress));
        assertEquals(type.getMimeType(), ArchiveType.of(compress).getMimeType());
        compress.toFile().deleteOnExit();

        log.info("DECOMPRESS {}", type);
        Path decompress = archive.decompress(compress);
        assertTrue(exists(decompress));
//        decompress.toFile().deleteOnExit(); // delete on exit do not make recursive deletion
        deleteFolder(decompress, true);
    }

    private void deleteFolder(Path path, boolean bypassNotEmptyDirectory) throws IOException {
        if (isDirectory(path)) {
            if (bypassNotEmptyDirectory) {
                List<Path> children = listChildren(path);
                for (Path child : children) {
                    if (isDirectory(child)) {
                        deleteFolder(child, bypassNotEmptyDirectory);
                    } else {
                        deleteIfExists(child);
                    }
                }
            }
            deleteIfExists(path);
        }
    }

    private List<Path> listChildren(Path path) throws IOException {
        List<Path> children = new ArrayList<>();
        if (isDirectory(path)) {
            try (DirectoryStream<Path> childrenStream = newDirectoryStream(path)) {
                for (Path child : childrenStream) {
                    children.add(child);
                }
            }
        }
        return Collections.unmodifiableList(children);
    }
}
