package com.narsil.misc;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MiscUtilsTest {

    private static final Logger LOGGER = Logger.getLogger("MiscUtilsTest");

    private byte[] bytes;
    private List<Byte> byteList;
    private List<Short> shortList;
    private List<Integer> integerList;
    private int[] intArray;
    private String json;
    private String hs;
    private byte[] _1stBytes;
    private byte[] _2ndBytes;
    private byte[] _3rdBytes;
    private short sv;
    private int iv;

    @Before
    public void init() {

        bytes = RandUtils.getInstance().generateBytes(16);
        byteList = Arrays.asList((byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03);
        shortList = Arrays.asList((short) 0x0100, (short) 0x0101, (short) 0x0102, (short) 0x0103, (short) 0x0104, (short) 0x0105, (short) 0x0106, (short) 0x0107);
        integerList = Arrays.asList(65536, 65537, 65538, 65539, 65540, 65541, 65542, 65543, 65544, 65545, 65546, 65547, 65548, 65549, 65550, 65551);
        intArray = new int[] {0, 1, 2, 3, 4, 5, 6, 7};
        json = "{\"a\":\"1\",\"b\":\"2\",\"c\":{\"x\":\">3<\",\"y\":\">///<\"}}";
        hs = "0123456789ABCDEF";

        _1stBytes = RandUtils.getInstance().generateBytes(4);
        _2ndBytes = RandUtils.getInstance().generateBytes(8);
        _3rdBytes = RandUtils.getInstance().generateBytes(16);

        sv = (short) 0xABCD;
        iv = 0x12345678;
    }

    @Test
    public void test00_showBytes() {
        MiscUtils.showBytes(bytes);
    }

    @Test
    public void test01_showBytes() {
        MiscUtils.showBytes(bytes, "test bytes");
    }

    @Test
    public void test02_showList() {
        MiscUtils.showList(integerList);
    }

    @Test
    public void test03_showList() {
        MiscUtils.showList(integerList, "test list");
    }

    @Test
    public void test04_showListByHex() {
        MiscUtils.showListByHex(byteList, "byte list", Byte.BYTES);
        MiscUtils.showListByHex(shortList, "short list", Short.BYTES);
        MiscUtils.showListByHex(integerList, "int list", Integer.BYTES);
    }

    @Test
    public void test05_showJson() {
        MiscUtils.showJson(json);
    }

    @Test
    public void test06_showJson() {
        MiscUtils.showJson(json, "test json");
    }

    @Test
    public void test07_hexStringToByteArray() {
        byte[] hsBytes = MiscUtils.hexStringToByteArray(hs);
        MiscUtils.showBytes(hsBytes, "hsBytes");
    }

    @Test
    public void test08_integerListToIntArray() {
        int[] numbers = MiscUtils.integerListToIntArray(integerList);
        for (int i=0; i<numbers.length; i++) {
            LOGGER.info(numbers[i] + ((i < numbers.length - 1) ? ", " : ""));
        }
    }

    @Test
    public void test09_intArrayToIntegerList() {
        List<Integer> numberList = MiscUtils.intArrayToIntegerList(intArray);
        MiscUtils.showList(numberList, "numberList");
    }

    @Test
    public void test10_concatBytes() {
        byte[] cBytes = MiscUtils.concatBytes(_1stBytes, _2ndBytes, _3rdBytes);
        MiscUtils.showBytes(_1stBytes, "1st bytes");
        MiscUtils.showBytes(_2ndBytes, "2nd bytes");
        MiscUtils.showBytes(_3rdBytes, "3rd bytes");
        MiscUtils.showBytes(cBytes, "cBytes");
    }

    @Test
    public void test11_shortToBytes() {
        byte[] svBytes = MiscUtils.shortToBytes(sv);
        MiscUtils.showBytes(svBytes, "svBytes");
    }

    @Test
    public void test12_intToBytes() {
        byte[] ivBytes = MiscUtils.intToBytes(iv);
        MiscUtils.showBytes(ivBytes, "ivBytes");
    }

    @Test
    public void test13_bytesToShorts() {
        int[] values = MiscUtils.bytesToShorts(bytes);
        for (int i=0; i<values.length; i++) {
            LOGGER.info(String.format("%02X", (short) values[i]) + ((i < values.length - 1) ? ", " : ""));
        }
    }

    @Test
    public void test14_shortsToBytes() {
        byte[] msvBytes = MiscUtils.shortsToBytes(intArray);
        MiscUtils.showBytes(msvBytes, "msvBytes");
    }
}