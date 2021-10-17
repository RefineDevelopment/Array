package xyz.refinedev.practice.arena.impl;

import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.ArenaType;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class StandaloneArena extends Arena {

    private final Array plugin;
    private final List<StandaloneArena> duplicates = new ArrayList<>();

    public StandaloneArena(Array plugin, String name) {
        super(plugin, name, ArenaType.STANDALONE);

        this.plugin = plugin;
    }

    @Override
    public boolean isSetup() {
        return this.getSpawn1() != null && this.getSpawn2() != null && this.getMin() != null && this.getMax() != null;
    }

}
