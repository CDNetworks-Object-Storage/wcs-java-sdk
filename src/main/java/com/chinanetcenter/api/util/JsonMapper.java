/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.chinanetcenter.api.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Simply encapsulate Jackson to implement a Mapper for JSON String <-> Java Object.
 * <p>
 * Encapsulates different output styles, using different builder functions to create instances.
 *
 * @author calvin
 */
public class JsonMapper {

    private ObjectMapper mapper;

    public JsonMapper() {
        this(null);
    }

    public JsonMapper(Include include) {
        mapper = new ObjectMapper();
        // Set the style for including attributes when outputting.
        if (include != null) {
            mapper.setSerializationInclusion(include);
        }
        // When setting input, ignore properties that exist in the JSON string but not in the Java object.
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Create a Mapper that only outputs non-null and non-empty (e.g., List.isEmpty) properties to a JSON string. It is recommended to use it in external interfaces.
     */
    public static JsonMapper nonEmptyMapper() {
        return new JsonMapper(Include.NON_EMPTY);
    }

    /**
     * Create a Mapper that outputs only properties whose initial values have been changed to a JSON string, using the most economical storage method. It is recommended for use in internal interfaces.
     */
    public static JsonMapper nonDefaultMapper() {
        return new JsonMapper(Include.NON_DEFAULT);
    }

    /**
     * The object can be a POJO, a Collection, or an array. If the object is null, "null" is returned. If the collection is empty, "[]" is returned.
     */
    public String toJson(Object object) {

        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Deserialize POJO or simple Collection like List&lt;String&gt;.
     * <p>
     * If the JSON string is null or the string "null", return null. If the collection is an empty collection, return "[]".
     * <p>
     * To deserialize complex Collections like List&lt;MyBean&gt;, please use fromJson(String, JavaType).
     *
     * @see #fromJson(String, com.fasterxml.jackson.databind.JavaType)
     */
    public <T> T fromJson(String jsonString, Class<T> clazz) {
        if (StringUtil.isEmpty(jsonString)) {
            return null;
        }

        try {
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Deserializing complex Collections like List&lt;Bean&gt;,
     * First, use `createCollectionType()` or `constructMapType()` to construct the type, then call this function.
     */
    @SuppressWarnings("unchecked")
    public <T> T fromJson(String jsonString, JavaType javaType) {
        if (StringUtil.isEmpty(jsonString)) {
            return null;
        }

        try {
            return (T) mapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Construct Collection type.
     */
    @SuppressWarnings("rawtypes")
    public JavaType contructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    /**
     * Construct Map type.
     */
    @SuppressWarnings("rawtypes")
    public JavaType contructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }

    /**
     * When a JSON only contains some properties of a Bean, update an existing Bean by only overwriting those properties.
     */
    public void update(String jsonString, Object object) {
        try {
            mapper.readerForUpdating(object).readValue(jsonString);
        } catch (JsonProcessingException e) {
        } catch (IOException e) {
        }
    }

    /**
     * Output JSONP format data.
     */
    public String toJsonP(String functionName, Object object) {
        return toJson(new JSONPObject(functionName, object));
    }

    /**
     * Configure whether to use the Enum's `toString()` function to read and write Enums. When set to `False`, the Enum's `name()` function will be used to read and write Enums. The default is `False`.
     * Note: This function must be called after the Mapper is created and before all read and write operations.
     */
    public void enableEnumUseToString() {
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }

    /**
     * Support using JAXB annotations, so that POJO annotations are not coupled with Jackson.
     * The default behavior is to first look for JAXB annotations; if not found, then Jackson annotations will be sought.
     */
    public void enableJaxbAnnotation() {
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        mapper.registerModule(module);
    }

    /**
     * Retrieve the Mapper for further settings or use other serialization APIs.
     */
    public ObjectMapper getMapper() {
        return mapper;
    }
}
