package me.drizzy.practice.util.nametags.provider;

import me.drizzy.practice.util.nametags.construct.NameTagInfo;
import org.bukkit.entity.Player;

public class DefaultNameTagProvider extends NameTagProvider {

    public DefaultNameTagProvider() {
        super("Default Provider", 0);
    }

    @Override
    public NameTagInfo fetchNameTag(Player toRefresh,Player refreshFor) {
        return (createNameTag(toRefresh.getDisplayName().replace(toRefresh.getName(),""), ""));
    }

}
