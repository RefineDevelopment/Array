package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.ArenaType;
import xyz.refinedev.practice.arena.impl.SharedArena;
import xyz.refinedev.practice.arena.impl.StandaloneArena;
import xyz.refinedev.practice.arena.rating.Rating;
import xyz.refinedev.practice.arena.rating.RatingType;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chat.Clickable;
import xyz.refinedev.practice.util.chat.ProgressBar;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.location.LocationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/17/2021
 * Project: Array
 */

@Getter @Setter
@RequiredArgsConstructor
public class ArenaManager {

    private final Array plugin;
    private final BasicConfigurationFile config;

    private final List<Arena> arenas = new ArrayList<>();
    private boolean pasting;

    public void init() {
        ConfigurationSection section = config.getConfigurationSection("arenas");
        if (section == null || section.getKeys(false).isEmpty()) return;

        for ( String name : section.getKeys(false) ) {
            String path = name + ".";

            ArenaType arenaType = ArenaType.valueOf(section.getString(path + "type"));
            switch (arenaType) {
                case SHARED: {
                    SharedArena sharedArena = new SharedArena(name);
                    this.load(sharedArena);
                    this.calculateRatings(sharedArena);
                    this.arenas.add(sharedArena);
                    break;
                }
                case STANDALONE: {
                    StandaloneArena standaloneArena = new StandaloneArena(name);
                    this.load(standaloneArena);
                    this.calculateRatings(standaloneArena);
                    this.loadDuplicates(standaloneArena);
                    this.arenas.add(standaloneArena);
                    break;
                }
            }
        }
        plugin.logger("&7Loaded &c" + arenas.size() + " &7Arena(s)!");
        if (!arenas.isEmpty()) {
            plugin.logger("&7Loading Chunks for &c" + arenas.size() + " &7Arena(s)");
            this.loadChunks();
        }
    }

    /**
     * Load an {@link Arena} from the config
     *
     * @param arena {@link Arena} the arena being loaded
     */
    public void load(Arena arena) {
        String path = "arenas." + arena.getName();

        if (config.contains(path + ".display-name")) arena.setDisplayName(CC.translate(config.getString(path + ".display-name")));
        if (config.contains(path + ".spawn1")) arena.setSpawn1(LocationUtil.deserialize(config.getString(path + ".spawn1")));
        if (config.contains(path + ".spawn2")) arena.setSpawn2(LocationUtil.deserialize(config.getString(path + ".spawn2")));
        if (config.contains(path + ".max")) arena.setMax(LocationUtil.deserialize(config.getString(path + ".max")));
        if (config.contains(path + ".min")) arena.setMin(LocationUtil.deserialize(config.getString(path + ".min")));
        if (config.contains(path + ".disable-pearls")) arena.setDisablePearls(config.getBoolean(path + ".disable-pearls"));
        if (config.contains(path + ".build-height")) arena.setBuildHeight(config.getInteger(path + ".build-height"));
        if (config.contains(path + ".fall-death-height")) arena.setDeathHeight(config.getInteger(path + ".fall-death-height", 25));

        if (config.contains(path + ".icon")) {
            try {
                arena.setDisplayIcon(Array.GSON.fromJson(config.getString(path + ".icon"), ItemStack.class));
            } catch (Exception e) {
                arena.setDisplayIcon(new ItemStack(Material.PAPER));
            }
        }

        if (config.contains(path + ".kits") && !config.getStringList(path + ".kits").isEmpty()) {
            List<String> kitNames = config.getStringList(path + ".kits");
            arena.setKits(kitNames.stream().map(plugin.getKitManager()::getByName).filter(Objects::nonNull).collect(Collectors.toList()));
        }
    }

    /**
     * Load the Duplicate arenas for a specific arena
     *
     * @param arena {@link Arena} the arena whose duplicates we are loading
     */
    public void loadDuplicates(Arena arena) {
        String path = "arenas." + arena.getName();

        ConfigurationSection section = config.getConfigurationSection(path + ".duplicates");
        if (section == null || section.getKeys(false).isEmpty()) return;

        StandaloneArena standaloneArena = (StandaloneArena) arena;
        for (String duplicateId : section.getKeys(false)) {
            Location spawn1 = LocationUtil.deserialize(config.getString(path + ".duplicates." + duplicateId + ".spawn1"));
            Location spawn2 = LocationUtil.deserialize(config.getString(path + ".duplicates." + duplicateId + ".spawn2"));
            Location max = LocationUtil.deserialize(config.getString(path + ".duplicates." + duplicateId + ".max"));
            Location min = LocationUtil.deserialize(config.getString(path + ".duplicates." + duplicateId + ".min"));

            StandaloneArena duplicate = new StandaloneArena(arena.getName() + "#" + duplicateId);
            duplicate.setDisplayName(arena.getDisplayName());
            duplicate.setSpawn1(spawn1);
            duplicate.setSpawn2(spawn2);
            duplicate.setMax(max);
            duplicate.setMin(min);
            duplicate.setKits(arena.getKits());
            duplicate.setDuplicate(true);
            duplicate.takeSnapshot();

            standaloneArena.getDuplicates().add(duplicate);
            this.arenas.add(duplicate);
        }
        standaloneArena.takeSnapshot();
    }

    /**
     * Save an {@link Arena} to the config
     *
     * @param arena {@link Arena} the arena being saved
     */
    public void save(Arena arena) {
        String path = "arenas." + arena.getName() + ".";

        config.set("arenas." + arena.getName(), null);
        config.set(path + "display-name", CC.untranslate(arena.getDisplayName()));
        config.set(path + "type", arena.getType().name());
        config.set(path + "spawn1", LocationUtil.serialize(arena.getSpawn1()));
        config.set(path + "spawn2", LocationUtil.serialize(arena.getSpawn2()));
        config.set(path + "max", LocationUtil.serialize(arena.getMax()));
        config.set(path + "min", LocationUtil.serialize(arena.getMin()));
        config.set(path + "disable-pearls", arena.isDisablePearls());
        config.set(path + "build-height", arena.getBuildHeight());
        config.set(path + "fall-death-height", arena.getFallDeathHeight());
        config.set(path + "icon", Array.GSON.toJson(arena.getDisplayIcon()));
        config.set(path + "kits", arena.getKits().stream().map(Kit::getName).filter(Objects::nonNull).collect(Collectors.toList()));
        config.save();

        if (arena.isStandalone()) {
            StandaloneArena standaloneArena = (StandaloneArena) arena;
            if (standaloneArena.getDuplicates().isEmpty()) return;

            int id = 0;
            for (StandaloneArena duplicate : standaloneArena.getDuplicates()) {
                id++;

                config.set(path + "duplicates." + id + ".spawn1", LocationUtil.serialize(duplicate.getSpawn1()));
                config.set(path + "duplicates." + id + ".spawn2", LocationUtil.serialize(duplicate.getSpawn2()));
                config.set(path + "duplicates." + id + ".max", LocationUtil.serialize(duplicate.getMax()));
                config.set(path + "duplicates." + id + ".min", LocationUtil.serialize(duplicate.getMin()));
            }
        }

        config.save();
    }

    /**
     * Delete an {@link Arena} from the config
     *
     * @param arena {@link Arena} the arena being deleted
     */
    public void delete(Arena arena) {
        config.set("arenas." + arena.getName(), null);
        config.save();

        this.arenas.remove(arena);
    }

    /**
     * Load the {@link Arena}'s ratings from the config
     *
     * @param arena {@link Arena} the arena whose ratings are being calculated
     */
    public void calculateRatings(Arena arena) {
        String path = "arenas." + arena.getName() + ".";

        int terrible = config.getInteger(path + "RATINGS.TERRIBLE");
        int average = config.getInteger(path + "RATINGS.AVERAGE");
        int decent = config.getInteger(path + "RATINGS.DECENT");
        int okay = config.getInteger(path + "RATINGS.OKAY");
        int good = config.getInteger(path + "RATINGS.GOOD");

        Rating rating = new Rating(arena, terrible, average, decent, okay, good);
        arena.setRating(rating);
    }

    public void reloadArenas() {
        this.arenas.clear();
        this.init();
    }

    /**
     * Get an {@link Arena} by its name
     *
     * @param name {@link String} name of the arena
     * @return {@link Arena}
     */
    public Arena getByName(String name) {
        return arenas.stream().filter(arena -> arena.getName().equals(name)).findAny().orElse(null);
    }

    /**
     * Get a {@link Arena} by a {@link Kit}
     *
     * @param kit {@link Kit} kit being utilized
     * @return {@link Arena}
     */
    public Arena getByKit(Kit kit) {
        List<Arena> randomArenas = new ArrayList<>();

        for (Arena arena : arenas) {
            if (!arena.isSetup() || !arena.getKits().contains(kit)) continue;
            if (arena.isActive() && (arena.isDuplicate() || arena.isStandalone())) continue;
            if (kit.getGameRules().isBuild() && arena.isShared()) continue;

            randomArenas.add(arena);
        }

        if (randomArenas.isEmpty()) {
            return null;
        }

        int i = Array.RANDOM.nextInt(randomArenas.size());
        return randomArenas.get(i);
    }

    /**
     * Load chunks of all the loaded arenas
     */
    public void loadChunks() {
        for ( Arena arena : this.arenas ) {
            if (arena.getMax() == null || arena.getMin() == null) continue;

            int arenaMinX = arena.getMin().getBlockX() >> 4;
            int arenaMinZ = arena.getMin().getBlockZ() >> 4;
            int arenaMaxX = arena.getMax().getBlockX() >> 4;
            int arenaMaxZ = arena.getMax().getBlockZ() >> 4;

            if (arenaMinX > arenaMaxX) {
                int lastArenaMinX = arenaMinX;
                arenaMinX = arenaMaxX;
                arenaMaxX = lastArenaMinX;
            }

            if (arenaMinZ > arenaMaxZ) {
                int lastArenaMinZ = arenaMinZ;
                arenaMinZ = arenaMaxZ;
                arenaMaxZ = lastArenaMinZ;
            }

            for ( int x = arenaMinX; x <=  arenaMaxX; x++ ) {
                for ( int z = arenaMinZ; z <=  arenaMaxZ; z++ ) {
                    Chunk chunk = arena.getMin().getWorld().getChunkAt(x, z);
                    if (!chunk.isLoaded()) {
                        chunk.load();
                    }
                }
            }
        }
        plugin.logger("&7Loaded &cChunks &7Successfully!");
    }

    public Arena findDuplicate(StandaloneArena arena) {
        Arena queriedArena = null;

        if (arena.getDuplicates() == null) return null;

        boolean found = false;
        for ( Arena duplicate : arena.getDuplicates() ) {
            if (!duplicate.isActive()) {
                queriedArena = duplicate;
                found = true;
                break;
            }
        }

        if (!found) return null;
        return queriedArena;
    }

    /**
     * Send the player our rating message and allow them to
     * rate the arena provided in their match
     *
     * @param player {@link Player} the player sending the rating message
     * @param arena {@link Arena} the arena being rated
     */
    public void sendRatingMessage(Player player, Arena arena) {
        Profile profile = plugin.getProfileManager().getProfileByPlayer(player);
        profile.setIssueRating(true);

        String key = "&7Click to rate &a" + arena.getDisplayName();
        Clickable clickable = new Clickable("&c&l[1⭐]", key + " &7as &cTerrible&7.", "/rate " + arena.getName() + " " + RatingType.TERRIBLE.name());
        clickable.add("&6&l[2⭐]", key + " &7as &6Okay&7.", "/rate " + arena.getName() + " " + RatingType.OKAY.name());
        clickable.add("&e&l[3⭐]", key + " &7as &eAverage&7.", "/rate " + arena.getName() + " " + RatingType.AVERAGE.name());
        clickable.add("&2&l[4⭐]", key + " &7as &2Decent&7.", "/rate " + arena.getName() + " " + RatingType.DECENT.name());
        clickable.add("&a&l[5⭐]", key + " &7as &aGood&7.", "/rate " + arena.getName() + " " + RatingType.GOOD.name());

        player.sendMessage("");
        player.sendMessage(Locale.MATCH_RATING_MESSAGE.toString());
        clickable.sendToPlayer(player);
    }

    /**
     * Get the Ratings Survey Bar
     *
     * @param currentRating {@link Integer} the rating of the Arena
     * @param totalRating {@link Integer} total ratings of the Arena
     *
     * @return {@link String} the bar
     */
    public String getBar(int currentRating, int totalRating) {
        return ProgressBar.getProgressBar(currentRating, totalRating, 10, '\u258e', ChatColor.GREEN, ChatColor.RED);
    }
}
