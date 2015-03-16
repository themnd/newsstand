package com.atex.plugins.newsstand.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Simple file utility abstractions.
 */
public abstract class FileUtils {

    public static void createFile(final InputStream is, final File f) throws IOException {
        Files.copy(is, Paths.get(f.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
    }

    public static void createFile(final byte[] bytes, final File f) throws IOException {
        try (final ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            createFile(in, f);
        }
    }

}
