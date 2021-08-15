package me.drizzy.practice.essentials.meta;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

/**
 * @author Drizzy
 * Created at 4/19/2021
 */
@Getter
@Setter
public class NametagMeta {

    public boolean enabled = true;
    public String defaultColor = "<rank_color>";
    public ChatColor partyColor = ChatColor.BLUE;
    public ChatColor eventColor = ChatColor.RED;



}