package com.narsil.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * misc. function toolkit
 *
 * @author iamnarsil
 * @version 20230705
 * @since 20230328
 */
public class MiscUtils {

    private static final Logger LOGGER = Logger.getLogger("MiscUtils");

//    static {
//        // use new StreamHandler (System.out) to replace default ConsoleHandler (System.err)
//        SimpleFormatter simpleFormatter = new SimpleFormatter();
//        StreamHandler streamHandler = new StreamHandler(System.out, simpleFormatter);
//        LOGGER.setUseParentHandlers(false);
//        LOGGER.addHandler(streamHandler);
//    }

    /**
     * show byte array
     *
     * @param bytes byte array
     */
    public static void showBytes(byte[] bytes) {
        showBytes(bytes, null);
    }

    /**
     * show byte array
     *
     * @param bytes byte array
     * @param title title will be shown
     */
    public static void showBytes(byte[] bytes, String title) {

        String result = "";

        title = (title != null && !title.isBlank()) ? title : "bytes";
        if (bytes == null) {
            result = result.concat("\n" + title + " -> null\n");
        } else {
            result = result.concat("\n" + title + " (" + bytes.length + ") =\n");
            for (int i = 0; i < bytes.length; i++) {

                if (i % 16 == 0) {
                    result = result.concat(String.format("%04X", i) + ": ");
                }

                result = result.concat(("" + "0123456789ABCDEF".charAt(0x0F & bytes[i] >> 4) + "0123456789ABCDEF".charAt(bytes[i] & 0x0F)) + " ");

                if (i % 16 == 15 || i == bytes.length - 1) {
                    result = result.concat("\n");
                }
            }
        }

        LOGGER.info(result);
    }

    /**
     * show list
     *
     * @param inputList input list
     * @param <T> type of element in list
     */
    public static <T> void showList(List<T> inputList) {
        showList(inputList, null);
    }

    /**
     * show list
     *
     * @param inputList input list
     * @param title title will be shown
     * @param <T> type of element in list
     */
    public static <T> void showList(List<T> inputList, String title) {

        String result = "";

        title = (title != null && !title.isBlank()) ? title : "inputList";
        if (inputList == null) {
            result = result.concat("\n" + title + " -> null\n");
        } else {
            result = result.concat("\n" + title + " (" + inputList.size() + ") = [\n");
            for (int i = 0; i < inputList.size(); i++) {
                String separator = (i != inputList.size() - 1) ? "," : "";
                result = result.concat("\t" + inputList.get(i).toString() + separator + "\n");
            }
            result = result.concat("]\n");
        }

        LOGGER.info(result);
    }

    /**
     * show list
     *
     * @param inputList input list
     * @param title title will be shown
     * @param unitLength unit hex length (byte: 1, short: 2, int: 4)
     * @param <T> type of element in list
     */
    public static <T extends Number> void showListByHex(List<T> inputList, String title, int unitLength) {

        String result = "";

        title = (title != null && !title.isBlank()) ? title : "inputList";
        if (inputList == null) {
            result = result.concat("\n" + title + " -> null\n");
        } else {
            result = result.concat("\n" + title + " (" + inputList.size() + ") = [\n");
            for (int i = 0; i < inputList.size(); i++) {
                String separator = (i != inputList.size() - 1) ? "," : "";
                String element;
                switch (unitLength) {
                    case Byte.BYTES -> element = String.format("%02X", inputList.get(i).byteValue());
                    case Short.BYTES -> element = String.format("%04X", inputList.get(i).shortValue());
                    case Integer.BYTES -> element = String.format("%08X", inputList.get(i).intValue());
                    default -> element = String.valueOf(inputList.get(i));
                }
                result = result.concat("\t" + element + separator + "\n");
            }
            result = result.concat("]\n");
        }

        LOGGER.info(result);
    }

    /**
     * show json
     *
     * @param json json
     */
    public static void showJson(String json) {
        showJson(json, null);
    }

    /**
     * show json
     *
     * @param json json
     * @param title title will be shown
     */
    public static void showJson(String json, String title) {

        String result = "";

        title = (title != null && !title.isBlank()) ? title : "inputJson";
        if (json == null) {
            result = result.concat("\n" + title + " -> null\n");
        } else {
            result = result.concat("\n" + title + " = \n");
            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create();
            String prettyJson = gson.toJson(JsonParser.parseString(json));
            result = result.concat(prettyJson + "\n");
        }

        LOGGER.info(result);
    }

    /**
     * convert hex string to byte array
     *
     * @param hs hex string
     * @return result byte array
     */
    public static byte[] hexStringToByteArray(String hs) {

        int length = hs.length();
        byte[] result = new byte[length/2];

        for(int i=0; i<length; i+=2) {
            result[i / 2] = (byte)((Character.digit(hs.charAt(i), 16) << 4) + Character.digit(hs.charAt(i+1), 16));
        }

        return result;
    }

    /**
     * convert integer list to int array
     *
     * @param inputList integer list
     * @return int array
     */
    public static int[] integerListToIntArray(List<Integer> inputList) {
        return inputList.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * covert int array to integer list
     *
     * @param inputs int array
     * @return integer list
     */
    public static List<Integer> intArrayToIntegerList(int[] inputs) {
        return Arrays.stream(inputs).boxed().collect(Collectors.toList());
    }

    /**
     * concatenate multiple byte array in order
     *
     * @param bytes multiple byte array
     * @return concatenated byte array
     */
    public static byte[] concatBytes(byte[]... bytes) {

        if (bytes != null) {
            int length = Arrays.stream(bytes).filter(Objects::nonNull).mapToInt(s -> s.length).sum();

            ByteBuffer byteBuffer = ByteBuffer.allocate(length);
            Arrays.stream(bytes).filter(Objects::nonNull).forEach(byteBuffer::put);

            return byteBuffer.array();
        } else {
            return null;
        }
    }

    /**
     * convert short value to byte array
     *
     * @param value short value (2 bytes)
     * @return byte array
     */
    public static byte[] shortToBytes(short value) {

        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.putShort(value);

        return bb.array();
    }

    /**
     * convert int value to byte array
     *
     * @param value int value (4 bytes)
     * @return byte array
     */
    public static byte[] intToBytes(int value) {

        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(value);

        return bb.array();
    }

    /**
     * convert byte array to short array
     *
     * @param values byte array
     * @return short array
     */
    public static int[] bytesToShorts(byte[] values) {

        ByteBuffer bb = ByteBuffer.allocate(values.length);
        bb.put(values);
        bb.rewind();

        int[] result = new int[values.length / 2];
        for (int i=0; i<result.length; i++) {
            result[i] = bb.getShort();
        }

        return result;
    }

    /**
     * convert short array to byte array
     *
     * @param values short array
     * @return byte array
     */
    public static byte[] shortsToBytes(int[] values) {

        ByteBuffer bb = ByteBuffer.allocate(values.length * 2);

        for (int value : values) {
            bb.putShort((short) value);
        }

        return bb.array();
    }
}
