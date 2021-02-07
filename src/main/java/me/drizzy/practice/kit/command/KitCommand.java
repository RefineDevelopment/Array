package me.drizzy.practice.kit.command;

import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = { "kit", "kithelp" }, permission = "practice.dev")
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
        player.sendMessage(CC.translate("&7» &b/kit combo (name) &7- Toggle combo mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit editable (name) &7- Toggle editable mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit lavakill (name) &7- Toggle lava-kill mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit parkour (name) &7- Toggle parkour mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit partyffa (name) &7- Toggle party-ffa mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit partysplit (name) &7- Toggle party-split mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit antifoodloss (name) &7- Toggle anti-food-loss mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit bowhp (name) &7- Toggle bow-hp mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit ffacenter (name) &7- Toggle ffa-center mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit healthregen (name) &7- Toggle health-regen mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit infinitespeed (name) &7- Toggle infinite-speed mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit infinitestrength (name) &7- Toggle infinite-strength mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit noitems (name) &7- Toggle no-items mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit showhealth (name) &7- Toggle show-health mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit stickspawn (name) &7- Toggle Stick-spawn mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit bedwars (name) &7- Toggle Bedwars mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit netheruhc (name) &7- Toggle Nether-UHC mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit boxuhc (name) &7- Toggle Box-UHC mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit spleef (name) &7- Toggle spleef mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit timed (name) &7- Toggle timed mode for a Kit."));
        player.sendMessage(CC.translate("&7» &b/kit setLoadout &7- Sets the loadout of the kit as your inventory."));
        player.sendMessage(CC.translate("&7» &b/kit getLoadout &7- Get the loadout of the kit."));
        player.sendMessage(CC.translate("&7» &b/kit list &7- Lists All Kits"));
        player.sendMessage(CC.translate("&7» &b/kit save &7- Save All the Kits"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
    }
}
