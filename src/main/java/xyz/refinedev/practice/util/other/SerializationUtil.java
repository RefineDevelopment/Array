package xyz.refinedev.practice.util.other;

import org.bukkit.Bukkit;
import xyz.refinedev.practice.Array;

import java.util.ArrayList;
import java.util.List;

public class SerializationUtil {

    public static String serialize(Object o) {
        return Array.GSON.toJson(o);
    }

    public static <T> T deserialize(String s, Class<T> clazz) {
        return Array.GSON.fromJson(s, clazz);
    }

    public static <T> List<String> serialize(List<T> o) {
        List<String> se = new ArrayList<>();
        o.forEach(o1 -> se.add(Array.GSON.toJson(o1)));
        return se;
    }

    public static <T> List<T> deserialize(List<String> se, Class<T> clazz) {
        List<T> tr = new ArrayList<>();
        se.forEach(s -> tr.add(Array.GSON.fromJson(s, clazz)));
        return tr;
    }
}
