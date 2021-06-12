package xyz.refinedev.practice.util.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import xyz.refinedev.practice.util.chat.CC;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Replacement {
    private Map<Object, Object> replacements=new HashMap<>();
    private String message="";

    public Replacement(String message) {
        this.message=message;
    }

    public Replacement add(Object current, Object replacement) {
        replacements.put(current, replacement);
        return this;
    }

    public String toString() {
        replacements.keySet().forEach(current -> this.message=this.message.replace(String.valueOf(current), String.valueOf(replacements.get(current))));
        return CC.translate(this.message);
    }

    public String toString(boolean ignored) {
        replacements.keySet().forEach(current -> this.message=this.message.replace(String.valueOf(current), String.valueOf(replacements.get(current))));
        return this.message;
    }
}
