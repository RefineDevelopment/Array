package me.drizzy.practice.array.commands;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tduel", "toggleduel", "dueltoggle"})
public class ArrayToggleDuelCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        boolean duelstate = profile.getSettings().isReceiveDuelRequests();
        player.sendMessage(CC.translate("&7Receiving Duels: " + (!duelstate ? "&aOn" : "&cOff")));
        profile.getSettings().setReceiveDuelRequests(!duelstate);
    }

}
