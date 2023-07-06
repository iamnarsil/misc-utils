package com.narsil.misc.serialize;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.narsil.misc.JsonUtils;

import java.lang.reflect.Type;

/**
 * enumeration serialization type adapter
 *
 * @author iamnarsil
 * @version 20230705
 * @since 20230328
 */
public class EnumSerializationTypeAdapter<T extends Enum<T>> implements JsonSerializer<T>, JsonDeserializer<T> {

    private final EnumSerialization<T> enumSerialization;

    public EnumSerializationTypeAdapter(EnumSerialization<T> enumSerialization) {
        this.enumSerialization = enumSerialization;
    }

    @Override public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        if (jsonElement != null) {
            return enumSerialization.deserialize(jsonElement);
        }

        return null;
    }

    @Override public JsonElement serialize(T t, Type type, JsonSerializationContext jsonSerializationContext) {

        if (t instanceof EnumSerialization) {
            return JsonUtils.jsToJe(((EnumSerialization<?>)t).serialize());
        }

        return null;
    }
}
