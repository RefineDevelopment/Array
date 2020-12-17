package me.array.ArrayPractice.util;

import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Clickable {
    private List<TextComponent> components;

    public Clickable(final String msg) {
        this.components = new ArrayList<TextComponent>();
        final TextComponent message = new TextComponent(msg);
        this.components.add(message);
    }

    public Clickable(final String msg, final String hoverMsg, final String clickString) {
        this.components = new ArrayList<TextComponent>();
        this.add(msg, hoverMsg, clickString);
    }

    public Clickable() {
        this.components = new ArrayList<TextComponent>();
    }

    public TextComponent add(final String msg, final String hoverMsg, final String clickString) {
        final TextComponent message = new TextComponent(msg);
        if (hoverMsg != null) {
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMsg).create()));
        }
        if (clickString != null) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
        }
        this.components.add(message);
        return message;
    }

    public void add(final String message) {
        this.components.add(new TextComponent(message));
    }

    public void sendToPlayer(final Player player) {
        player.sendMessage((BaseComponent[]) this.asComponents());
    }

    public TextComponent[] asComponents() {
        return this.components.toArray(new TextComponent[0]);
    }
}
