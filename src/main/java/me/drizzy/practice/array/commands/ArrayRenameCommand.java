package me.drizzy.practice.array.commands;

import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.util.chat.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@CommandMeta(label={"array rename"}, permission="array.dev")
public class ArrayRenameCommand {
    public void execute(Player p, @CPL("name") String name) {
        if (p.getItemInHand() == null || p.getItemInHand().getType().equals(Material.AIR)) {
            p.sendMessage(ChatColor.RED + "Hold something in your hand.");
            return;
        }
        ItemStack hand=p.getItemInHand();
        ItemMeta meta=hand.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name.replace("_", " ")));
        hand.setItemMeta(meta);
        p.getInventory().setItemInHand(hand);
        p.updateInventory();
        p.sendMessage(CC.translate("&8[&b&lArray&8] &aThe Item in your hand has been renamed!"));
    }
}
