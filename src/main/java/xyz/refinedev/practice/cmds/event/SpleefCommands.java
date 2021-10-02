package xyz.refinedev.practice.cmds.event;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.managers.EventManager;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/15/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class SpleefCommands {

    private final Array plugin;
    private final EventManager manager;

    @Command(name = "", aliases = "help", desc = "View Spleef Commands")
    @Require("array.event.admin")
    public void help(@Sender Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c&lSpleef &8(&7&o&7Commands List&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &7* &c/spleef host &8(&7&o&7Host a Spleef Event&8)"));
        player.sendMessage(CC.translate(" &7* &c/spleef setknockback &8<&7knockback&8> &8(&7&o&7Set Spleef Knockback Profile&8)"));
        player.sendMessage(CC.translate(" &7* &c/spleef tp &8(&7&o&7Teleport to the Spleef Event Arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/spleef setspawn &8(&7&o&7Set the spawn location for Spleef&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "host", desc = "Start a Spleef Event")
    @Require("array.event.admin")
    public void host(@Sender Player player) {
        manager.hostByType(player, EventType.SPLEEF);
    }

    @Command(name = "knockback", aliases = {"setkb", "kb"}, usage = "<knockback>", desc = "Set Spleef's Knockback")
    @Require("array.event.admin")
    public void knockback(@Sender CommandSender player, String kb) {
        manager.setSpleefKB(kb);
        manager.save();
        player.sendMessage(Locale.EVENT_KNOCKBACK.toString().replace("<knockback>", kb));
    }

    @Command(name = "setspawn", aliases = "location", desc = "Set Spleef's Spawn Point")
    @Require("array.event.admin")
    public void setSpawn(@Sender Player player) {
        manager.setSpleefSpawn(player.getLocation());
        player.sendMessage(Locale.EVENT_SPAWN.toString().replace("<position>", "Main"));
        manager.save();
    }

    @Command(name = "tp", aliases = "teleport", desc = "Teleport to Spleef's Spawn Location")
    @Require("array.event.admin")
    public void teleport(@Sender Player player) {
        if (manager.getSpleefSpawn() == null) {
            player.sendMessage("&cCould not teleport, spawn points are not setup");
            return;
        }

        player.teleport(manager.getSpleefSpawn());
        player.sendMessage(Locale.EVENT_TELEPORT.toString().replace("<event_name>", "Spleef"));
    }
}
