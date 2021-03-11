package me.drizzy.practice.duel.command;

import me.drizzy.practice.duel.menu.DuelSelectKitMenu;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.meta.ProfileRematchData;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "rematch")
public class RematchCommand {

    public void execute(Player player) {
        if (player.hasMetadata("frozen")) {
            player.sendMessage(CC.RED + "You cannot duel a player while being frozen.");
            return;
        }

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getRematchData() == null) {
            player.sendMessage(CC.RED + "You do not have anyone to rematch.");
            return;
        }

        profile.checkForHotbarUpdate();

        if (profile.getRematchData() == null) {
            player.sendMessage(CC.RED + "That player is no longer available.");
            return;
        }

        ProfileRematchData profileRematchData = profile.getRematchData();

        if (profileRematchData.isReceive()) {
            profileRematchData.accept();
        } else {
            if (profileRematchData.isSent()) {
                player.sendMessage(CC.RED + "You have already sent a rematch request to that player.");
                return;
            }
            new DuelSelectKitMenu("rematch").openMenu(player);
            //profileRematchData.request();
        }
    }

}
