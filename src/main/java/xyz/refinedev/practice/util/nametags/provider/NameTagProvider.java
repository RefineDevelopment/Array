package xyz.refinedev.practice.util.nametags.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.nametags.construct.NameTagInfo;

@Getter
@AllArgsConstructor
public abstract class NameTagProvider {

    private final Array plugin = Array.getInstance();

    private final String name;
    private final int weight;

    public abstract NameTagInfo fetchNameTag(Player toRefresh, Player refreshFor);

    public NameTagInfo createNameTag(String prefix, String suffix) {
        return (plugin.getNameTagHandler().getOrCreate(CC.translate(prefix), CC.translate(suffix)));
    }
}