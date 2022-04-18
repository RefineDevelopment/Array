package xyz.refinedev.practice.util.menu.pagination;

import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.button.BackButton;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ViewAllPagesMenu extends Menu {

    public PaginatedMenu menu;

    public ViewAllPagesMenu(PaginatedMenu menu) {
        this.menu = menu;
    }

    @Override
    public String getTitle(Array plugin, Player player) {
        return "&cJump to page";
    }

    @Override
    public Map<Integer, Button> getButtons(Array plugin, Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new BackButton(menu));

        int index = 10;

        for (int i = 1; i <= menu.getPages(plugin, player); i++) {
            buttons.put(index++, new JumpToPageButton(i, menu, menu.getPage() == i));

            if ((index - 8) % 9 == 0) {
                index += 2;
            }
        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

}
