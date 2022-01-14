package xyz.refinedev.practice.party.menu.buttons;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.other.SkullCreator;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 1/14/2022
 * Project: Array
 */

@RequiredArgsConstructor
public class PartyListButton extends Button {

    private final Player pplayer;

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack stack = SkullCreator.itemFromUuid(pplayer.getUniqueId());
        return new ItemBuilder(stack)
                .name("&a" + this.pplayer.getName())
                .durability(3)
                .build();
    }
}
