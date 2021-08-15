package me.drizzy.practice.duel.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.duel.RematchProcedure;
import me.drizzy.practice.duel.menu.DuelSelectKitMenu;
import me.drizzy.practice.profile.Profile;
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

        RematchProcedure rematchProcedure = profile.getRematchData();

        if (rematchProcedure.isReceive()) {
            rematchProcedure.accept();
        } else {
            if (rematchProcedure.isSent()) {
                player.sendMessage(Locale.ERROR_EXPIREREMATCH.toString());
                return;
            }
            rematchProcedure.request();
        }
    }

}
