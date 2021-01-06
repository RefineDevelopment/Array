package me.array.ArrayPractice.profile.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tduel", "toggleduel", "dueltoggle"})
public class TduelCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        boolean scoreboardstate = profile.getOptions().isReceiveDuelRequests();
        player.sendMessage(CC.translate("&7Receiving Duels: " + (!scoreboardstate ? "&aOn" : "&cOff")));
        profile.getOptions().setReceiveDuelRequests(!scoreboardstate);
    }

}
