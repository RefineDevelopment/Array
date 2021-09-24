package xyz.refinedev.practice.hook.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: Array
 */

//TODO: Complete this
public class PracticePlaceholders extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "array";
    }

    @Override
    public String getAuthor() {
        return "Drizzy";
    }

    @Override
    public String getVersion() {
        return "2.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        switch (identifier) {
            case "": {

            }
        }

        return null;
    }
}
