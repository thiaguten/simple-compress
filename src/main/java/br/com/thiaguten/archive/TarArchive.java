package br.com.thiaguten.archive;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * Tar Archive Implementation.
 *
 * @author Thiago Gutenberg Carvalho da Costa
 */
public class TarArchive extends AbstractArchive implements Archive {

    @Override
    public String getMimeType() {
        return "application/x-tar";
    }

    @Override
    public String getExtension() {
        return ".tar";
    }

    @Override
    protected ArchiveEntry createArchiveEntry(String targetPath, long targetSize, byte[] targetBytes) {
        TarArchiveEntry tarEntry = new TarArchiveEntry(targetPath);
        tarEntry.setSize(targetSize);
        return tarEntry;
    }

    @Override
    protected ArchiveOutputStream createArchiveOutputStream(BufferedOutputStream bufferedOutputStream) throws IOException {
        return new TarArchiveOutputStream(bufferedOutputStream);
    }

    @Override
    protected ArchiveInputStream createArchiveInputStream(BufferedInputStream bufferedInputStream) throws IOException {
        return new TarArchiveInputStream(bufferedInputStream);
    }

}
