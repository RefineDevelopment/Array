package me.array.ArrayPractice.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigurationSerializableTypeAdapter implements JsonDeserializer<ConfigurationSerializable>, JsonSerializer<ConfigurationSerializable> {
    private static final Type CONFIGURATIONSERIALIZABLE_SERIALIZED_TYPE = new TypeToken<LinkedHashMap<String, Object>>() {
        private static final long serialVersionUID = 1L;
    }.getType();

    private static ConfigurationSerializable deserializeObject(Map<String, Object> deserialized) throws ReflectiveOperationException {
        LinkedHashMap<String, Object> conversion = new LinkedHashMap<>(deserialized.size());
        for (Map.Entry<String, Object> entry : deserialized.entrySet()) {
            if (entry.getValue() instanceof Map && ((Map) (entry.getValue())).containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                Map<?, ?> raw = (Map<?, ?>) entry.getValue();
                Map<String, Object> typed = new LinkedHashMap<String, Object>(raw.size());
                for (Map.Entry<?, ?> child : raw.entrySet()) {
                    typed.put(child.getKey().toString(), fixObject(child.getKey().toString(), child.getValue()));
                }
                conversion.put(entry.getKey(), deserializeObject(typed));
            } else {
                conversion.put(entry.getKey(), fixObject(entry.getKey(), entry.getValue()));
            }
        }
        return ConfigurationSerialization.deserializeObject(conversion);
    }

    private static Object fixObject(String key, Object object) {
        if (object instanceof Double) {
            return ((Double) object).intValue();
        }
        return object;
    }

    @Override
    public ConfigurationSerializable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            LinkedHashMap<String, Object> deserialized = context.deserialize(json, CONFIGURATIONSERIALIZABLE_SERIALIZED_TYPE);
            if (deserialized.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                return deserializeObject(deserialized);
            } else {
                Method method = ((Class<?>) typeOfT).getDeclaredMethod("deserialize", Map.class);
                return (ConfigurationSerializable) method.invoke(null, deserialized);
            }
        } catch (Exception e) {
            System.out.println("Currently In Block " + json.toString());
            throw new JsonParseException("Could not deserialize " + typeOfT + ".", e);
        }
    }

    @Override
    public JsonElement serialize(ConfigurationSerializable src, Type typeOfSrc, JsonSerializationContext context) {
        Map<String, Object> values = new LinkedHashMap<String, Object>();
        values.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(src.getClass()));
        values.putAll(src.serialize());
        return context.serialize(values);
    }
}
