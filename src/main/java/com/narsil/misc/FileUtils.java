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
 * @version 20230713
 * @since 20230328
 */
public class FileUtils {

    private static final Logger LOGGER = Logger.getLogger("FileUtils");

    // max allowable file size: 1 GB
    private static final long MAX_FLIE_SIZE = 1073741824L;

    /**
     * write raw data to file
     *
     * @param filePath  file path
     * @param bytes    raw data
     * @return result
     */
    public static boolean writeBytesToPath(String filePath, byte[] bytes) {

        // check file path
        if (filePath == null || filePath.isBlank()) {
            LOGGER.severe("invalid file path");
            return false;
        }

        // check if file exists
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                if (!file.delete()) {
                    LOGGER.severe("fail to delete file");
                }
            }
        } else {
            // check if parent directory exists
            File parent = file.getParentFile();
            if (parent == null) {
                String dirPath = filePath.substring(0, filePath.lastIndexOf(File.separator));
                File dir = new File(dirPath);
                if (!dir.mkdirs()) {
                    LOGGER.severe("fail to create directory");
                }
            } else {
                if (!parent.exists() || !parent.isDirectory()) {
                    if (!parent.mkdirs()) {
                        LOGGER.severe("fail to create directory");
                    }
                }
            }
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(bytes);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }

        return true;
    }

    /**
     * write text data to file
     *
     * @param filePath  file path
     * @param textList text data
     * @return result
     */
    public static boolean writeTextToPath(String filePath, List<String> textList) {

        // check file path
        if (filePath == null || filePath.isBlank()) {
            LOGGER.severe("invalid file path");
            return false;
        }

        // check if file exists
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                if (!file.delete()) {
                    LOGGER.severe("fail to delete file");
                }
            }
        } else {
            // check if parent directory exists
            File parent = file.getParentFile();
            if (parent == null) {
                String dirPath = filePath.substring(0, filePath.lastIndexOf(File.separator));
                File dir = new File(dirPath);
                if (!dir.mkdirs()) {
                    LOGGER.severe("fail to create directory");
                }
            } else {
                if (!parent.exists() || !parent.isDirectory()) {
                    if (!parent.mkdirs()) {
                        LOGGER.severe("fail to create directory");
                    }
                }
            }
        }

        String text = "";
        for (String s : textList) {
            text = text.concat(s).concat("\n");
        }

        try (FileWriter fileWriter = new FileWriter(filePath);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(text);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }

        return true;
    }

    /**
     * read raw data from file
     *
     * @param filePath file path
     * @return raw data
     */
    public static byte[] readBytesFromPath(String filePath) {

        byte[] bytes = new byte[0];

        // check file path
        if (filePath == null || filePath.isBlank()) {
            LOGGER.severe("invalid file path");
            return bytes;
        }

        // check if file exists
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            LOGGER.severe("file not exit");
            return bytes;
        }

        // check file length
        if (file.length() > MAX_FLIE_SIZE) {
            LOGGER.severe("file size exceeds limit");
            return bytes;
        }

        bytes = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            int length = fis.read(bytes);
            if (length == file.length()) {
                return bytes;
            }
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }

        return bytes;
    }

    /**
     * read text data from file
     *
     * @param filePath file path
     * @return text data
     */
    public static List<String> readTextFromPath(String filePath) {

        List<String> textList = new ArrayList<>();

        // check file path
        if (filePath == null || filePath.isBlank()) {
            LOGGER.severe("invalid file path");
            return textList;
        }

        // check if file exists
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            LOGGER.severe("file not exit");
            return textList;
        }

        // check file length
        if (file.length() > MAX_FLIE_SIZE) {
            LOGGER.severe("file size exceeds limit");
            return textList;
        }

        // use stream -> efficiently reading large file
        // NOTE: the file is processed lazily -> only part of the content is stored in memory at a given time
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.forEach(textList::add);
        } catch (Exception e) {
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
            LOGGER.severe(e.getMessage());
        }

        return file;
    }

    /**
     * check if directory exists, otherwise create it
     *
     * @param dirPath target directory absolute path
     * @return check result
     */
    public static boolean checkDirectory(String dirPath) {

        // check directory path
        if (dirPath == null || dirPath.isBlank()) {
            return false;
        }

        File dir = new File(dirPath);
        if (dir.exists() && dir.isDirectory()) {
            return true;
        } else {
            if (dir.mkdirs()) {
                return true;
            } else {
                LOGGER.severe("fail to create directory");
                return false;
            }
        }
    }

    public static String normalizeDirPath(String dirPath, boolean endWithSeparator) {

        // check dir path
        if (dirPath == null || dirPath.isBlank()) {
            return "";
        }

        String path = dirPath.trim();
        if (endWithSeparator) {
            if (path.endsWith(File.separator)) {
                return path;
            } else {
                return path.concat(File.separator);
            }
        } else {
            if (path.endsWith(File.separator)) {
                return path.substring(0, path.length() - 1);
            } else {
                return path;
            }
        }
    }
}
