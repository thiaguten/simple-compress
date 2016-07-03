package br.com.thiaguten.archive;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Archive Supported Types.
 *
 * @author Thiago Gutenberg Carvalho da Costa
 */
public enum ArchiveType {

    TAR {
        @Override
        public Archive getStrategy() {
            return MAP.get(TAR);
        }
    },
    ZIP {
        @Override
        public Archive getStrategy() {
            return MAP.get(ZIP);
        }
    },
    GZIP {
        @Override
        public Archive getStrategy() {
            return MAP.get(GZIP);
        }
    };

    private static final Map<ArchiveType, Archive> MAP = new HashMap<>();

    static {
        MAP.put(TAR, new TarArchive());
        MAP.put(ZIP, new ZipArchive());
        MAP.put(GZIP, new GzipArchive());
    }

    // convenient

    public static ArchiveType of(Path path) throws IOException {
        // An implementation using "Java Service Provider Interface (SPI)" is
        // registered in /META-INF/services/java.nio.file.spi.FileTypeDetector,
        // improving the standard default NIO implementation with the Apache Tika API.
        return of(Files.probeContentType(path));
    }

    public static ArchiveType of(String mimeType) {
        for (ArchiveType type : values()) {
            if (type.getMimeType().equalsIgnoreCase(mimeType)) {
                return type;
            }
        }
        throw new RuntimeException(String.format("Archive type (%s) not supported", mimeType));
    }

    // shortcuts

    public String getMimeType() {
        return getStrategy().getMimeType();
    }

    public String getExtension() {
        return getStrategy().getExtension();
    }

    public abstract Archive getStrategy();

}
