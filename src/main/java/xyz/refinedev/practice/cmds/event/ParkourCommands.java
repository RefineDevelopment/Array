package xyz.refinedev.practice.cmds.event;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.EventTeamSize;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.managers.EventManager;
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
public class ParkourCommands {

    private final Array plugin;
    private final EventManager manager;

    @Command(name = "", aliases = "help", desc = "View Parkour Commands")
    @Require("array.event.admin")
    public void help(@Sender Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c&lParkour &8(&7&o&7Commands List&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &7* &c/parkour host &8(&7&o&7Host a Parkour Event&8)"));
        player.sendMessage(CC.translate(" &7* &c/parkour tp &8(&7&o&7Teleport to the Parkour Event Arena&8)"));
        player.sendMessage(CC.translate(" &7* &c/parkour setspawn &8(&7&o&7Set the spawn location for Parkour&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "host", desc = "Start a Parkour Event")
    @Require("array.event.admin")
    public void host(@Sender Player player) {
        manager.hostByType(player, EventType.PARKOUR, EventTeamSize.SOLO);
    }

    @Command(name = "setspawn", aliases = "location", desc = "Set Parkour Spawn Point")
    @Require("array.event.admin")
    public void setSpawn(@Sender Player player) {
        player.sendMessage(Locale.EVENT_SPAWN.toString().replace("<position>", "Main"));

        this.plugin.getEventManager().getHelper().setParkourSpawn(player.getLocation());
        this.manager.getHelper().save();
    }

    @Command(name = "tp", aliases = "teleport", desc = "Teleport to Parkour's Spawn Location")
    @Require("array.event.admin")
    public void teleport(@Sender Player player) {
        if (this.plugin.getEventManager().getHelper().getParkourSpawn() == null) {
            player.sendMessage("&cCould not teleport, spawn points are not setup");
            return;
        }

        player.teleport(this.plugin.getEventManager().getHelper().getParkourSpawn());
        player.sendMessage(Locale.EVENT_TELEPORT.toString().replace("<event_name>", "Parkour"));
    }
}
