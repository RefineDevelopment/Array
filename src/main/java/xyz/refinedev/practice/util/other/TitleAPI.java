package xyz.refinedev.practice.util.other;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author ConnorLinFoot
 * Project: TitleAPI
 */

@UtilityClass
public class TitleAPI {

    private final BasicConfigurationFile config = Array.getInstance().getMainConfig();

    public void sendMatchStart(Player player) {
        String path = "MATCH.TITLE.STARTED";
        if (!config.contains(path)) return;
        if (!config.getBoolean(path + "ENABLED")) return;

        int stay = config.getInteger(path + "STAY", 5);
        int fadeIn = config.getInteger(path + "FADE_IN", 20);
        int fadeOut = config.getInteger(path + "FADE_OUT", 20);
        String text = config.getString(path + "TEXT");
        String subtitle = config.getString(path + "SUB_TEXT");

        if (subtitle != null && !subtitle.equalsIgnoreCase(" ") && !subtitle.equalsIgnoreCase("")) {
            player.sendTitle(new Title(text, subtitle, fadeIn, stay, fadeOut));
        } else {
            player.sendTitle(new Title(text, "", fadeIn, stay, fadeOut));
        }
    }

    public void sendMatchCountdown(Player player) {
        String path = "MATCH.TITLE.COUNTDOWN";
        if (!config.contains(path)) return;
        if (!config.getBoolean(path + "ENABLED")) return;

        int stay = config.getInteger(path + "STAY", 5);
        int fadeIn = config.getInteger(path + "FADE_IN", 20);
        int fadeOut = config.getInteger(path + "FADE_OUT", 20);
        String text = config.getString(path + "TEXT");
        String subtitle = config.getString(path + "SUB_TEXT");

        if (subtitle != null && !subtitle.equalsIgnoreCase(" ") && !subtitle.equalsIgnoreCase("")) {
            player.sendTitle(new Title(text, subtitle, fadeIn, stay, fadeOut));
        } else {
            player.sendTitle(new Title(text, "", fadeIn, stay, fadeOut));
        }
    }

    public void sendRespawning(Player player) {
        String path = "MATCH.TITLE.RESPAWNING";
        if (!config.contains(path)) return;
        if (!config.getBoolean(path + "ENABLED")) return;

        int stay = config.getInteger(path + "STAY", 5);
        int fadeIn = config.getInteger(path + "FADE_IN", 20);
        int fadeOut = config.getInteger(path + "FADE_OUT", 20);
        String text = config.getString(path + "TEXT");
        String subtitle = config.getString(path + "SUB_TEXT");

        if (subtitle != null && !subtitle.equalsIgnoreCase(" ") && !subtitle.equalsIgnoreCase("")) {
            player.sendTitle(new Title(text, subtitle, fadeIn, stay, fadeOut));
        } else {
            player.sendTitle(new Title(text, "", fadeIn, stay, fadeOut));
        }
    }

    public void sendRespawnCountdown(Player player, int tick) {
        String path = "MATCH.TITLE.RESPAWN_COUNTDOWN";
        if (!config.contains(path)) return;
        if (!config.getBoolean(path + "ENABLED")) return;

        int stay = config.getInteger(path + "STAY", 5);
        int fadeIn = config.getInteger(path + "FADE_IN", 20);
        int fadeOut = config.getInteger(path + "FADE_OUT", 20);
        String text = config.getString(path + "TEXT").replace("<seconds>", String.valueOf(tick));
        String subtitle = config.getString(path + "SUB_TEXT");

        if (subtitle != null && !subtitle.equalsIgnoreCase(" ") && !subtitle.equalsIgnoreCase("")) {
            player.sendTitle(new Title(text, subtitle, fadeIn, stay, fadeOut));
        } else {
            player.sendTitle(new Title(text, "", fadeIn, stay, fadeOut));
        }
    }

    public void sendMatchWinner(Player player) {
        String path = "MATCH.TITLE.WINNER";
        if (!config.contains(path)) return;
        if (!config.getBoolean(path + "ENABLED")) return;

        int stay = config.getInteger(path + "STAY", 5);
        int fadeIn = config.getInteger(path + "FADE_IN", 20);
        int fadeOut = config.getInteger(path + "FADE_OUT", 20);
        String text = config.getString(path + "TEXT");
        String subtitle = config.getString(path + "SUB_TEXT");

        if (subtitle != null && !subtitle.equalsIgnoreCase(" ") && !subtitle.equalsIgnoreCase("")) {
            player.sendTitle(new Title(text, subtitle, fadeIn, stay, fadeOut));
        } else {
            player.sendTitle(new Title(text, "", fadeIn, stay, fadeOut));
        }
    }

    public void sendMatchLoser(Player player) {
        String path = "MATCH.TITLE.LOSER";
        if (!config.contains(path)) return;
        if (!config.getBoolean(path + "ENABLED")) return;

        int stay = config.getInteger(path + "STAY", 5);
        int fadeIn = config.getInteger(path + "FADE_IN", 20);
        int fadeOut = config.getInteger(path + "FADE_OUT", 20);
        String text = config.getString(path + "TEXT");
        String subtitle = config.getString(path + "SUB_TEXT");

        if (subtitle != null && !subtitle.equalsIgnoreCase(" ") && !subtitle.equalsIgnoreCase("")) {
            player.sendTitle(new Title(text, subtitle, fadeIn, stay, fadeOut));
        } else {
            player.sendTitle(new Title(text, "", fadeIn, stay, fadeOut));
        }
    }

    public void sendBedDestroyed(Player player) {
        String path = "MATCH.TITLE.BED_DESTROYED";
        if (!config.contains(path)) return;
        if (!config.getBoolean(path + "ENABLED")) return;

        int stay = config.getInteger(path + "STAY", 5);
        int fadeIn = config.getInteger(path + "FADE_IN", 20);
        int fadeOut = config.getInteger(path + "FADE_OUT", 20);
        String text = config.getString(path + "TEXT");
        String subtitle = config.getString(path + "SUB_TEXT");

        if (subtitle != null && !subtitle.equalsIgnoreCase(" ") && !subtitle.equalsIgnoreCase("")) {
            player.sendTitle(new Title(text, subtitle, fadeIn, stay, fadeOut));
        } else {
            player.sendTitle(new Title(text, "", fadeIn, stay, fadeOut));
        }
    }

    public void clearTitle(Player player) {
        player.sendTitle(new Title("", "", 0, 0, 0));
    }
}
