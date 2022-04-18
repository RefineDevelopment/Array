package xyz.refinedev.practice.util.menu.pagination;

import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.menu.Button;

import java.util.Arrays;

@AllArgsConstructor
public class JumpToPageButton extends Button {

    private final int page;
    private final PaginatedMenu menu;
    private final boolean current;

    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        ItemStack itemStack = new ItemStack(this.current ? Material.ENCHANTED_BOOK : Material.BOOK, this.page);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColor.RED + "Page " + this.page);

        if (this.current) {
            itemMeta.setLore(Arrays.asList(
                    "",
                    ChatColor.GREEN + "Current page"
            ));
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void clicked(Array plugin, Player player, ClickType clickType) {
        this.menu.modPage(plugin, player, this.page - this.menu.getPage());
        Button.playNeutral(player);
    }

}
