package xyz.refinedev.practice.util.menu.button;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.menu.Button;

@Getter @Setter
@RequiredArgsConstructor
public class DisplayButton extends Button {

    private final ItemStack itemStack;
    private final boolean cancel;

    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        if (this.itemStack == null) {
            return new ItemStack(Material.AIR);
        } else {
            return this.itemStack;
        }
    }

    @Override
    public boolean shouldCancel(Player player, ClickType clickType) {
        return this.cancel;
    }

}
