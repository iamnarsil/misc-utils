package com.narsil.misc.serialize;

/**
 * enumeration serialization
 *
 * @author iamnarsil
 * @version 20230705
 * @since 20230328
 */
public interface EnumSerialization<T extends Enum<T>> extends EnumSerializer, EnumDeserializer<T> {

}
