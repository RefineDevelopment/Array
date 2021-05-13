package me.drizzy.practice.clan.commands.player;

import com.mongodb.client.model.Filters;
import me.drizzy.practice.clan.Clan;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Drizzy
 * Created at 5/13/2021
 */

@CommandMeta(label = "clan create")
public class ClanCreateCommand {

    public void execute(Player player, @CPL("name") String clanName) {
        Profile profile = Profile.getByPlayer(player);

        if (profile.hasClan()) {
            player.sendMessage(CC.translate("&7You are already in a clan!"));
            return;
        }

        if (clanName.length() < 2) {
            player.sendMessage(CC.RED + "Clan names must be greater than or equal to 2 characters long.");
            return;
        }

        if (!StringUtils.isAlpha(clanName)) {
            player.sendMessage(CC.RED + "Clan names must only contain alpha characters (letters only).");
            return;
        }

        if (clanName.length() > 8) {
            player.sendMessage(CC.RED + "Clan names must be less than or equal to 8 characters long.");
            return;
        }

        if (Clan.getByName(clanName) != null) {
            player.sendMessage(CC.translate("&7A clan with that name already exists!"));
            return;
        }

        Clan clan = new Clan(clanName, player.getUniqueId(), UUID.randomUUID());
        clan.setDescription(CC.GRAY + "This is the default description clan description.");
        clan.setDateCreated(System.currentTimeMillis());
        player.sendMessage(CC.GREEN + "You have successfully created a new clan!");
    }
}
