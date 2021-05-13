package me.drizzy.practice.clan.commands.player;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

/**
 * @author Drizzy
 * Created at 5/13/2021
 */

@CommandMeta(label = "clan chat")
public class ClanChatCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByPlayer(player);
        if (profile.getClan() == null) {
            player.sendMessage(CC.translate("&8[&c&lClan&8] &7You don't have a clan."));
            return;
        }
        if (profile.getSettings().isPartyChat()) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Your party chat is currently enabled, please disable it first."));
            return;
        }
        profile.getSettings().setClanChat(!profile.getSettings().isClanChat());
        player.sendMessage(CC.translate((profile.getSettings().isClanChat() ? "&aYou are now speaking in clan chat!" : "&aYou are now speaking in Global Chat")));
    }
}
