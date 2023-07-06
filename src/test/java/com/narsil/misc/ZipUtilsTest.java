package com.narsil.misc;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.logging.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ZipUtilsTest {

    private static final Logger LOGGER = Logger.getLogger("ZipUtilsTest");

    private byte[] compressInput;
    private String uncompressInput;

    @Before
    public void init() {

        compressInput = new byte[16 * 1024];
        Arrays.fill(compressInput, (byte) 0x00);

        uncompressInput = "H4sIAAAAAAAA/+3BIQEAAAACIP1/2hkWIA0AAAAAAAAAAAAAAAAAAABwNgPCCugAQAAA";
    }

    @Test
    public void test00_gzipCompressThenBase64() {

        MiscUtils.showBytes(compressInput, "compress input");

        String compressOutput = ZipUtils.gzipCompressThenBase64(compressInput);
        LOGGER.info("compress output = \n" + compressOutput);
    }

    @Test
    public void test01_gzipUncompress() {

        LOGGER.info("uncompress input = \n" + uncompressInput);

        byte[] uncompressOutput = ZipUtils.gzipUncompress(uncompressInput);
        MiscUtils.showBytes(uncompressOutput, "uncompress output");
    }
}