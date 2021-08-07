package xyz.refinedev.practice.cmds.event;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.EventManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.other.Cooldown;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 7/12/2021
 * Project: Array
 */

public class SumoCommands {

    private final static Array plugin = Array.getInstance();
    private final EventManager manager = plugin.getEventManager();


    @Command(name = "", aliases = "help", desc = "View Sumo Commands")
    @Require("array.event.admin")
    public void help(@Sender Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c&lSumo &8(&7&o&7Commands List&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &7* &c/sumo host &8(&7&o&7Host a Sumo Event&8)"));
        player.sendMessage(CC.translate(" &7* &c/sumo setknockback &8<&7knockback&8> &8(&7&o&7Set Sumo Knockback Profile&8)"));
        player.sendMessage(CC.translate(" &7* &c/sumo tp &8(&7&o&7Teleport to the Sumo Event Arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/sumo setspawn &8<&7one|two|spec&8> &8(&7&o&7Set the spawn locations for Sumo&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "knockback", aliases = {"setkb", "kb"}, usage = "<knockback>", desc = "Force start an on-going Sumo Event")
    @Require("array.event.admin")
    public void knockback(@Sender CommandSender player, String kb) {
        manager.setSumoKB(kb);
        manager.save();
        player.sendMessage(Locale.EVENT_KNOCKBACK.toString().replace("<knockback>", kb));
    }

    @Command(name = "setspawn", aliases = "location", desc = "Set Sumo's Spawn Points", usage = "<one|two|spec>")
    @Require("array.event.admin")
    public void setSpawn(@Sender Player player, String position) {
        switch (position) {
            case "one": {
                manager.setSumoSpawn1(player.getLocation());
                break;
            }
            case "two": {
                manager.setSumoSpawn2(player.getLocation());
                break;
            }
            case "spec": {
                manager.setSumoSpectator(player.getLocation());
                break;
            }
            default: {
                player.sendMessage(CC.translate("&7The position must be &cone&7/&ctwo&7/&cspec&7."));
                return;
            }
        }

        player.sendMessage(Locale.EVENT_SPAWN.toString().replace("<position>", position));
        manager.save();
    }

    @Command(name = "tp", aliases = "teleport", desc = "Teleport to Sumo's Spawn Location")
    @Require("array.event.admin")
    public void teleport(@Sender Player player) {
        player.teleport(manager.getSumoSpectator());
        player.sendMessage(Locale.EVENT_TELEPORT.toString().replace("<event_name>", "Sumo"));
    }
}
