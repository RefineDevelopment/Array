package xyz.refinedev.practice.arena;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.ChunkSection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.arena.cache.ArenaCache;
import xyz.refinedev.practice.arena.cache.ArenaChunk;
import xyz.refinedev.practice.arena.cuboid.Cuboid;
import xyz.refinedev.practice.arena.rating.Rating;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chunk.ChunkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/12/2021
 * Project: Array
 */

@Getter @Setter
public abstract class Arena {

    private List<Kit> kits = new ArrayList<>();

    private final String name;
    private String displayName;
    private Location spawn1, spawn2, min, max;

    private ArenaType type;
    private ArenaCache cache;
    private Rating rating = new Rating(this, 0, 0, 0,0, 0);
    private ItemStack displayIcon = new ItemStack(Material.PAPER);

    private int deathHeight = 25, buildHeight = 5;
    private boolean active, duplicate, disablePearls;

    public Arena(String name, ArenaType arenaType) {
        this.name = name;
        this.type = arenaType;
        this.displayName = CC.RED + name;
    }

    public int getMaxBuildHeight() {
        int highest = (int) (Math.max(spawn1.getY(), spawn2.getY()));
        return highest + buildHeight;
    }

    public int getFallDeathHeight() {
        return this.getSpawn1() == null ? 25 : this.getSpawn1().getBlockY() - this.deathHeight;
    }

    public void setActive(boolean active) {
        if (this.getType() != ArenaType.SHARED) this.active = active;
    }

    public boolean isStandalone() {
        return this.type == ArenaType.STANDALONE;
    }

    public boolean isShared() {
        return this.type == ArenaType.SHARED;
    }

    public abstract boolean isSetup();

    /**
     * Caches the arena's original chunks and saves them in memory
     * for restoration after the arena is used. By doing this we
     * improve the restoration speed and fix a lot of bugs regarding
     * resetting the arena, this way whatever you do inside the arena
     * chunks, it always gets reset no matter what.
     */
    public void takeSnapshot() {
        Cuboid cuboid = new Cuboid(min, max);
        ArenaCache chunkCache = new ArenaCache();
        cuboid.getChunks().forEach(chunk -> {
            chunk.load();
            Chunk nmsChunk = ((CraftChunk)chunk).getHandle();
            ChunkSection[] nmsSections = ChunkUtil.copyChunkSections(nmsChunk.getSections());
            chunkCache.chunks.put(new ArenaChunk(chunk.getX(), chunk.getZ()), ChunkUtil.copyChunkSections(nmsSections));
        });

        this.cache = chunkCache;
    }

    /**
     * This method completely restores the arena to its
     * original state, no matter what changes have occurred inside
     * its chunks. This method instead of running a tracker and runnable
     * might be a bit memory excessive but does better in performance
     * as compared to the latter.
     */
    public void restoreSnapshot() {
        Cuboid cuboid = new Cuboid(min, max);
        cuboid.getChunks().forEach(chunk -> {
            try {
                chunk.load();
                Chunk craftChunk = ((CraftChunk)chunk).getHandle();
                ChunkSection[] sections = ChunkUtil.copyChunkSections(this.cache.getArenaChunkAtLocation(chunk.getX(), chunk.getZ()));
                ChunkUtil.setChunkSections(craftChunk, sections);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
