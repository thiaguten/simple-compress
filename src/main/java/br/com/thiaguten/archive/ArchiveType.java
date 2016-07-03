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
