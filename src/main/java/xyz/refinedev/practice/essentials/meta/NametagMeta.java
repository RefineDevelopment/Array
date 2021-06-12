package xyz.refinedev.practice.essentials.meta;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 4/19/2021
 * Project: Array
 */

@Getter @Setter
public class NametagMeta {

    public boolean enabled = true;
    public String defaultColor = "<rank_color>";
    public ChatColor partyColor = ChatColor.BLUE;
    public ChatColor eventColor = ChatColor.RED;



}