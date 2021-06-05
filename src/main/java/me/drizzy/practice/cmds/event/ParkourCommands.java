package me.drizzy.practice.cmds.event;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.events.menu.EventSelectKitMenu;
import me.drizzy.practice.events.types.parkour.Parkour;
import me.drizzy.practice.events.types.parkour.ParkourManager;
import me.drizzy.practice.events.types.parkour.ParkourState;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.annotation.Command;
import me.drizzy.practice.util.command.annotation.Require;
import me.drizzy.practice.util.command.annotation.Sender;
import me.drizzy.practice.util.other.Cooldown;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/30/2021
 * Project: Array
 */

public class ParkourCommands {

    private final static Array plugin = Array.getInstance();
    private final ParkourManager manager = plugin.getParkourManager();


    @Command(name = "", aliases = "help", desc = "View Parkour Commands")
    @Require("array.event.admin")
    public void help(@Sender Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c&lParkour &8(&7&o&7Commands List&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/parkour cancel &8(&7&o&7Cancel current Parkour Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour cooldown &8(&7&o&7Reset the Parkour Event cooldown&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour host &8(&7&o&7Host a Parkour Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour forcestart &8(&7&o&7Force start a Parkour Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour join &8(&7&o&7Join ongoing Parkour Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour leave &8(&7&o&7Leave ongoing Parkour Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour tp &8(&7&o&7Teleport to the Parkour Event Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/parkour setspawn &8(&7&o&7Set the spawn location for Parkour&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "host", aliases = "start", desc = "Host a Parkour Event")
    @Require("array.host.Parkour")
    public void host(@Sender Player player) {
        new EventSelectKitMenu("Parkour").openMenu(player);
    }

    @Command(name = "cancel", aliases = {"abort", "forfeit"}, desc = "Cancel an on-going Parkour Event")
    @Require("array.event.admin")
    public void cancel(@Sender CommandSender sender) {
        if (manager.getActiveParkour() == null) {
            sender.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Parkour"));
            return;
        }
        Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
        manager.getActiveParkour().end(null);
    }

    @Command(name = "join", aliases = "participate", desc = "Join an on-going Parkour Event")
    public void join(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Parkour activeParkour = manager.getActiveParkour();

        if (profile.isBusy() || profile.getParty() != null) {
            player.sendMessage(Locale.EVENT_NOTABLE_JOIN.toString());
            return;
        }

        if (activeParkour == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Parkour"));
            return;
        }

        if (activeParkour.getState() != ParkourState.WAITING) {
            player.sendMessage(Locale.EVENT_ALREADY_STARTED.toString().replace("<event>", "Parkour"));
            return;
        }
        activeParkour.handleJoin(player);
    }


    @Command(name = "leave", aliases = "exit", desc = "Leave an on-going Parkour Event")
    @Require("array.event.admin")
    public void leave(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Parkour activeParkour = manager.getActiveParkour();

        if (activeParkour == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Parkour"));
            return;
        }

        if (!profile.isInParkour() || !activeParkour.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "Parkour"));
            return;
        }
        activeParkour.handleLeave(player);
    }

    @Command(name = "cooldown", desc = "Reset Parkour's Cooldown")
    @Require("array.event.admin")
    public void coolDown(@Sender CommandSender sender) {
        if (manager.getCooldown().hasExpired()) {
            sender.sendMessage(Locale.EVENT_NO_COOLDOWN.toString().replace("<event_name>", "Parkour"));
            return;
        }
        sender.sendMessage(Locale.EVENT_COOLDOW_RESET.toString().replace("<event_name>", "Parkour"));
        manager.setCooldown(new Cooldown(0));
    }

    @Command(name = "forcestart", desc = "Force start an on-going Parkour Event")
    @Require("array.event.admin")
    public void forceStart(@Sender CommandSender player) {
        Parkour activeParkour = manager.getActiveParkour();

        if (activeParkour == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Parkour"));
            return;
        }
        if (activeParkour.getState() == ParkourState.ROUND_FIGHTING) {
            player.sendMessage(Locale.EVENT_STARTED.toString());
        }
        activeParkour.onRound();
        player.sendMessage(Locale.EVENT_FORCESTART.toString().replace("<event_name>", "Parkour"));
    }

    @Command(name = "setspawn", aliases = "spawn", desc = "Set Parkour's Spawn Points")
    @Require("array.event.admin")
    public void setSpawn(@Sender Player player) {
        manager.setParkourSpawn(player.getLocation());
        player.sendMessage(Locale.EVENT_SPAWN.toString().replace("<position>", "Main"));
        manager.save();
    }

    @Command(name = "tp", aliases = "teleport", desc = "Teleport to Parkour's Spawn Location")
    @Require("array.event.admin")
    public void teleport(@Sender Player player) {
        player.teleport(manager.getParkourSpawn());
        player.sendMessage(Locale.EVENT_TELEPORT.toString().replace("<event_name>", "Parkour"));
    }
}    
