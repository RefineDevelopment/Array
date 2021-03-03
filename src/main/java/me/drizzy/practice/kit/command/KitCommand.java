package me.drizzy.practice.kit.command;

import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = { "kit", "kithelp" }, permission = "array.dev")
public class KitCommand {
    public void execute(final Player player) {
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        player.sendMessage(CC.translate( "&bArray &7» Kit Commands"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
        player.sendMessage(CC.translate("&b/kit create (name) &8- &8&o(&7&oCreate a Kit."));
        player.sendMessage(CC.translate("&b/kit setkb (name) &8- &8&o(&7&oSet a Kit's Knockback Profile."));
        player.sendMessage(CC.translate("&b/kit remove (name) &8- &8&o(&7&oDelete a Kit."));
        player.sendMessage(CC.translate("&b/kit hitdelay (name) (1-20) &8- &8&o(&7&oSet a Kit's Hitdelay"));
        player.sendMessage(CC.translate("&b/kit elo (name) &8- &8&o(&7&oToggle elo mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit build (name) &8- &8&o(&7&oToggle build mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit boxuhc (name) &8- &8&o(&7&oToggle boxuhc mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit combo (name) &8- &8&o(&7&oToggle combo mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit editable (name) &8- &8&o(&7&oToggle editable mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit lavakill (name) &8- &8&o(&7&oToggle lava-kill mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit parkour (name) &8- &8&o(&7&oToggle parkour mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit partyffa (name) &8- &8&o(&7&oToggle party-ffa mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit partysplit (name) &8- &8&o(&7&oToggle party-split mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit antifoodloss (name) &8- &8&o(&7&oToggle anti-food-loss mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit bowhp (name) &8- &8&o(&7&oToggle bow-hp mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit ffacenter (name) &8- &8&o(&7&oToggle ffa-center mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit healthregen (name) &8- &8&o(&7&oToggle health-regen mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit infinitespeed (name) &8- &8&o(&7&oToggle infinite-speed mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit infinitestrength (name) &8- &8&o(&7&oToggle infinite-strength mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit noitems (name) &8- &8&o(&7&oToggle no-items mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit showhealth (name) &8- &8&o(&7&oToggle show-health mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit stickspawn (name) &8- &8&o(&7&oToggle Stick-spawn mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit bedwars (name) &8- &8&o(&7&oToggle Bedwars mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit netheruhc (name) &8- &8&o(&7&oToggle Nether-UHC mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit boxuhc (name) &8- &8&o(&7&oToggle Box-UHC mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit spleef (name) &8- &8&o(&7&oToggle spleef mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit timed (name) &8- &8&o(&7&oToggle timed mode for a Kit."));
        player.sendMessage(CC.translate("&b/kit setLoadout &8- &8&o(&7&oSets the loadout of the kit as your inventory."));
        player.sendMessage(CC.translate("&b/kit getLoadout &8- &8&o(&7&oGet the loadout of the kit."));
        player.sendMessage(CC.translate("&b/kit list &8- &8&o(&7&oLists All Kits"));
        player.sendMessage(CC.translate("&b/kit save &8- &8&o(&7&oSave All the Kits"));
        player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
    }
}
