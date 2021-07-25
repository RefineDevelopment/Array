package xyz.refinedev.practice.arena.impl;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.ArenaType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;
import xyz.refinedev.practice.util.location.LocationUtil;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SharedArena extends Arena {
    
    private final BasicConfigurationFile config = Array.getInstance().getArenasConfig();

    public SharedArena(String name) {
        super(name);
    }

    @Override
    public ArenaType getType() {
        return ArenaType.SHARED;
    }

    @Override
    public void save() {
        String path = "arenas." + getName();

        config.set(path, null);
        config.set(path + ".type", getType().name());
        config.set(path + ".display-name", CC.untranslate(getDisplayName()));
        config.set(path + ".icon.material", displayIcon.getType().name());
        config.set(path + ".icon.durability", displayIcon.getDurability());
        config.set(path + ".disable-pearls", disablePearls);

        if (spawn1 != null) config.set(path + ".spawn1", LocationUtil.serialize(spawn1));
        if (spawn2 != null) config.set(path + ".spawn2", LocationUtil.serialize(spawn2));
        if (max != null) config.set(path + ".max", LocationUtil.serialize(max));
        if (min != null) config.set(path + ".min", LocationUtil.serialize(min));

        config.set(path + ".kits", getKits());
        config.save();
    }

    @Override
    public void delete() {
        config.set("arenas." + getName(), null);
        config.save();
    }

}
