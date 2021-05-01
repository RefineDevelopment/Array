package me.drizzy.practice.essentials.commands;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.kit.KitInventory;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandMeta(label = "array clearloadout", permission = "array.dev")
public class ArrayClearLoadoutsCommand {
    public void execute(Player player, @CPL("kit/all")String kittype, @CPL("global/profile") String type) {
        if(type.equalsIgnoreCase("global")) {
            if (Kit.getByName(kittype) != null) {
                Kit kit = Kit.getByName(kittype);
                for ( Profile profile : Profile.getProfiles().values() ) {
                    for ( KitInventory kitInventory : profile.getStatisticsData().get(kit).getLoadouts() ) {
                        profile.getStatisticsData().get(kit).deleteKit(kitInventory);
                    }
                    profile.save();
                    if (profile.getPlayer().isOnline()) {
                        profile.getPlayer().kickPlayer("Please re-log due to your kit loadouts being reset by an Admin.");
                    }
                }
            } else {
                if (kittype.equalsIgnoreCase("all")) {
                    for ( Kit kit : Kit.getKits() ) {
                        for ( Profile profile : Profile.getProfiles().values() ) {
                            for ( KitInventory kitInventory : profile.getStatisticsData().get(kit).getLoadouts() ) {
                                profile.getStatisticsData().get(kit).deleteKit(kitInventory);
                            }
                            profile.save();
                            if (profile.getPlayer().isOnline()) {
                                profile.getPlayer().kickPlayer("Please re-log due to your kit loadouts being reset by an Admin.");
                            }
                        }
                    }
                 } else {
                    player.sendMessage(CC.translate("&7Invalid Type!"));
                    return;
                }
            }
            player.sendMessage(CC.translate("&8[&cArray&8] &7Succesfully deleted kitloadouts for &cAll Profiles!"));
        } else if (Bukkit.getPlayer(type) == null || !Bukkit.getPlayer(type).isOnline()) {
            player.sendMessage(CC.translate("&8[&cArray&8] &7That player is offline or does not exist."));
        } else {
            if (Kit.getByName(kittype) != null) {
                Kit kit = Kit.getByName(kittype);
                Player target = Bukkit.getPlayer(type);
                Profile profile = Profile.getByPlayer(target);
                for ( KitInventory kitInventory : profile.getStatisticsData().get(kit).getLoadouts() ) {
                    profile.getStatisticsData().get(kit).deleteKit(kitInventory);
                }
                profile.save();
                player.sendMessage(CC.translate("&8[&cArray&8] &7Successfully deleted kitloadouts for &c" + type));
            } else {
                if (kittype.equalsIgnoreCase("all")) {
                    for ( Kit kit : Kit.getKits() ) {
                        Player target = Bukkit.getPlayer(type);
                        Profile profile = Profile.getByPlayer(target);
                        for ( KitInventory kitInventory : profile.getStatisticsData().get(kit).getLoadouts() ) {
                            profile.getStatisticsData().get(kit).deleteKit(kitInventory);
                        }
                        profile.save();
                    }
                } else {
                    player.sendMessage(CC.translate("&7Invalid Type!"));
                }
            }
        }
    }
}
