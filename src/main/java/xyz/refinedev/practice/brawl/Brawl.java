package xyz.refinedev.practice.brawl;

import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.brawl.arena.BrawlArena;
import xyz.refinedev.practice.kit.Kit;
//import xyz.refinedev.practice.managers.BrawlManager;
import org.bukkit.entity.Entity;

import java.util.*;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/12/2021
 * Project: Array
 */

@Getter @Setter
public class Brawl {

    public static final Array plugin = Array.getInstance();

    @Getter public static final List<Brawl> brawls = new ArrayList<>();

    private final Map<UUID, BrawlPlayer> players = new HashMap<>();
    private final List<Entity> droppedItems = new ArrayList<>();

    private final String name;
    private BrawlArena arena;
    private String displayName;
    private Kit kit;

    private boolean enabled;

    /**
     * The Main construct for a Brawl
     *
     * @param name The name of the Brawl
     */
    public Brawl(String name) {
        this.name = name;
        this.displayName = name;

        brawls.add(this);
    }

    /**
     * Get a {@link Brawl} using its name
     *
     * @param name The name of the Brawl
     * @return {@link Brawl}
     */
    public Brawl getByName(String name) {
        for ( Brawl brawl : brawls ) {
            if (brawl.getName().equalsIgnoreCase(name)) {
                return brawl;
            }
        }
        return null;
    }

    /**
     * Returns a {@link BrawlPlayer} using UUID
     *
     * @param uuid The UUID of the Player
     * @return {@link BrawlPlayer}
     */
    public BrawlPlayer getPlayerByUUID(UUID uuid) {
        return players.get(uuid);
    }

    /*public void save() {
        Configuration config = plugin.getBrawlConfig().getConfiguration();

        String key = "BRAWLS." + name + ".";

        config.set(key + "DISPLAY_NAME", displayName);
        config.set(key + "ENABLED", enabled);
        config.set(key + "ARENA", arena.getName());
        config.set(key + "KIT", kit.getName());
    }*/


}