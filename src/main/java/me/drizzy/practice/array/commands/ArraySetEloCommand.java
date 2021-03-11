package me.drizzy.practice.array.commands;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.PlayerUtil;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label="array setelo", permission="array.staff")
public class ArraySetEloCommand {
    public void execute(Player player, @CPL("profile") String name, @CPL("[global|kit]") String type, @CPL("amount") String inter ) {
        Player target=PlayerUtil.getPlayer(name);
        int elo = Integer.parseInt(inter);
        if (target != null) {
            Profile profile=Profile.getByUuid(target);

            if (type.equalsIgnoreCase("global")) {
                profile.setGlobalElo(elo);
                player.sendMessage(CC.translate("&8[&bArray&8] &7Updated Global elo for &b" + name));
                return;
            }

            if (Kit.getByName(type) == null) {
                player.sendMessage(CC.translate("&8[&bArray&8] &7Kit &b" + name + " &7doesn't exist!"));
            } else {
                Kit kit=Kit.getByName(type);
                profile.getStatisticsData().get(kit).setElo(elo);
                player.sendMessage(CC.translate("&8[&bArray&8] &7Updated &b" + type + "'s&7 elo for &b" + name));
                profile.calculateGlobalElo();
            }
        } else {
            player.sendMessage(CC.translate("&8[&bArray&8] &7Player not found or is not online!"));
        }
    }
}
