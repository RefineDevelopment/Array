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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class StandaloneArena extends Arena {

    private final Array plugin;
    private final List<StandaloneArena> duplicates = new ArrayList<>();

    public StandaloneArena(Array plugin, String name) {
        super(plugin, name, ArenaType.STANDALONE);

        this.plugin = plugin;
    }

    @Override
    public void save() {
        String path = "arenas." + getName();

        BasicConfigurationFile config = plugin.getArenasConfig();
        config.set(path, null);
        config.set(path + ".type", this.getType().name());
        config.set(path + ".display-name", CC.untranslate(this.getDisplayName()));

        if (this.getSpawn1() != null) config.set(path + ".spawn1", LocationUtil.serialize(this.getSpawn1()));
        if (this.getSpawn2() != null) config.set(path + ".spawn2", LocationUtil.serialize(this.getSpawn2()));
        if (this.getMax() != null) config.set(path + ".max", LocationUtil.serialize(this.getMax()));
        if (this.getMin() != null) config.set(path + ".min", LocationUtil.serialize(this.getMin()));

        config.set(path + ".disable-pearls", this.isDisablePearls());
        config.set(path + ".fall-death-height", this.getDeathHeight());
        config.set(path + ".icon.material", this.getDisplayIcon().getType().name());
        config.set(path + ".icon.durability", this.getDisplayIcon().getDurability());
        config.set(path + ".kits", getKits().stream().map(Kit::getName).collect(Collectors.toList()));

        if (!duplicates.isEmpty()) {
            int id = 0;

            for (StandaloneArena duplicate : duplicates) {
                id++;

                config.set(path + ".duplicates." + id + ".spawn1", LocationUtil.serialize(duplicate.getSpawn1()));
                config.set(path + ".duplicates." + id + ".spawn2", LocationUtil.serialize(duplicate.getSpawn2()));
                config.set(path + ".duplicates." + id + ".max", LocationUtil.serialize(duplicate.getMax()));
                config.set(path + ".duplicates." + id + ".min", LocationUtil.serialize(duplicate.getMin()));
            }
        }

        config.save();
    }

    @Override
    public boolean isSetup() {
        return this.getSpawn1() != null && this.getSpawn2() != null && this.getMin() != null && this.getMax() != null;
    }

}
