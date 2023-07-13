package com.narsil.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Base64;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * zip operation toolkit
 *
 * @author iamnarsil
 * @version 20230713
 * @since 20230328
 */
public class ZipUtils {

    private static final Logger LOGGER = Logger.getLogger("ZipUtils");

    /**
     * using gzip to compress byte array, then encoding to base64
     *
     * @param input byte array
     * @return encoded result
     */
    public static String gzipCompressThenBase64(byte[] input) {

        String output = null;

        if (input != null && input.length > 0) {

            // try-catch-resource
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    GZIPOutputStream gzos = new GZIPOutputStream(baos)) {

                // gzip compress
                gzos.write(input);
                gzos.flush();
                gzos.close();

                byte[] bytes = baos.toByteArray();

                // base64 encode
                if (bytes.length > 0) {
                    output = Base64.getEncoder().encodeToString(bytes);
                }

            } catch (Exception e) {
                LOGGER.severe(e.getMessage());
            }

        }

        return output;
    }

    /**
     * base64 decode firstly, then using gzip to uncompress
     * @param input encoded data
     * @return uncompress byte array
     */
    public static byte[] gzipUncompress(String input) {

        byte[] output = null;

        if (input != null && !input.trim().isEmpty()) {

            // base64 decode
            byte[] compressed = Base64.getDecoder().decode(input);

            // check input is compressed or not
            boolean isCompressed =
                    (compressed[0] == (byte)(GZIPInputStream.GZIP_MAGIC)) &&
                            (compressed[1] == (byte)(GZIPInputStream.GZIP_MAGIC >> 8));

            if (isCompressed) {

                // try-catch-resource
                try (ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
                        GZIPInputStream gzis = new GZIPInputStream(bais);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                    // gzip uncompress
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = gzis.read(buffer)) > 0) {
                        baos.write(buffer, 0, count);
                    }
                    output = baos.toByteArray();

                } catch (Exception e) {
                    LOGGER.severe(e.getMessage());
                }
            } else {
                output = compressed;
            }
        }

        return output;
    }

    /**
     * zip entire directory
     *
     * @param sourceDirPath source directory absolute path
     * @param zipFilePath expected zip file absolute path
     * @return zip result
     */
    public static boolean zipDirectory(String sourceDirPath, String zipFilePath) {

        File dir = new File(sourceDirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            LOGGER.severe(sourceDirPath + " is NOT a directory");
            return false;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath);
                ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {

            Path source = Paths.get(sourceDirPath);
            Files.walkFileTree(source, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) {

                    try {
                        Path relativePath = source.relativize(dir);
                        String relativeDirName = FileUtils.normalizeDirPath(relativePath.toString(), false);

                        if (relativeDirName != null && !relativeDirName.isBlank()) {
                            // make a zip entry, then put it into zipOutputStream
                            ZipEntry zipEntry = new ZipEntry(relativeDirName + "/");
                            // LOGGER.info("zipEntry name = " + zipEntry.getName());
                            zipOutputStream.putNextEntry(zipEntry);
                            zipOutputStream.closeEntry();
                        }

                    } catch (Exception e) {
                        LOGGER.severe(e.getMessage());
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {

                    // skip symbolic link
                    if (attributes.isSymbolicLink()) {
                        return FileVisitResult.CONTINUE;
                    }

                    try (FileInputStream fileInputStream = new FileInputStream(file.toFile())) {

                        Path relativePath = source.relativize(file);
                        // make a zip entry, then put it into zipOutputStream
                        ZipEntry zipEntry = new ZipEntry(relativePath.toString());
                        // LOGGER.info("zipEntry name = " + zipEntry.getName());
                        zipOutputStream.putNextEntry(zipEntry);

                        // start to read file
                        byte[] buffer = new byte[4096];
                        int bytes;
                        while ((bytes = fileInputStream.read(buffer)) != -1) {
                            zipOutputStream.write(buffer, 0, bytes);
                        }

                        zipOutputStream.closeEntry();

                    } catch (Exception e) {
                        LOGGER.severe(e.getMessage());
                    }
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult visitFileFailed(Path path, IOException e) {
                    LOGGER.severe(e.getMessage());
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            return false;
        }

        return true;
    }
}
