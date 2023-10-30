package com.narsil.misc;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JsonUtilsTest {

    private static final Logger LOGGER = Logger.getLogger("JsonUtilsTest");

    private Type typeOfT = null;
    private SampleTemplate vo = null;
    private String jsonString = null;
    private JsonElement jsonElement = null;
    private final Map<String, Object> map = new HashMap<>();
    private File file = null;
    private FieldNamingPolicy fnPolicy;
    private FieldNamingStrategy fnStrategy;
    private ExclusionStrategy exStrategy;
    private String jsonStringB = null;
    private String jsonStringC = null;
    private String jsonStringD = null;
    private String jsonStringE = null;
    private String jsonStringEx = null;
    private String jsonStringInit = null;

    @Before
    public void init() {

        try {
            typeOfT = SampleTemplate.class;
            vo = new SampleTemplate(
                    9999,
                    "this is a message",
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2023-06-15 06:05:11.143"));
            jsonString = "{\"alphaCode\":9999,\"betaMessage\":\"this is a message\",\"gammaDate\":\"2023-06-15 06:05:11.143\"}";
            jsonElement = JsonParser.parseString(jsonString);

            // put value by arbitrary ordering
            map.put("gammaDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse("2023-06-15 06:05:11.143"));
            map.put("alphaCode", 9999);
            map.put("betaMessage", "this is a message");

            try {
                URL fileUrl = getClass().getClassLoader().getResource("test.json");
                if (fileUrl != null) {
                    file = new File(fileUrl.toURI());
                }
            } catch (Exception e) {
                LOGGER.info(e.getMessage());
            }

            fnPolicy = FieldNamingPolicy.UPPER_CASE_WITH_UNDERSCORES;
            fnStrategy = field -> {
                if (field.getDeclaringClass() == SampleTemplate.class && field.getName().equals("gammaDate")) {
                    return "deltaDate";
                } else {
                    return field.getName();
                }
            };
            exStrategy = new ExclusionStrategy() {

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }

                @Override
                public boolean shouldSkipField(FieldAttributes field) {
                    // skip "gammaDate"
                    return field.getDeclaringClass() == SampleTemplate.class && field.getName().equals("gammaDate");
                }
            };

            jsonStringB = "{\"ALPHA_CODE\":9999,\"BETA_MESSAGE\":\"this is a message\",\"GAMMA_DATE\":\"2023-06-15 06:05:11.143\"}";
            jsonStringC = "{\"alphaCode\":9999,\"betaMessage\":\"this is a message\",\"deltaDate\":\"2023-06-15 06:05:11.143\"}";
            jsonStringD = "{\"alphaCode\":9999,\"betaMessage\":\"this is a message\"}";
            jsonStringE = "{\"ALPHA_CODE\":9999,\"BETA_MESSAGE\":\"this is a message\"}";
            jsonStringEx = "{\"ALPHA_CODE\":9999,\"BETA_MESSAGE\":\"this is a message\",\"GAMMA_INFO\":\"no other info\"}";
            jsonStringInit = "{\"alphaCode\":-1}";

        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }
    }

    @Test
    public void test00_isEmptyJson() {

        boolean isEmpty = JsonUtils.isEmptyJson(jsonString);
        LOGGER.info(String.valueOf(isEmpty));
        assertFalse(isEmpty);
    }

    @Test
    public void test01_voToJs_A() {

        String jsonString = JsonUtils.voToJs(vo, typeOfT);
        LOGGER.info(jsonString);
        assertEquals(this.jsonString, jsonString);
    }

    @Test
    public void test01_voToJs_B() {

        String jsonString = JsonUtils.voToJs(vo, typeOfT, fnPolicy, null);
        LOGGER.info(jsonString);
        assertEquals(jsonStringB, jsonString);
    }

    @Test
    public void test01_voToJs_C() {

        String jsonString = JsonUtils.voToJs(vo, typeOfT, fnStrategy, null);
        LOGGER.info(jsonString);
        assertEquals(jsonStringC, jsonString);
    }

    @Test
    public void test01_voToJs_D() {

        String jsonString = JsonUtils.voToJs(vo, typeOfT, null, exStrategy);
        LOGGER.info(jsonString);
        assertEquals(jsonStringD, jsonString);
    }

    @Test
    public void test01_voToJs_E() {

        String jsonString = JsonUtils.voToJs(vo, typeOfT, fnPolicy, exStrategy);
        LOGGER.info(jsonString);
        assertEquals(jsonStringE, jsonString);
    }

    @Test
    public void test02_voToJe_A() {

        JsonElement jsonElement = JsonUtils.voToJe(vo, typeOfT);
        LOGGER.info(jsonElement.toString());
        assertEquals(this.jsonString, jsonElement.toString());
    }

    @Test
    public void test02_voToJe_B() {

        JsonElement jsonElement = JsonUtils.voToJe(vo, typeOfT, fnPolicy, null);
        LOGGER.info(jsonElement.toString());
        assertEquals(this.jsonStringB, jsonElement.toString());
    }

    @Test
    public void test02_voToJe_C() {

        JsonElement jsonElement = JsonUtils.voToJe(vo, typeOfT, fnStrategy, null);
        LOGGER.info(jsonElement.toString());
        assertEquals(this.jsonStringC, jsonElement.toString());
    }

    @Test
    public void test02_voToJe_D() {

        JsonElement jsonElement = JsonUtils.voToJe(vo, typeOfT, null, exStrategy);
        LOGGER.info(jsonElement.toString());
        assertEquals(this.jsonStringD, jsonElement.toString());
    }

    @Test
    public void test02_voToJe_E() {

        JsonElement jsonElement = JsonUtils.voToJe(vo, typeOfT, fnPolicy, exStrategy);
        LOGGER.info(jsonElement.toString());
        assertEquals(this.jsonStringE, jsonElement.toString());
    }

    @Test
    public void test03_voToMap() {

        Map<String, Object> map = JsonUtils.voToMap(vo, typeOfT);
        assertEquals(3, map.size());
        map.forEach((k, v) -> {
            LOGGER.info(k + " = " + v);
            switch (k) {
                case "alphaCode" -> assertEquals(9999.0, Double.parseDouble(v.toString()), 0.0);
                case "betaMessage" -> assertEquals("this is a message", v.toString());
                case "gammaDate" -> assertEquals("2023-06-15 06:05:11.143", v.toString());
            }
        });
    }

    @Test
    public void test04_jsToVo_A() {

        SampleTemplate sampleTemplate = JsonUtils.jsToVo(jsonString, typeOfT);
        LOGGER.info(sampleTemplate != null ? sampleTemplate.toJson() : null);
        assert sampleTemplate != null;
        assertEquals(jsonString, sampleTemplate.toJson());
    }

    @Test
    public void test04_jsToVo_B() {

        SampleTemplate sampleTemplate = JsonUtils.jsToVo(jsonStringB, typeOfT, fnPolicy, null);
        LOGGER.info(sampleTemplate != null ? sampleTemplate.toJson() : null);
        assert sampleTemplate != null;
        assertEquals(jsonString, sampleTemplate.toJson());
    }

    @Test
    public void test04_jsToVo_C() {

        SampleTemplate sampleTemplate = JsonUtils.jsToVo(jsonStringC, typeOfT, fnStrategy, null);
        LOGGER.info(sampleTemplate != null ? sampleTemplate.toJson() : null);
        assert sampleTemplate != null;
        assertEquals(jsonString, sampleTemplate.toJson());
    }

    @Test
    public void test04_jsToVo_D() {

        SampleTemplate sampleTemplate = JsonUtils.jsToVo(jsonString, typeOfT, null, exStrategy);
        LOGGER.info(sampleTemplate != null ? sampleTemplate.toJson() : null);
        assert sampleTemplate != null;
        assertEquals(jsonString, sampleTemplate.toJson());
    }

    @Test
    public void test04_jsToVo_E() {

        SampleTemplate sampleTemplate = JsonUtils.jsToVo(jsonStringB, typeOfT, fnPolicy, exStrategy);
        LOGGER.info(sampleTemplate != null ? sampleTemplate.toJson() : null);
        assert sampleTemplate != null;
        assertEquals(jsonString, sampleTemplate.toJson());
    }

    @Test
    public void test05_jsToJe() {

        JsonElement jsonElement = JsonUtils.jsToJe(jsonString);
        LOGGER.info(jsonElement != null ? jsonElement.toString() : null);
        assert jsonElement != null;
        assertEquals(jsonString, jsonElement.toString());
    }

    @Test
    public void test06_jsToMap() {

        Map<String, Object> map = JsonUtils.jsToMap(jsonString);
        if (map != null) {
            map.forEach((k, v) -> {
                LOGGER.info(k + " = " + v);
                switch (k) {
                    case "alphaCode" -> assertEquals(9999.0, Double.parseDouble(v.toString()), 0.0);
                    case "betaMessage" -> assertEquals("this is a message", v.toString());
                    case "gammaDate" -> assertEquals("2023-06-15 06:05:11.143", v.toString());
                }
            });
        } else {
            LOGGER.info("map is null");
        }
    }

    @Test
    public void test07_jeToVo_A() {

        SampleTemplate vo = JsonUtils.jeToVo(jsonElement, typeOfT);
        LOGGER.info(vo != null ? vo.toJson() : null);
        assert vo != null;
        assertEquals(jsonElement.toString(), vo.toJson());
    }

    @Test
    public void test07_jeToVo_B() {

        SampleTemplate vo = JsonUtils.jeToVo(jsonElement, typeOfT, fnPolicy, null);
        LOGGER.info(vo != null ? vo.toJson() : null);
        assert vo != null;
        assertEquals(jsonStringInit, vo.toJson());
    }

    @Test
    public void test07_jeToVo_C() {

        SampleTemplate vo = JsonUtils.jeToVo(jsonElement, typeOfT, fnStrategy, null);
        LOGGER.info(vo != null ? vo.toJson() : null);
        assert vo != null;
        assertEquals(jsonStringD, vo.toJson());
    }

    @Test
    public void test07_jeToVo_D() {

        SampleTemplate vo = JsonUtils.jeToVo(jsonElement, typeOfT, null, exStrategy);
        LOGGER.info(vo != null ? vo.toJson() : null);
        assert vo != null;
        assertEquals(jsonElement.toString(), vo.toJson());
    }

    @Test
    public void test07_jeToVo_E() {

        SampleTemplate vo = JsonUtils.jeToVo(jsonElement, typeOfT, fnPolicy, exStrategy);
        LOGGER.info(vo != null ? vo.toJson() : null);
        assert vo != null;
        assertEquals(jsonStringInit, vo.toJson());
    }

    @Test
    public void test08_jeToJs() {

        String jsonString = JsonUtils.jeToJs(jsonElement);
        LOGGER.info(jsonString);
        assertEquals(jsonElement.toString(), jsonString);
    }

    @Test
    public void test09_jeToMap() {

        Map<String, Object> map = JsonUtils.jeToMap(jsonElement);
        if (map != null) {
            map.forEach((k, v) -> {
                LOGGER.info(k + " = " + v);
                switch (k) {
                    case "alphaCode" -> assertEquals(9999.0, Double.parseDouble(v.toString()), 0.0);
                    case "betaMessage" -> assertEquals("this is a message", v.toString());
                    case "gammaDate" -> assertEquals("2023-06-15 06:05:11.143", v.toString());
                }
            });
        } else {
            LOGGER.info("map is null");
        }
    }

    @Test
    public void test10_mapToVo() {

        SampleTemplate vo = JsonUtils.mapToVo(map, typeOfT);
        LOGGER.info(vo.toJson());
        map.forEach((k, v) -> {
            switch (k) {
                case "alphaCode" -> assertEquals(Integer.parseInt(v.toString()), vo.getAlphaCode());
                case "betaMessage" -> assertEquals(v.toString(), vo.getBetaMessage());
                case "gammaDate" -> assertEquals(v, vo.gammaDate);
            }
        });
    }

    @Test
    public void test11_mapToJs() {

        String jsonString = JsonUtils.mapToJs(map);
        LOGGER.info(jsonString);
        assertEquals(this.jsonString, jsonString);
    }

    @Test
    public void test12_mapToJe() {

        JsonElement jsonElement = JsonUtils.mapToJe(map);
        LOGGER.info(jsonElement.toString());
        assertEquals(jsonString, jsonElement.toString());
    }

    @Test
    public void test13_fileToVo() {

        SampleTemplate vo = JsonUtils.fileToVo(file, typeOfT);
        if (vo != null) {
            LOGGER.info(vo.toJson());
            assertEquals(jsonString, vo.toJson());
        }
    }

    @Test
    public void test14_fileToJs() {

        String jsonString = JsonUtils.fileToJs(file, typeOfT);
        LOGGER.info(jsonString);
        assertEquals(this.jsonString, jsonString);
    }

    @Test
    public void test15_fileToJe() {

        JsonElement jsonElement = JsonUtils.fileToJe(file, typeOfT);
        if (jsonElement != null) {
            LOGGER.info(jsonElement.toString());
            assertEquals(jsonString, jsonElement.toString());
        }
    }

    @Test
    public void test16_fileToMap() {

        Map<String, Object> map = JsonUtils.fileToMap(file, typeOfT);
        if (map != null) {
            map.forEach((k, v) -> {
                LOGGER.info(k + " = " + v);
                switch (k) {
                    case "alphaCode" -> assertEquals(9999.0, Double.parseDouble(v.toString()), 0.0);
                    case "betaMessage" -> assertEquals("this is a message", v.toString());
                    case "gammaDate" -> assertEquals("2023-06-15 06:05:11.143", v.toString());
                }
            });
        }
    }

    private static class SampleTemplate {

        private int alphaCode = -1;
        private String betaMessage;
        private Date gammaDate;

        public SampleTemplate() {
        }

        public SampleTemplate(int alphaCode, String betaMessage, Date gammaDate) {
            this.alphaCode = alphaCode;
            this.betaMessage = betaMessage;
            this.gammaDate = gammaDate;
        }

        public int getAlphaCode() {
            return alphaCode;
        }

        public void setAlphaCode(int alphaCode) {
            this.alphaCode = alphaCode;
        }

        public String getBetaMessage() {
            return betaMessage;
        }

        public void setBetaMessage(String betaMessage) {
            this.betaMessage = betaMessage;
        }

        public Date getGammaDate() {
            return gammaDate;
        }

        public SampleTemplate setGammaDate(Date gammaDate) {
            this.gammaDate = gammaDate;
            return this;
        }

        public String toJson() {
            return JsonUtils.voToJs(this, this.getClass());
        }
    }
}