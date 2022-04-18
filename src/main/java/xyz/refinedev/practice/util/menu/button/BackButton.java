package xyz.refinedev.practice.util.menu.button;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.Arrays;

@RequiredArgsConstructor
public class BackButton extends Button {

    private final Menu back;

    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        return new ItemBuilder(Material.REDSTONE)
                .name(CC.RED + CC.BOLD + "Back")
                .lore(Arrays.asList(
                        CC.RED + "Click here to return to",
                        CC.RED + "the previous menu.")
                )
                .build();
    }

    @Override
    public void clicked(Array plugin, Player player, ClickType clickType) {
        Button.playNeutral(player);
        plugin.getMenuHandler().openMenu(back, player);
    }

}
