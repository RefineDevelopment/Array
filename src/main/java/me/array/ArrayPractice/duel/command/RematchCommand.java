package me.array.ArrayPractice.duel.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.duel.menu.DuelSelectKitMenu;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.meta.ProfileRematchData;
import me.array.ArrayPractice.util.external.CC;
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
