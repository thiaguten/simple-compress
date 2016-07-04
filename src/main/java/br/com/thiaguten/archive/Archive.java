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
