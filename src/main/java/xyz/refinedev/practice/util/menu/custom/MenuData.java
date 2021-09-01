package xyz.refinedev.practice.util.menu.custom;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/7/2021
 * Project: Array
 */

@Getter @Setter
public class MenuData {

    private String name;
    private int size;
    private String title;
    private boolean paginated, autoUpdate, placeholder;
    private ItemStack placeholderItem;
    private List<ButtonData> buttons;

}
