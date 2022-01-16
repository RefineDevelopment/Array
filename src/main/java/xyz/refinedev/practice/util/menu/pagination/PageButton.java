package xyz.refinedev.practice.util.menu.pagination;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;

public class PageButton extends Button {

    private final Array plugin;
    private final int mod;
    private final PaginatedMenu menu;

    public PageButton(Array plugin, int mod, PaginatedMenu menu) {
        super(plugin);
        this.plugin = plugin;
        this.mod = mod;
        this.menu = menu;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
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
    public void clicked(Player player, ClickType clickType) {
        if (clickType == ClickType.RIGHT) {
            new ViewAllPagesMenu(plugin, this.menu).openMenu(player);
            playNeutral(player);
        } else {
            if (hasNext(player)) {
                this.menu.modPage(player, this.mod);
                Button.playNeutral(player);
            } else {
                Button.playFail(player);
            }
        }
    }

    private boolean hasNext(Player player) {
        int pg = this.menu.getPage() + this.mod;
        return pg > 0 && this.menu.getPages(player) >= pg;
    }

}
