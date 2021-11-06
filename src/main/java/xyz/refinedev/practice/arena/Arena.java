package xyz.refinedev.practice.arena;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.rating.Rating;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.util.chat.CC;

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

    private final Array plugin = Array.getInstance();
    private List<Kit> kits = new ArrayList<>();

    private final String name;
    private String displayName;
    private Location spawn1, spawn2, min, max;

    private ArenaType type;
    private Rating rating = new Rating(this, 0, 0, 0,0, 0);
    private ItemStack displayIcon = new ItemStack(Material.PAPER);

    private int deathHeight = 25, buildHeight = 5;
    private boolean active, duplicate, disablePearls;

    public Arena(String name, ArenaType arenaType) {
        this.name = name;
        this.type = arenaType;
        this.displayName = CC.RED + name;

        plugin.getArenaManager().getArenas().add(this);
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
}
