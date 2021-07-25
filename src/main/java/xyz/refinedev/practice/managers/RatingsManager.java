package xyz.refinedev.practice.managers;

import org.bukkit.ChatColor;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.meta.Rating;
import xyz.refinedev.practice.util.chat.ProgressBar;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/12/2021
 * Project: Array
 */

public class RatingsManager {

    private final BasicConfigurationFile config = Array.getInstance().getRateConfig();

    public void init() {
        this.load();
        this.save();

        Array.logger("&7Calculated Arena Ratings!");
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

    public String getBar(int currentRating, int totalRating) {
        return ProgressBar.getProgressBar(currentRating, totalRating, 40, '|', ChatColor.GREEN, ChatColor.RED);
    }


}
