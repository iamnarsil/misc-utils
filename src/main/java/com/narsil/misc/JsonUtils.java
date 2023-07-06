package com.narsil.misc;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * json operation toolkit
 * <p>
 * supports the conversion of several data type,
 * including Json-format string (JS), Google Json element (JE), Java Map (Map) and serializable value object (VO).
 *
 * @author iamnarsil
 * @version 20230705
 * @since 20230328
 */
public class JsonUtils {

    private static final Logger LOGGER = Logger.getLogger("JsonUtils");

    /**
     * check json is empty or not
     *
     * @param jsonString json string
     * @return input json is empty or not
     */
    public static boolean isEmptyJson(String jsonString) {

        if (jsonString != null && !jsonString.trim().isEmpty()) {
            JsonElement jsonElement = jsToJe(jsonString);
            if (jsonElement != null && !jsonElement.isJsonNull()) {
                try {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    return (jsonObject.size() == 0);
                } catch (Exception e) {
                    LOGGER.severe(e.getMessage());
                }
            }
        }

        return true;
    }

    /**
     * value object -> json string
     *
     * @param vo value object
     * @param typeOfT class type of value object (ex: ValueObject.class)
     * @return json string
     */
    public static String voToJs(Object vo, Type typeOfT) {
        return voToJs(vo, typeOfT, null, null);
    }

    /**
     * value object -> json string (with policy & strategy)
     *
     * @param vo value object
     * @param typeOfT class type of value object (ex: ValueObject.class)
     * @param policy field naming policy
     * @param strategy exclusion strategy
     * @return json string
     */
    public static String voToJs(Object vo, Type typeOfT, FieldNamingPolicy policy, ExclusionStrategy strategy) {

        GsonBuilder gsonBuilder = new GsonBuilder()
                .disableHtmlEscaping()
                .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        if (policy != null) {
            gsonBuilder.setFieldNamingPolicy(policy);
        }

        if (strategy != null) {
            gsonBuilder.addSerializationExclusionStrategy(strategy);
        }

        Gson gson = gsonBuilder.create();
        return gson.toJson(vo, typeOfT);
    }

    /**
     * value object -> google json object
     *
     * @param vo value object
     * @param typeOfT class type of value object (ex: ValueObject.class)
     * @return google json object
     */
    public static JsonElement voToJe(Object vo, Type typeOfT) {

        GsonBuilder gsonBuilder = new GsonBuilder()
                .disableHtmlEscaping()
                .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        Gson gson = gsonBuilder.create();
        return gson.toJsonTree(vo, typeOfT);
    }

    /**
     * value object -> map
     *
     * @param vo value object
     * @param typeOfT class type of value object (ex: ValueObject.class)
     * @return map
     */
    public static Map<String, Object> voToMap(Object vo, Type typeOfT) {

        // value object -> json string
        String jsonString = voToJs(vo, typeOfT);

        // use LinkedHashMap to keep order
        Type typeOfGeneric = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();

        // json string -> map
        return jsToVo(jsonString, typeOfGeneric);
    }

    /**
     * json string -> value object
     *
     * @param jsonString json string
     * @param typeOfT class type of value object (ex: ValueObject.class)
     * @param <T> value object
     * @return value object
     */
    public static <T> T jsToVo(String jsonString, Type typeOfT) {
        return jsToVo(jsonString, typeOfT, null);
    }

    /**
     * json string -> value object (with policy & strategy)
     *
     * @param jsonString json string
     * @param typeOfT class type of value object (ex: ValueObject.class)
     * @param <T> value object
     * @param policy field naming policy
     * @return value object
     */
    public static <T> T jsToVo(String jsonString, Type typeOfT, FieldNamingPolicy policy) {

        GsonBuilder gsonBuilder = new GsonBuilder()
                .disableHtmlEscaping()
                .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        if (policy != null) {
            gsonBuilder.setFieldNamingPolicy(policy);
        }

        try {
            Gson gson = gsonBuilder.create();
            return gson.fromJson(jsonString, typeOfT);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }

        return null;
    }

    /**
     * json string -> google json object
     *
     * @param jsonString json string
     * @return google json object
     */
    public static JsonElement jsToJe(String jsonString) {
        try {
            return JsonParser.parseString(jsonString);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }

        return null;
    }

    /**
     * json string -> map
     *
     * @param jsonString json string
     * @return map
     */
    public static Map<String, Object> jsToMap(String jsonString) {

        // use LinkedHashMap to keep order
        Type typeOfGeneric = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();

        // json string -> map
        return jsToVo(jsonString, typeOfGeneric);
    }

    /**
     * google json object -> value object
     *
     * @param jsonElement google json object
     * @param typeOfT class type of value object (ex: ValueObject.class)
     * @param <T> value object
     * @return value object
     */
    public static <T> T jeToVo(JsonElement jsonElement, Type typeOfT) {

        GsonBuilder gsonBuilder = new GsonBuilder()
                .disableHtmlEscaping()
                .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        try {
            Gson gson = gsonBuilder.create();
            return gson.fromJson(jsonElement, typeOfT);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }

        return null;
    }

    /**
     * google json object -> json string
     *
     * @param jsonElement google json object
     * @return json string
     */
    public static String jeToJs(JsonElement jsonElement) {
        return jsonElement.toString();
    }

    /**
     * google json object -> map
     *
     * @param jsonElement google json object
     * @return map
     */
    public static Map<String, Object> jeToMap(JsonElement jsonElement) {

        GsonBuilder gsonBuilder = new GsonBuilder()
                .disableHtmlEscaping()
                .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        // use LinkedHashMap to keep order
        Type typeOfGeneric = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();

        try {
            Gson gson = gsonBuilder.create();
            return gson.fromJson(jsonElement, typeOfGeneric);
        }  catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }

        return null;
    }

    /**
     * map -> value object
     *
     * @param map map
     * @param typeOfT class type of value object (ex: ValueObject.class)
     * @param <T> value object
     * @return value object
     */
    public static <T> T mapToVo(Map<String, Object> map, Type typeOfT) {

        // use LinkedHashMap to keep order
        Type typeOfGeneric = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();

        // map -> json string
        String jsonString = voToJs(map, typeOfGeneric);

        // json string -> value object
        return jsToVo(jsonString, typeOfT);
    }

    /**
     * map -> json string
     *
     * @param map map
     * @return json string
     */
    public static String mapToJs(Map<String, Object> map) {

        // use LinkedHashMap to keep order
        Type typeOfGeneric = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();

        // use TreeMap to guarantee natural ordering of keys
        TreeMap<String, Object> treeMap = new TreeMap<>(map);

        // map -> json string
        return voToJs(treeMap, typeOfGeneric);
    }

    /**
     * map -> google json object
     *
     * @param map map
     * @return google json object
     */
    public static JsonElement mapToJe(Map<String, Object> map) {

        GsonBuilder gsonBuilder = new GsonBuilder()
                .disableHtmlEscaping()
                .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        // use LinkedHashMap to keep order
        Type typeOfGeneric = new TypeToken<LinkedHashMap<String, Object>>(){}.getType();

        // use TreeMap to guarantee natural ordering of keys
        TreeMap<String, Object> treeMap = new TreeMap<>(map);

        Gson gson = gsonBuilder.create();
        return gson.toJsonTree(treeMap, typeOfGeneric);
    }

    /**
     * file -> value object
     *
     * @param file file
     * @param typeOfT class type of value object (ex: ValueObject.class)
     * @param <T> value object
     * @return value object
     */
    public static <T> T fileToVo(File file, Type typeOfT) {

        GsonBuilder gsonBuilder = new GsonBuilder()
                .disableHtmlEscaping()
                .setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        try (FileReader reader = new FileReader(file)) {
            Gson gson = gsonBuilder.create();
            return gson.fromJson(reader, typeOfT);
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
        }

        return null;
    }

    /**
     * file -> json string
     *
     * @param file file
     * @param typeOfT class type of value object (ex: ValueObject.class)
     * @param <T> value object
     * @return json string
     */
    public static <T> String fileToJs(File file, Type typeOfT) {

        // file -> value object
        T vo = fileToVo(file, typeOfT);

        // value object -> json string
        return (vo != null) ? voToJs(vo, typeOfT) : null;
    }

    /**
     * file -> google json object
     *
     * @param file file
     * @param typeOfT class type of value object (ex: ValueObject.class)
     * @param <T> value object
     * @return google json object
     */
    public static <T> JsonElement fileToJe(File file, Type typeOfT) {

        // file -> value object
        T vo = fileToVo(file, typeOfT);

        // value object -> google json object
        return (vo != null) ? voToJe(vo, typeOfT) : null;
    }

    /**
     * file -> map
     *
     * @param file file
     * @param typeOfT class type of value object (ex: ValueObject.class)
     * @param <T> value object
     * @return map
     */
    public static <T> Map<String, Object> fileToMap(File file, Type typeOfT) {

        // file -> value object
        T vo = fileToVo(file, typeOfT);

        // value object -> map
        return (vo != null) ? voToMap(vo, typeOfT) : null;
    }
}
