package com.narsil.misc.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.logging.Logger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EnumSerializationTypeAdapterTest {

    private static final Logger LOGGER = Logger.getLogger("EnumSerializationTypeAdapterTest");

    private GameRole role;
    private String id;
    private String json;

    @Before
    public void init() {
        role = GameRole.MAGE;
        id = "03";
        json = "{\"id\":\"03\",\"type\":\"magic\",\"skill\":\"fire ball\"}";
    }

    @Test
    public void test00_getId() {
        LOGGER.info(role + " id = " + role.getId());
    }

    @Test
    public void test01_getById() {
        LOGGER.info("id (" + id + ") -> " + GameRole.getById(id));
    }

    @Test
    public void test02_toJson() {
        LOGGER.info(role + " = " + role.toJson());
    }

    @Test
    public void test03_fromJson() {
        GameRole gameRole = GameRole.fromJson(json);
        LOGGER.info(json + " -> " + gameRole);
    }

    public enum GameRole implements EnumSerialization<GameRole> {

        KNIGHT ("01", "melee", "berserk"),
        ARCHER ("02", "ranged", "guided arrow"),
        MAGE ("03", "magic", "fire ball");

        private final String id;
        private final String type;
        private final String skill;

        GameRole(String id, String type, String skill) {
            this.id = id;
            this.type = type;
            this.skill = skill;
        }

        public String getId() {
            return id;
        }

        public String toJson() {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(this.getClass(),
                            new EnumSerializationTypeAdapter<>(this))
                    .create();

            return gson.toJson(this, this.getClass());
        }

        public static GameRole fromJson(String jsonString) {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(GameRole.class,
                            new EnumSerializationTypeAdapter<>(GameRole.KNIGHT))
                    .create();

            return gson.fromJson(jsonString, GameRole.class);
        }

        public static GameRole getById(String id) {

            GameRole[] values = GameRole.values();
            for (GameRole role : values) {
                if (role.getId().equalsIgnoreCase(id)) {
                    return role;
                }
            }

            return null;
        }

        @Override
        public GameRole deserialize(JsonElement jsonElement) {

            if (jsonElement.isJsonObject()) {

                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String id = jsonObject.get("id").getAsString();

                if (id != null) {
                    return getById(id);
                }
            }

            return null;
        }

        @Override
        public String serialize() {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", id);
            jsonObject.addProperty("type", type);
            jsonObject.addProperty("skill", skill);

            return jsonObject.toString();
        }
    }
}