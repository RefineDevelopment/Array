package xyz.refinedev.practice.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.meta.Rating;
import xyz.refinedev.practice.arena.meta.RatingType;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chat.Clickable;
import xyz.refinedev.practice.util.chat.ProgressBar;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/12/2021
 * Project: Array
 */

public class RatingsManager {

    private final Array plugin;
    private final BasicConfigurationFile config;

    public RatingsManager(Array plugin) {
        this.plugin = plugin;
        this.config = plugin.getRateConfig();
    }

    public void init() {
        if (plugin.getConfigHandler().isRATINGS_ENABLED()) {
            this.load();
            this.save();
        }
    }

    public void load() {
        for ( Arena arena : Arena.getArenas() ) {
            if (arena == null) return;
            String name = arena.getName().toUpperCase();

            if (config.getConfiguration().getConfigurationSection("RATINGS") == null) return;

            for ( String key : config.getConfiguration().getConfigurationSection("RATINGS").getKeys(false)) {
                if (key == null) return;
                String path = "RATINGS." + name + ".";

                if (key.equalsIgnoreCase(name)) {
                    int terrible = config.getInteger(path + "TERRIBLE", 0);
                    int average = config.getInteger(path + "AVERAGE", 0);
                    int decent = config.getInteger(path + "DECENT", 0);
                    int okay = config.getInteger(path + "OKAY", 0);
                    int good = config.getInteger(path + "GOOD", 0);

                    Rating rating = new Rating(arena);
                    rating.setTerrible(terrible);
                    rating.setAverage(average);
                    rating.setDecent(decent);
                    rating.setOkay(okay);
                    rating.setGood(good);

                    arena.setRating(rating);
                }
            }
        }
    }

    public void save() {
        for ( Arena arena : Arena.getArenas() ) {
            if (arena == null) return;

            String name = arena.getName().toUpperCase();
            Rating rating = arena.getRating();
            String path = "RATINGS." + name + ".";

            config.set(path + "TERRIBLE", rating.getTerrible());
            config.set(path + "AVERAGE", rating.getAverage());
            config.set(path + "DECENT", rating.getDecent());
            config.set(path + "OKAY", rating.getTerrible());
            config.set(path + "GOOD", rating.getGood());
        }
        config.save();
    }

    /**
     * Send the player our rating message and allow them to
     * rate the arena provided in their match
     *
     * @param player {@link Player} the player sending the rating message
     * @param arena {@link Arena} the arena being rated
     */
    public void sendRatingMessage(Player player, Arena arena) {
        Profile profile = Profile.getByPlayer(player);
        profile.setCanIssueRating(true);

        String key = "&7Click to rate &a" + arena.getDisplayName();
        Clickable clickable = new Clickable("&c&l[1⭐]", key + " &7as &cTerrible&7.", "/rate " + arena.getName() + " " + RatingType.TERRIBLE.name());
        clickable.add("&6&l[2⭐]", key + " &7as &6Okay&7.", "/rate " + arena.getName() + " " + RatingType.OKAY.name());
        clickable.add("&e&l[3⭐]", key + " &7as &eAverage&7.", "/rate " + arena.getName() + " " + RatingType.AVERAGE.name());
        clickable.add("&2&l[4⭐]", key + " &7as &2Decent&7.", "/rate " + arena.getName() + " " + RatingType.DECENT.name());
        clickable.add("&a&l[5⭐]", key + " &7as &aGood&7.", "/rate " + arena.getName() + " " + RatingType.GOOD.name());

        player.sendMessage("");
        player.sendMessage(CC.translate("&aPlease give us feedback on the Arena, How was it?"));
        clickable.sendToPlayer(player);
    }

    /**
     * Get the Ratings Survey Bar
     *
     * @param currentRating {@link Integer} the rating of the Arena
     * @param totalRating {@link Integer} total ratings of the Arena
     * @return
     */
    public String getBar(int currentRating, int totalRating) {
        return ProgressBar.getProgressBar(currentRating, totalRating, 40, '|', ChatColor.GREEN, ChatColor.RED);
    }


}
