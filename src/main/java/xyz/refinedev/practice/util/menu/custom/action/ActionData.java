package xyz.refinedev.practice.util.menu.custom.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/7/2021
 * Project: Array
 */

@Getter @Setter
@RequiredArgsConstructor
public class ActionData {

    private final ActionType type;
    private final String clickType;
    private final String action;

}
