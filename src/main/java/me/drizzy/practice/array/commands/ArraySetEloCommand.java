package me.drizzy.practice.array.commands;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label="array setelo", permission="array.staff")
public class ArraySetEloCommand {
    public void execute(Player player, @CPL("profile") String name, @CPL("type (global/kitname)") String type, @CPL("amount") String inter ) {
        Player target=PlayerUtil.getPlayer(name);
        int elo = Integer.parseInt(inter);
        if (target != null) {
            Profile profile=Profile.getByUuid(target);

            if (type.equalsIgnoreCase("global")) {
                profile.setGlobalElo(elo);
                player.sendMessage(CC.translate("&8[&9Array&8] &7Updated Global elo for &d" + name));
                return;
            }

            if (!type.equalsIgnoreCase("global")) {
                if (Kit.getByName(type) == null) {
                    player.sendMessage(CC.translate("&8[&9Array&8] &7Kit &d" + name + " &7doesn't exist!"));
                } else {
                    Kit kit=Kit.getByName(type);
                    profile.getKitData().get(kit).setElo(elo);
                    player.sendMessage(CC.translate("&8[&9Array&8] &7Updated &d" + type + "'s&7 elo for &d" + name));
                    profile.calculateGlobalElo();
                }
            }
        } else {
            player.sendMessage(CC.RED + "Player not found or not online!");
        }
    }
}
