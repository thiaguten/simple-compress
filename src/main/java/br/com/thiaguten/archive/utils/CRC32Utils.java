package br.com.thiaguten.archive.utils;

import java.util.zip.CRC32;

/**
 * CRC32 Utility class
 *
 * @author Thiago Gutenberg Carvalho da Costa
 */
public final class CRC32Utils {

    private static final CRC32 crc32 = new CRC32();

    public static long crc32Checksum(byte[] bytes) {
        crc32.update(bytes);
        long checksum = crc32.getValue();
        crc32.reset();
        return checksum;
    }

}
