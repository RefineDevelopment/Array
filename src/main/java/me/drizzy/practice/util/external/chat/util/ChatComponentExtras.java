package me.drizzy.practice.util.external.chat.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

@AllArgsConstructor
@Data
public class ChatComponentExtras {

    private HoverEvent hoverEvent;
    private ClickEvent clickEvent;

}
