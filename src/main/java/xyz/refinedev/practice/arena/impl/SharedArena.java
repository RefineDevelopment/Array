package xyz.refinedev.practice.arena.impl;

import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.ArenaType;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.location.LocationUtil;

import java.util.stream.Collectors;

@Getter @Setter
public class SharedArena extends Arena {
    
    private final Array plugin;

    public SharedArena(Array plugin, String name) {
        super(plugin, name, ArenaType.SHARED);

        this.plugin = plugin;
    }

    @Override
    public void save() {
        BasicConfigurationFile config = plugin.getArenasConfig();
        String path = "arenas." + getName();

        config.set(path, null);
        config.set(path + ".type", getType().name());
        config.set(path + ".display-name", CC.untranslate(getDisplayName()));

        if (this.getSpawn1() != null) config.set(path + ".spawn1", LocationUtil.serialize(this.getSpawn1()));
        if (this.getSpawn2() != null) config.set(path + ".spawn2", LocationUtil.serialize(this.getSpawn2()));
        if (this.getMax() != null) config.set(path + ".max", LocationUtil.serialize(this.getMax()));
        if (this.getMin() != null) config.set(path + ".min", LocationUtil.serialize(this.getMin()));

        config.set(path + ".disable-pearls", this.isDisablePearls());
        config.set(path + ".fall-death-height", this.getFallDeathHeight());
        config.set(path + ".icon.material", this.getDisplayIcon().getType().name());
        config.set(path + ".icon.durability", this.getDisplayIcon().getDurability());
        config.set(path + ".kits", this.getKits().stream().map(Kit::getName).collect(Collectors.toList()));

        config.save();
    }

    @Override
    public boolean isSetup() {
        return this.getSpawn1() != null && this.getSpawn2() != null;
    }

}
