package xyz.refinedev.practice.util.chat;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/12/2021
 * Project: Array
 */

public class ProgressBar {

    public static String getBar(int current, int total) {
        return ProgressBar.getProgressBar(current, total, 40, '\u258e', ChatColor.GREEN, ChatColor.GRAY);
    }

    public static String getProgressBar(int current, int max, int totalBars, char symbol, ChatColor completedColor, ChatColor notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat("" + completedColor + symbol, progressBars)
                + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
    }
}
