package me.array.ArrayPractice.kit.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = { "kit", "kithelp" }, permission = "practice.staff")
public class KitCommand {
    public void execute(final Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        player.sendMessage(CC.translate( "&bArray &7» Kit Commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        player.sendMessage(CC.translate("&7» &b/kit create (name) &7- Create a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit setkb (name) &7- Set a Kit's Knockback Profile."));
        player.sendMessage(CC.translate("&7» &b/kit remove (name) &7- Delete a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit hitdelay (name) (1-20) &7- Set a Kit's Hitdelay"));
        player.sendMessage(CC.translate("&7» &b/kit elo (name) &7- Toggle elo mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit build (name) &7- Toggle build mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit boxuhc (name) &7- Toggle boxuhc mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit sumo (name) &7- Toggle sumo mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit setLoadout &7- Sets the loadout of the kit as your inventory."));
        player.sendMessage(CC.translate("&7» &b/kit getLoadout &7- Get the loadout of the kit."));
        player.sendMessage(CC.translate("&7» &b/kit list &7- Lists All Kits"));
        player.sendMessage(CC.translate("&7» &b/kit save &7- Save All the Kits"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
    }
}
