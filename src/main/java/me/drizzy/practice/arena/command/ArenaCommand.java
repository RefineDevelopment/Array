package me.drizzy.practice.arena.command;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(label = { "arena", "arenahelp" }, permission = "array.dev")
public class ArenaCommand {
    public void execute(final CommandSender player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
        player.sendMessage(CC.translate( "&bArray &7» Arena Commands"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
        player.sendMessage(CC.translate(" &8• &b/arena create &8<&7name&8> &8<&7Shared|Standalone|TheBridge&8> &8- &8&o(&7&oCreate an Arena&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/arena remove &8<&7name&8> &8- &8&o(&7&oDelete an Arena&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/arena portalwand &8- &8&o(&7&oReceive a wand to select the portal for TheBridge Arena&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/arena kitlist &8<&7Arena&8> &8- &8&o(&7&oLists all the kits of an arena&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/arena seticon &8<&7Arena&8> &8- &8&o(&7&oSets the item your holding as Arena Icon&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/arena setportal &8<&7red|blue&8> &8- &8&o(&7&oSets red/blue portal from your selection&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/arena setspawn &8<&71/2&8> &8<&71/2&8> &8- &8&o(&7&oSet 1/2 spawn of arena&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/arena addkit &8<&7Arena&8> &8<&7Kit&8> &8- &8&o(&7&oAdd a kit to the arena&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/arena addnormalkits &8<&7Arena&8> &8<&7Kit&8> &8- &8&o(&7&oAdd all the normal kits to the arena&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/arena addbuildkits &8<&7Arena&8> &8<&7Kit&8> &8- &8&o(&7&oAdd all the build kits to the arena&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/arena removekit &8<&7Arena&8> <&7Kit&8> &8- &8&o(&7&oRemove a kit from the arena&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/arena disablepearls &8- &8&o(&7&oEnable or Disable the ability for players to pearl on the arena&8&o)"));
        player.sendMessage(CC.translate(" &8• &b/arena save &8- &8&o(&7&oSave Arenas&8&o)"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m--------&7&m" + StringUtils.repeat("-", 37) + "&b&m--------"));
    }

}
