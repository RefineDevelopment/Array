package xyz.refinedev.practice.party.menu.buttons;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
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

    private final Player partyPlayer;

    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        ItemStack stack = SkullCreator.itemFromUuid(partyPlayer.getUniqueId());
        ItemBuilder itemBuilder = new ItemBuilder(stack);
        itemBuilder.name("&a" + this.partyPlayer.getName());
        itemBuilder.durability(3);
        return itemBuilder.build();
    }
}
