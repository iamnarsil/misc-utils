package com.narsil.misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * file operation toolkit
 *
 * @author iamnarsil
 * @version 20230710
 * @since 20230328
 */
public class FileUtils {

    private static final Logger LOGGER = Logger.getLogger("FileUtils");

    /**
     * write raw data to file
     *
     * @param dirPath  directory path
     * @param fileName file name
     * @param bytes    raw data
     * @return result
     */
    public static boolean writeBytesToPath(String dirPath, String fileName, byte[] bytes) {

        if (dirPath == null || dirPath.isBlank() || fileName == null || fileName.isBlank() || bytes == null || bytes.length == 0) {
            return false;
        }

        if (dirPath.endsWith(File.separator)) {
            dirPath = dirPath.substring(0, dirPath.length() - 1);
        }

        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            return false;
        }

        String filePath = dirPath.concat(File.separator).concat(fileName);

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(bytes);
        } catch (Exception e) {
            // e.printStackTrace();
            LOGGER.severe(e.getMessage());
        }

        return true;
    }

    /**
     * write text data to file
     *
     * @param dirPath  directory path
     * @param fileName file name
     * @param textList text data
     * @return result
     */
    public static boolean writeTextToPath(String dirPath, String fileName, List<String> textList) {

        if (dirPath == null || dirPath.isBlank() || fileName == null || fileName.isBlank() || textList == null || textList.isEmpty()) {
            return false;
        }

        if (dirPath.endsWith(File.separator)) {
            dirPath = dirPath.substring(0, dirPath.length() - 1);
        }

        File dir = new File(dirPath);
        if (!dir.exists() && !dir.mkdirs()) {
            return false;
        }

        String filePath = dirPath.concat(File.separator).concat(fileName);

        String text = "";
        for (String s : textList) {
            text = text.concat(s).concat("\n");
        }

        try (FileWriter fileWriter = new FileWriter(filePath); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(text);
        } catch (Exception e) {
            // e.printStackTrace();
            LOGGER.severe(e.getMessage());
        }

        return true;
    }

    /**
     * read raw data from file
     *
     * @param dirPath  directory path
     * @param fileName file name
     * @return raw data
     */
    public static byte[] readBytesFromPath(String dirPath, String fileName) {

        byte[] bytes = new byte[0];

        if (dirPath == null || dirPath.isBlank() || fileName == null || fileName.isBlank()) {
            return bytes;
        }

        if (dirPath.endsWith(File.separator)) {
            dirPath = dirPath.substring(0, dirPath.length() - 1);
        }

        String filePath = dirPath.concat(File.separator).concat(fileName);
        File file = new File(filePath);

        if (!file.exists()) {
            return bytes;
        }

        bytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            int length = fis.read(bytes);
            if (length == file.length()) {
                return bytes;
            }
        } catch (Exception e) {
            // e.printStackTrace();
            LOGGER.severe(e.getMessage());
        }

        return bytes;
    }

    /**
     * read text data from file
     *
     * @param dirPath directory path
     * @param fileName file name
     * @return text data
     */
    public static List<String> readTextFromPath(String dirPath, String fileName) {

        List<String> textList = new ArrayList<>();

        if (dirPath == null || dirPath.isBlank() || fileName == null || fileName.isBlank()) {
            return textList;
        }

        if (dirPath.endsWith(File.separator)) {
            dirPath = dirPath.substring(0, dirPath.length() - 1);
        }

        String filePath = dirPath.concat(File.separator).concat(fileName);
        File file = new File(filePath);

        if (!file.exists()) {
            return textList;
        }

        // use stream -> efficiently reading large file
        // NOTE: the file is processed lazily -> only part of the content is stored in memory at a given time
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.forEach(textList::add);
        } catch (Exception e) {
            // e.printStackTrace();
            LOGGER.severe(e.getMessage());
        }

        return textList;
    }

    /**
     * get file from classpath
     *
     * @param fileName file name
     * @return file
     */
    public static File getFileFromClassPath(String fileName) {

        File file = null;

        try {
            URL fileUrl  = FileUtils.class.getClassLoader().getResource(fileName);
            if (fileUrl != null) {
                file = new File(fileUrl.toURI());
            }
        } catch (Exception e) {
            // e.printStackTrace();
            LOGGER.severe(e.getMessage());
        }

        return file;
    }
}
