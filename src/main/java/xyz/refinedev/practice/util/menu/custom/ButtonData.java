package xyz.refinedev.practice.util.menu.custom;

import lombok.Data;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.util.menu.custom.action.ActionData;

import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/7/2021
 * Project: Array
 */

@Data
public class ButtonData {

    private final int slot;
    private final ItemStack item;
    private final List<ActionData> actions;
}
