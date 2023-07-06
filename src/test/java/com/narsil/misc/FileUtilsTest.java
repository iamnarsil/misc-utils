package com.narsil.misc;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.Base64;
import java.util.logging.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileUtilsTest {

    private static final Logger LOGGER = Logger.getLogger("FileUtilsTest");

    private String dirPath;
    private String binaryFileName;
    private String jsonFileName;
    private byte[] raw;

    @Before
    public void init() {
        dirPath = "D:\\temp";
        binaryFileName = "test.bin";
        jsonFileName = "test.json";
        raw = Base64.getDecoder().decode("YCJfAQQwMTA4XzYGMDQwMDAwXBBhdWN2ZWZnaGlqa2xtbm9w");
    }

    @Test
    public void test00_writeBytesToPath() {
        boolean result = FileUtils.writeBytesToPath(dirPath, binaryFileName, raw);
        LOGGER.info("result = " + result);
    }

    @Test
    public void test01_readBytesFromPath() {
        byte[] bytes = FileUtils.readBytesFromPath(dirPath, binaryFileName);
        MiscUtils.showBytes(bytes, binaryFileName);
    }

    @Test
    public void test02_getFileFromClassPath() {
        File file = FileUtils.getFileFromClassPath(jsonFileName);
        if (file != null) {
            LOGGER.info("file path = " + file.getAbsolutePath());
            LOGGER.info("file length = " + file.length());
        } else {
            LOGGER.info(jsonFileName + " is null");
        }
    }
}