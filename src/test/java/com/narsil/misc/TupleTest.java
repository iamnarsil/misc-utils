package com.narsil.misc;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.logging.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TupleTest {

    private static final Logger LOGGER = Logger.getLogger("TupleTest");

    private int data_int;
    private String data_string;
    private byte[] data_bytes;

    @Before
    public void init() {
        data_int = 123;
        data_string = "456";
        data_bytes = MiscUtils.hexStringToByteArray("778899AABBCC");
    }

    @Test
    public void test00_collect() {

        Tuple.Pair<Integer, String> pair = Tuple.collect(data_int, data_string);
        LOGGER.info("int = " + pair.getA() + ", string = " + pair.getB());
    }

    @Test
    public void test01_collect() {

        Tuple.Ternary<Integer, String, byte[]> ternary = Tuple.collect(data_int, data_string, data_bytes);
        LOGGER.info("int = " + ternary.getA() + ", string = " + ternary.getB());
        MiscUtils.showBytes(ternary.getC());
    }
}