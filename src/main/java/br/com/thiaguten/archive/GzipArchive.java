package br.com.thiaguten.archive;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * Gzip Archive Implementation.
 *
 * @author Thiago Gutenberg Carvalho da Costa
 */
public class GzipArchive extends AbstractArchive implements Archive {

    @Override
    public String getMimeType() {
        return "application/gzip";
    }

    @Override
    public String getExtension() {
        return ".tgz";
    }

    @Override
    protected ArchiveEntry createArchiveEntry(String targetPath, long targetSize, byte[] targetBytes) {
        TarArchiveEntry targzEntry = new TarArchiveEntry(targetPath);
        targzEntry.setSize(targetSize);
        return targzEntry;
    }

    @Override
    protected ArchiveOutputStream createArchiveOutputStream(BufferedOutputStream bufferedOutputStream) throws IOException {
        return new TarArchiveOutputStream(new GzipCompressorOutputStream(bufferedOutputStream));
    }

    @Override
    protected ArchiveInputStream createArchiveInputStream(BufferedInputStream bufferedInputStream) throws IOException {
        return new TarArchiveInputStream(new GzipCompressorInputStream(bufferedInputStream));
    }

}
