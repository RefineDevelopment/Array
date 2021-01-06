package me.array.ArrayPractice.profile.command.donator;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tpv", "toggleplayervisibility"}, permission = "practice.sock")
public class ToggleVisibilityCommand {

    public void execute(Player player) {

        Profile profile = Profile.getByUuid(player);

        if (profile.getLastRunVisibility() == 0L || (System.currentTimeMillis() - profile.getLastRunVisibility()) >= 5000L) {

            profile.setVisibility(!profile.isVisibility());

            boolean vis = profile.isVisibility();

            player.sendMessage(CC.translate((vis ? "&a" : "&c") + "You are " + (vis ? "now" : "no longer") + " seeing all &dEpic " + (vis ? "&a" : "&c") + "ranks and above."));

            profile.handleVisibility();

            profile.setLastRunVisibility(System.currentTimeMillis());
        } else {
            player.sendMessage(CC.translate("&cYou have to wait " + ((System.currentTimeMillis() - profile.getLastRunVisibility())) / 1000) + " more seconds before running that command again.");
        }
    }

}
