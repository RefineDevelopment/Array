package me.drizzy.practice.util.nametags.provider;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.nametags.NameTagHandler;
import me.drizzy.practice.util.nametags.construct.NameTagInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public abstract class NameTagProvider {

    private final String name;
    private final int weight;

    public abstract NameTagInfo fetchNameTag(Player toRefresh, Player refreshFor);

    public static NameTagInfo createNameTag(String prefix, String suffix) {
        return (NameTagHandler.getOrCreate(CC.translate(prefix), CC.translate(suffix)));
    }
}