package me.drizzy.practice.party.command;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = {"party chat", "p chat"})
public class PartyChatCommand {
    public void execute(Player player) {
        Profile profile = Profile.getByPlayer(player);
        if (profile.getParty() == null) {
            player.sendMessage(CC.translate("&8[&c&lParty&8] &7You don't have a party."));
            return;
        }
        if (profile.getSettings().isClanChat()) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Your clan chat is currently enabled, please disable it first."));
            return;
        }
        profile.getSettings().setPartyChat(!profile.getSettings().isPartyChat());
        player.sendMessage(CC.translate((profile.getSettings().isPartyChat() ? "&aYou are now speaking in party chat!" : "&aYou are now speaking in Global Chat")));
    }
}
