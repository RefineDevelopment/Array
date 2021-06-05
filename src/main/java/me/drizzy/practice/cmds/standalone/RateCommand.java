package me.drizzy.practice.cmds.standalone;

import me.drizzy.practice.Array;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.annotation.Command;
import me.drizzy.practice.util.command.annotation.Sender;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import org.bukkit.entity.Player;

/**
 * This Project is property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/4/2021
 * Project: Array
 */

public class RateCommand {

    private final static Array plugin = Array.getInstance();
    private final static BasicConfigurationFile config = plugin.getRateConfig();

    @Command(name = "", desc = "Rate an Arena", usage = "<arena> <rating>")
    public void rate(@Sender Player player, Arena arena, String rating) {
        Profile profile = Profile.getByPlayer(player);

        if (!profile.isCanIssueRating()) {
            player.sendMessage(CC.translate("&7You cannot do this right now."));
            return;
        }
        String key = "RATINGS." + arena.getName().toUpperCase() + ".";

        //To check if the values are not since we are calling them in our next method
        //I could just make a proper manager for all this but I'm kind of in a rush rn

        if (config.get(key + "TERRIBLE") == null) config.set(key + "TERRIBLE", 0);
        if (config.get(key + "AVERAGE") == null) config.set(key + "AVERAGE", 0);
        if (config.get(key + "DECENT") == null) config.set(key + "DECENT", 0);
        if (config.get(key + "OKAY") == null) config.set(key + "OKAY", 0);
        if (config.get(key + "GOOD") == null) config.set(key + "GOOD", 0);

        switch (rating) {
            case "Terrible":
                config.set(key + "TERRIBLE", config.getInteger(key + "TERRIBLE") + 1);
                break;
            case "Average":
                config.set(key + "AVERAGE", config.getInteger(key + "AVERAGE") + 1);
                break;
            case "Okay":
                config.set(key + "OKAY", config.getInteger(key + "OKAY") + 1);
                break;
            case "Decent":
                config.set(key + "DECENT", config.getInteger(key + "DECENT") + 1);
                break;
            case "Good":
                config.set(key + "GOOD", config.getInteger(key + "GOOD") + 1);
                break;
        }
        config.save();

        player.sendMessage(CC.translate("&aThanks for rating the map! We have recorded your rating."));
        profile.setCanIssueRating(false);


    }
}
