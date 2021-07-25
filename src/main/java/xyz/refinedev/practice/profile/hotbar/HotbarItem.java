package xyz.refinedev.practice.profile.hotbar;

import lombok.Data;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/14/2021
 * Project: Array
 */

@Data
public class HotbarItem {

    private final HotbarType type;
    private final HotbarLayout layout;
    private final int slot;
    private String command;
}
