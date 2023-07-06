package com.narsil.misc;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JsonUtilsTest {

    private static final Logger LOGGER = Logger.getLogger("JsonUtilsTest");

    private Type typeOfT = null;
    private SampleTemplate vo = null;
    private String jsonString = null;
    private JsonElement jsonElement = null;
    private final Map<String, Object> map = new HashMap<>();
    private File file = null;
    private FieldNamingPolicy policy;
    private ExclusionStrategy strategy;
    private String jsonString2 = null;

    @Before
    public void init() {

        typeOfT = SampleTemplate.class;
        vo = new SampleTemplate(9999, "this is a message", new Date());
        jsonString = "{\"alphaCode\":9999,\"betaMessage\":\"this is a message\", \"gammaDate\":\"2023-06-15 06:05:11.143\"}";
        // jsonString = "123";
        jsonElement = JsonParser.parseString(jsonString);

        // put value by arbitrary ordering
        map.put("gammaDate", new Date());
        map.put("alphaCode", 9999);
        map.put("betaMessage", "this is a message");

        try {
            URL fileUrl  = getClass().getClassLoader().getResource("test.json");
            if (fileUrl != null) {
                file = new File(fileUrl.toURI());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        policy = FieldNamingPolicy.UPPER_CASE_WITH_UNDERSCORES;
        strategy = new ExclusionStrategy() {

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }

            @Override
            public boolean shouldSkipField(FieldAttributes field) {
                // filter "errorMessage"
                return field.getDeclaringClass() == SampleTemplate.class && field.getName().equals("errorMessage");
            }
        };

        jsonString2 = "{\"ALPHA_CODE\":9999,\"BETA_MESSAGE\":\"this is a message\", \"GAMMA_INFO\":\"no other info\"}";
    }

    @Test
    public void test00_isEmptyJson() {

        boolean isEmpty = JsonUtils.isEmptyJson(jsonString);
        LOGGER.info(String.valueOf(isEmpty));
    }

    @Test
    public void test01_voToJs_A() {

        String jsonString = JsonUtils.voToJs(vo, typeOfT);
        LOGGER.info(jsonString);
    }

    @Test
    public void test01_voToJs_B() {

        String jsonString = JsonUtils.voToJs(vo, typeOfT, policy, null);
        LOGGER.info(jsonString);
    }

    @Test
    public void test01_voToJs_C() {

        String jsonString = JsonUtils.voToJs(vo, typeOfT, null, strategy);
        LOGGER.info(jsonString);
    }

    @Test
    public void test01_voToJs_D() {

        String jsonString = JsonUtils.voToJs(vo, typeOfT, policy, strategy);
        LOGGER.info(jsonString);
    }

    @Test
    public void test02_voToJe() {

        JsonElement jsonElement = JsonUtils.voToJe(vo, typeOfT);
        LOGGER.info(jsonElement.toString());
    }

    @Test
    public void test03_voToMap() {

        Map<String, Object> map = JsonUtils.voToMap(vo, typeOfT);
        map.forEach((k, v) -> LOGGER.info(k + " = " + v));
    }

    @Test
    public void test04_jsToVo_A() {

        SampleTemplate sampleTemplate = JsonUtils.jsToVo(jsonString, typeOfT);
        LOGGER.info(sampleTemplate != null ? sampleTemplate.toJson() : null);
    }

    @Test
    public void test04_jsToVo_B() {

        SampleTemplate sampleTemplate = JsonUtils.jsToVo(jsonString, typeOfT, policy);
        LOGGER.info(sampleTemplate != null ? sampleTemplate.toJson() : null);
    }

    @Test
    public void test04_jsToVo_C() {

        SampleTemplate sampleTemplate = JsonUtils.jsToVo(jsonString2, typeOfT);
        LOGGER.info(sampleTemplate != null ? sampleTemplate.toJson() : null);
    }

    @Test
    public void test04_jsToVo_D() {

        SampleTemplate sampleTemplate = JsonUtils.jsToVo(jsonString2, typeOfT, policy);
        LOGGER.info(sampleTemplate != null ? sampleTemplate.toJson() : null);
    }

    @Test
    public void test05_jsToJe() {

        JsonElement jsonElement = JsonUtils.jsToJe(jsonString);
        LOGGER.info(jsonElement != null ? jsonElement.toString() : null);
    }

    @Test
    public void test06_jsToMap() {

        Map<String, Object> map = JsonUtils.jsToMap(jsonString);
        if (map != null) {
            map.forEach((k, v) -> LOGGER.info(k + " = " + v));
        } else {
            LOGGER.info("map is null");
        }
    }

    @Test
    public void test07_jeToVo() {

        SampleTemplate vo = JsonUtils.jeToVo(jsonElement, typeOfT);
        LOGGER.info(vo != null ? vo.toJson() : null);
    }

    @Test
    public void test08_jeToJs() {

        String jsonString = JsonUtils.jeToJs(jsonElement);
        LOGGER.info(jsonString);
    }

    @Test
    public void test09_jeToMap() {

        Map<String, Object> map = JsonUtils.jeToMap(jsonElement);
        if (map != null) {
            map.forEach((k, v) -> LOGGER.info(k + " = " + v));
        } else {
            LOGGER.info("map is null");
        }
    }

    @Test
    public void test10_mapToVo() {

        SampleTemplate vo = JsonUtils.mapToVo(map, typeOfT);
        LOGGER.info(vo.toJson());
    }

    @Test
    public void test11_mapToJs() {

        String jsonString = JsonUtils.mapToJs(map);
        LOGGER.info(jsonString);
    }

    @Test
    public void test12_mapToJe() {

        JsonElement jsonElement = JsonUtils.mapToJe(map);
        LOGGER.info(jsonElement.toString());
    }

    @Test
    public void test13_fileToVo() {

        SampleTemplate vo = JsonUtils.fileToVo(file, typeOfT);

        if (vo != null) {
            LOGGER.info(vo.toJson());
        }
    }

    @Test
    public void test14_fileToJs() {

        String jsonString = JsonUtils.fileToJs(file, typeOfT);
        LOGGER.info(jsonString);
    }

    @Test
    public void test15_fileToJe() {

        JsonElement jsonElement = JsonUtils.fileToJe(file, typeOfT);

        if (jsonElement != null) {
            LOGGER.info(jsonElement.toString());
        }
    }

    @Test
    public void test16_fileToMap() {

        Map<String, Object> map = JsonUtils.fileToMap(file, typeOfT);

        if (map != null) {
            map.forEach((k, v) -> LOGGER.info(k + " = " + v));
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