package me.drizzy.practice.cmds.event;

import me.drizzy.practice.events.menu.EventSelectEventMenu;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.annotation.Command;
import me.drizzy.practice.util.command.annotation.Sender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/29/2021
 * Project: Array
 */

public class EventCommands {

    @Command(name = "", desc = "View the Events Menu")
    public void menu(@Sender Player player) {
        new EventSelectEventMenu().openMenu(player);
    }

    @Command(name = "help", aliases = "event help", desc = "View Event Commands")
    public void help(@Sender CommandSender player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate( "&cArray &7» Event Commands"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/event &8(&7&oOpen Events Menu&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets &8(&7&oView Bracket Commands&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo &8(&7&oView Sumo Commands&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms &8(&7&oView LMS Commands&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour &8(&7&oView Parkour Commands&8)"));
        player.sendMessage(CC.translate(" &8• &c/gulag &8(&7&oView Gulag Commands&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }
}
