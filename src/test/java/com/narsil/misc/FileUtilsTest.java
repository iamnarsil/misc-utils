package com.narsil.misc;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileUtilsTest {

    private static final Logger LOGGER = Logger.getLogger("FileUtilsTest");

    private String jsonFileName;
    private String binaryFilePath;
    private String textFilePath;
    private byte[] raw;
    private List<String> textList;
    private String dirPath;

    @Before
    public void init() {

        jsonFileName = "test.json";
        binaryFilePath = "D:\\temp\\456\\test.bin";
        textFilePath = "D:\\temp\\456\\test.txt";

        raw = Base64.getDecoder().decode("YCJfAQQwMTA4XzYGMDQwMDAwXBBhdWN2ZWZnaGlqa2xtbm9w");

        textList = new ArrayList<>();
        textList.add("{\"alphaCode\":9999,\"betaMessage\":\"this is a message\", \"gammaDate\":\"2023-06-15 06:05:11.143\"}");
        textList.add("YCJfAQQwMTA4XzYGMDQwMDAwXBBhdWN2ZWZnaGlqa2xtbm9w");
        textList.add("0123465789");

        dirPath = "D:\\temp\\abc";
    }

    @Test
    public void test00_writeBytesToPath() {
        boolean result = FileUtils.writeBytesToPath(binaryFilePath, raw);
        LOGGER.info("result = " + result);
    }

    @Test
    public void test01_writeTextToPath() {
        boolean result = FileUtils.writeTextToPath(textFilePath, textList);
        LOGGER.info("result = " + result);
    }

    @Test
    public void test02_readBytesFromPath() {
        byte[] bytes = FileUtils.readBytesFromPath(binaryFilePath);
        MiscUtils.showBytes(bytes, "result");
    }

     @Test
    public void test03_readTextFromPath() {
        List<String> textList = FileUtils.readTextFromPath(textFilePath);
        MiscUtils.showList(textList, "result");
    }

    @Test
    public void test04_getFileFromClassPath() {
        File file = FileUtils.getFileFromClassPath(jsonFileName);
        if (file != null) {
            LOGGER.info("file path = " + file.getAbsolutePath());
            LOGGER.info("file length = " + file.length());
        } else {
            LOGGER.info(jsonFileName + " is null");
        }
    }

    @Test
    public void test05_checkDirectory() {
        boolean result = FileUtils.checkDirectory(dirPath);
        LOGGER.info("result = " + result);
    }

    @Test
    public void test06_normalizeDirPath() {
        String result = FileUtils.normalizeDirPath(dirPath, true);
        LOGGER.info("result = " + result);
    }
}