package com.narsil.misc;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.logging.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RandUtilsTest {

    private static final Logger LOGGER = Logger.getLogger("RandUtilsTest");

    private int length;
    private int bound;

    @Before
    public void init() {
        length = 16;
        bound = 100;
    }

    @Test
    public void test00_generateNumber() {
        LOGGER.info("rand number string (" + length + ") = " + RandUtils.getInstance().generateNumber(length));
    }

    @Test
    public void test01_generateString() {
        LOGGER.info("rand string (" + length + ") = " + RandUtils.getInstance().generateString(length));
    }

    @Test
    public void test02_generateHexString() {
        LOGGER.info("rand hex string (" + length + ") = " + RandUtils.getInstance().generateHexString(length));
    }

    @Test
    public void test03_generateBytes() {
        byte[] bytes = RandUtils.getInstance().generateBytes(length);
        MiscUtils.showBytes(bytes, "random bytes");
    }

    @Test
    public void test04_generateInteger() {
        LOGGER.info("rand number (<" + bound + ") = " + RandUtils.getInstance().generateInteger(bound));
    }
}