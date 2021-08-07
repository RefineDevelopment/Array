package xyz.refinedev.practice.util.menu.custom;

import lombok.Data;

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
public class MenuData {

    private String name;
    private int size;
    private String title;
    private boolean paginated;
    private List<ButtonData> buttons;

}
