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

    private String dirPath;
    private String binaryFileName;
    private String textFileName;
    private String jsonFileName;
    private byte[] raw;
    private List<String> textList;

    @Before
    public void init() {
        dirPath = "D:\\temp";
        binaryFileName = "test.bin";
        textFileName = "test.txt";
        jsonFileName = "test.json";
        raw = Base64.getDecoder().decode("YCJfAQQwMTA4XzYGMDQwMDAwXBBhdWN2ZWZnaGlqa2xtbm9w");

        textList = new ArrayList<>();
        textList.add("{\"alphaCode\":9999,\"betaMessage\":\"this is a message\", \"gammaDate\":\"2023-06-15 06:05:11.143\"}");
        textList.add("YCJfAQQwMTA4XzYGMDQwMDAwXBBhdWN2ZWZnaGlqa2xtbm9w");
        textList.add("0123465789");
    }

    @Test
    public void test00_writeBytesToPath() {
        boolean result = FileUtils.writeBytesToPath(dirPath, binaryFileName, raw);
        LOGGER.info("result = " + result);
    }

    @Test
    public void test01_writeTextToPath() {
        boolean result = FileUtils.writeTextToPath(dirPath, textFileName, textList);
        LOGGER.info("result = " + result);
    }

    @Test
    public void test02_readBytesFromPath() {
        byte[] bytes = FileUtils.readBytesFromPath(dirPath, binaryFileName);
        MiscUtils.showBytes(bytes, textFileName);
    }

     @Test
    public void test03_readTextFromPath() {
        List<String> textList = FileUtils.readTextFromPath(dirPath, textFileName);
        MiscUtils.showList(textList, textFileName);
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
}