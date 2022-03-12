package xyz.refinedev.practice.util.menu.pagination;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

public class PageButton extends Button {

    private final int mod;
    private final PaginatedMenu menu;

    public PageButton(int mod, PaginatedMenu menu) {
        this.mod = mod;
        this.menu = menu;
    }

    @Override
    public ItemStack getButtonItem(Array plugin, Player player) {
        ItemBuilder builder = new ItemBuilder(Material.CARPET);
        if (this.hasNext(player)) {
            builder.name(this.mod > 0 ?  "§a⟶" : "§c⟵");
        } else {
            builder.name(ChatColor.GRAY + (this.mod > 0 ? "Last page" : "First page"));
        }
        builder.durability(this.hasNext(player) ? 11 : 7);

        return builder.build();
    }

    @Override
    public void clicked(Array plugin, Player player, ClickType clickType) {
        if (clickType == ClickType.RIGHT) {
            Menu menu = new ViewAllPagesMenu(this.menu);
            plugin.getMenuHandler().openMenu(menu, player);

            Button.playNeutral(player);
            return;
        }

        if (this.hasNext(player)) {
            this.menu.modPage(player, this.mod);
            Button.playNeutral(player);
            return;
        }
        Button.playFail(player);
    }

    private boolean hasNext(Player player) {
        int pg = this.menu.getPage() + this.mod;
        return pg > 0 && this.menu.getPages(player) >= pg;
    }

}
