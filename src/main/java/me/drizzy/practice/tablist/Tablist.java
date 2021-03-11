package me.drizzy.practice.tablist;

import me.allen.ziggurat.ZigguratAdapter;
import me.allen.ziggurat.objects.BufferedTabObject;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

//TODO: Finish recoding the tab
public class Tablist implements ZigguratAdapter {

    @Override
    public String getHeader() {
        return "";
    }

    @Override
    public String getFooter() {
        return "";
    }

    @Override
    public Set<BufferedTabObject> getSlots(final Player player) {
        final Set<BufferedTabObject> elements=new HashSet<>();
        final Profile profile=Profile.getByUuid(player.getUniqueId());
        return elements;
    }


}