package xyz.refinedev.practice.cmds.event;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.types.gulag.Gulag;
import xyz.refinedev.practice.events.types.gulag.GulagManager;
import xyz.refinedev.practice.events.types.gulag.GulagState;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.other.Cooldown;
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

public class GulagCommands {

    private final static Array plugin = Array.getInstance();
    private final GulagManager manager = plugin.getGulagManager();


    @Command(name = "", aliases = "help", desc = "View Gulag Commands")
    @Require("array.event.admin")
    public void help(@Sender Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c&lGulag &8(&7&o&7Commands List&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/gulag cancel &8(&7&o&7Cancel current Gulag Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/gulag cooldown &8(&7&o&7Reset the Gulag Event cooldown&8)"));
        player.sendMessage(CC.translate(" &8• &c/gulag host &8(&7&o&7Host a Gulag Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/gulag setknockback &8<&7knockback&8> &8(&7&o&7Set Gulag Knockback Profile&8)"));
        player.sendMessage(CC.translate(" &8• &c/gulag forcestart &8(&7&o&7Force start a Gulag Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/gulag join &8(&7&o&7Join ongoing Gulag Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/gulag leave &8(&7&o&7Leave ongoing Gulag Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/gulag tp &8(&7&o&7Teleport to the Gulag Event Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/gulag setspawn &8<&7one|two|spec&8> &8(&7&o&7Set the spawn locations for Gulag&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "host", aliases = "start", desc = "Host a Gulag Event")
    @Require("array.host.gulag")
    public void host(@Sender Player player) {
        if (manager.getActiveGulag() != null) {
            player.sendMessage(Locale.EVENT_ON_GOING.toString().replace("<event>", "Gulag"));
            return;
        }
        if (!manager.getCooldown().hasExpired()) {
            player.sendMessage(Locale.EVENT_COOLDOWN_ACTIVE.toString().replace("<event>", "Gulag"));
            return;
        }
        Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
        manager.setActiveGulag(new Gulag(player));
    }

    @Command(name = "cancel", aliases = {"abort", "forfeit"}, desc = "Cancel an on-going Gulag Event")
    @Require("array.event.admin")
    public void cancel(@Sender CommandSender sender) {
        if (manager.getActiveGulag() == null) {
            sender.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Gulag"));
            return;
        }
        Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
        manager.getActiveGulag().end();
    }

    @Command(name = "join", aliases = " participate", desc = "Join an on-going Gulag Event")
    public void join(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Gulag activeGulag = manager.getActiveGulag();

        if (profile.isBusy() || profile.getParty() != null) {
            player.sendMessage(Locale.EVENT_NOTABLE_JOIN.toString());
            return;
        }

        if (activeGulag == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Gulag"));
            return;
        }

        if (activeGulag.getState() != GulagState.WAITING) {
            player.sendMessage(Locale.EVENT_ALREADY_STARTED.toString().replace("<event>", "Gulag"));
            return;
        }
        activeGulag.handleJoin(player);
    }


    @Command(name = "leave", aliases = "exit", desc = "Leave an on-going Gulag Event")
    @Require("array.event.admin")
    public void leave(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Gulag activeGulag = manager.getActiveGulag();

        if (activeGulag == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Gulag"));
            return;
        }

        if (!profile.isInGulag() || !activeGulag.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "Gulag"));
            return;
        }
        activeGulag.handleLeave(player);
    }

    @Command(name = "cooldown", desc = "Reset Gulag's Cooldown")
    @Require("array.event.admin")
    public void coolDown(@Sender CommandSender sender) {
        if (manager.getCooldown().hasExpired()) {
            sender.sendMessage(Locale.EVENT_NO_COOLDOWN.toString().replace("<event_name>", "Gulag"));
            return;
        }
        sender.sendMessage(Locale.EVENT_COOLDOW_RESET.toString().replace("<event_name>", "Gulag"));
        manager.setCooldown(new Cooldown(0));
    }

    @Command(name = "forcestart", desc = "Force start an on-going Gulag Event")
    @Require("array.event.admin")
    public void forceStart(@Sender CommandSender player) {
        Gulag activeGulag = manager.getActiveGulag();

        if (activeGulag == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Gulag"));
            return;
        }
        if (activeGulag.isFighting()) {
            player.sendMessage(Locale.EVENT_STARTED.toString());
        }
        activeGulag.onRound();
        player.sendMessage(Locale.EVENT_FORCESTART.toString().replace("<event_name>", "Gulag"));
    }

    @Command(name = "knockback", aliases = {"setkb", "kb"},usage = "<knockback>", desc = "Force start an on-going Gulag Event")
    @Require("array.event.admin")
    public void knockback(@Sender CommandSender player, String kb) {
        manager.setGulagKnockbackProfile(kb);
        player.sendMessage(Locale.EVENT_KNOCKBACK.toString().replace("<knockback>", kb));
    }

    @Command(name = "setspawn", desc = "Set Gulag's Spawn Points", usage = "<location>")
    @Require("array.event.admin")
    public void setSpawn(@Sender Player player, String position) {
        switch (position) {
            case "one": {
                manager.setGulagSpawn1(player.getLocation());
                break;
            }
            case "two": {
                manager.setGulagSpawn2(player.getLocation());
                break;
            }
            case "spec": {
                manager.setGulagSpectator(player.getLocation());
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

    @Command(name = "tp", aliases = "teleport", desc = "Teleport to Gulag's Spawn Location")
    @Require("array.event.admin")
    public void teleport(@Sender Player player) {
        player.teleport(manager.getGulagSpectator());
        player.sendMessage(Locale.EVENT_TELEPORT.toString().replace("<event_name>", "Gulag"));
    }
}

