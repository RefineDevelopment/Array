package xyz.refinedev.practice.profile.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/17/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class WorldButton extends Button {

    private final World world;

    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        boolean isNether = world.getEnvironment() == World.Environment.NETHER;
        boolean isEnd = world.getEnvironment() == World.Environment.THE_END;

        return new ItemBuilder((isNether ? Material.NETHERRACK : isEnd ? Material.ENDER_STONE : Material.GRASS))
                .name((isNether ? "&c" : isEnd ? "&c" : "&a") + world.getName())
                .lore((player.getWorld() == world ? "&cYou are already in that world!" : "&7&oClick to teleport"))
                .build();
    }

    @Override
    public void clicked(Array plugin, Player player, ClickType clickType) {
        player.closeInventory();
        if (player.getWorld() != world) {
            player.teleport(world.getSpawnLocation());
            player.sendMessage(CC.translate("&7You are now in world &a" + world.getName() + "&7!"));
        }
    }
}
