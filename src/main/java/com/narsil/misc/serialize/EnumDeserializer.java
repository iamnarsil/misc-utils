package com.narsil.misc.serialize;

import com.google.gson.JsonElement;

/**
 * enumeration deserializier
 *
 * @author iamnarsil
 * @version 20230705
 * @since 20230328
 */
public interface EnumDeserializer<T extends Enum<T>> {

    T deserialize(JsonElement value);
}
