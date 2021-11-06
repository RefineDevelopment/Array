package xyz.refinedev.practice.arena.impl;

import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.ArenaType;

@Getter @Setter
public class SharedArena extends Arena {
    
    private final Array plugin = this.getPlugin();

    public SharedArena(String name) {
        super(name, ArenaType.SHARED);
    }

    @Override
    public boolean isSetup() {
        return this.getSpawn1() != null && this.getSpawn2() != null;
    }

}
