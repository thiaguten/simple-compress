package br.com.thiaguten.archive;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Archive interface.
 *
 * @author Thiago Gutenberg Carvalho da Costa
 */
public interface Archive {

    String getName();

    String getMimeType();

    String getExtension();

    Path compress(Path... paths) throws IOException;

    Path decompress(Path path) throws IOException;
}
