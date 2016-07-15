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
package br.com.thiaguten.archive;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.Files.probeContentType;

/**
 * Archive Supported Types.
 *
 * @author Thiago Gutenberg Carvalho da Costa
 */
public enum ArchiveType {

    TAR {
        @Override
        public Archive getStrategy() {
            return HOLDER.get(TAR);
        }
    },
    ZIP {
        @Override
        public Archive getStrategy() {
            return HOLDER.get(ZIP);
        }
    },
    GZIP {
        @Override
        public Archive getStrategy() {
            return HOLDER.get(GZIP);
        }
    };

    private static final Map<ArchiveType, Archive> HOLDER = new HashMap<>();

    static {
        HOLDER.put(TAR, new TarArchive());
        HOLDER.put(ZIP, new ZipArchive());
        HOLDER.put(GZIP, new GzipArchive());
    }

    // convenient

    public static ArchiveType of(Path path) throws IOException {
        return of(probeContentType(path));
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
