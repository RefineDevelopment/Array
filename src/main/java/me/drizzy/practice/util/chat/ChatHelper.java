package me.drizzy.practice.util.chat;

import me.drizzy.practice.util.external.ChatComponentBuilder;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

public class ChatHelper {

    public static ClickEvent click(String command) {
        return new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
    }

    public static HoverEvent hover(String text) {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("").parse(text).create());
    }

}
