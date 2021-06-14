package xyz.refinedev.practice.profile.menu;

import lombok.AllArgsConstructor;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/31/2021
 * Project: Array
 */

public class WorldsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&aWorlds");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for ( World world : Bukkit.getWorlds() ) {
            buttons.put(buttons.size(), new WorldButton(world));
        }
        return buttons;
    }

    @AllArgsConstructor
    private static class WorldButton extends Button {

        World world;

        @Override
        public ItemStack getButtonItem(Player player) {
            boolean isNether = world.getEnvironment() == World.Environment.NETHER;
            boolean isEnd = world.getEnvironment() == World.Environment.THE_END;

            return new ItemBuilder((isNether ? Material.NETHERRACK : isEnd ? Material.ENDER_STONE : Material.GRASS))
                    .name((isNether ? "&c" : isEnd ? "&c" : "&a") + world.getName())
                    .lore((player.getWorld() == world ? "&cYou are already in that world!" : "&7&oClick to teleport"))
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();
            if (player.getWorld() != world) {
                player.teleport(world.getSpawnLocation());
                player.sendMessage(CC.translate("&7You are now in world &a" + world.getName() + "&7!"));
            }
        }
    }
}
