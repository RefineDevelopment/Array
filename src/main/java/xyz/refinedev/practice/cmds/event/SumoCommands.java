package xyz.refinedev.practice.cmds.event;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.types.sumo.Sumo;
import xyz.refinedev.practice.events.types.sumo.SumoManager;
import xyz.refinedev.practice.events.types.sumo.SumoState;
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

public class SumoCommands {

    private final static Array plugin = Array.getInstance();
    private final SumoManager manager = plugin.getSumoManager();


    @Command(name = "", aliases = "help", desc = "View Sumo Commands")
    @Require("array.event.admin")
    public void help(@Sender Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c&lSumo &8(&7&o&7Commands List&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/sumo cancel &8(&7&o&7Cancel current Sumo Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo cooldown &8(&7&o&7Reset the Sumo Event cooldown&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo host &8(&7&o&7Host a Sumo Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo setknockback &8<&7knockback&8> &8(&7&o&7Set Sumo Knockback Profile&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo forcestart &8(&7&o&7Force start a Sumo Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo join &8(&7&o&7Join ongoing Sumo Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo leave &8(&7&o&7Leave ongoing Sumo Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo tp &8(&7&o&7Teleport to the Sumo Event Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/sumo setspawn &8<&7one|two|spec&8> &8(&7&o&7Set the spawn locations for Sumo&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "host", aliases = "start", desc = "Host a Sumo Event")
    @Require("array.host.sumo")
    public void host(@Sender Player player) {
        if (manager.getActiveSumo() != null) {
            player.sendMessage(Locale.EVENT_ON_GOING.toString().replace("<event>", "Sumo"));
            return;
        }
        if (!manager.getCooldown().hasExpired()) {
            player.sendMessage(Locale.EVENT_COOLDOWN_ACTIVE.toString().replace("<event>", "Sumo"));
            return;
        }
        Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
        manager.setActiveSumo(new Sumo(player));
    }

    @Command(name = "cancel", aliases = {"abort", "forfeit"}, desc = "Cancel an on-going Sumo Event")
    @Require("array.event.admin")
    public void cancel(@Sender CommandSender sender) {
        if (manager.getActiveSumo() == null) {
            sender.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Sumo"));
            return;
        }
        Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
        manager.getActiveSumo().end();
    }

    @Command(name = "join", aliases = {"join", "participate"}, desc = "Join an on-going Sumo Event")
    public void join(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Sumo activeSumo = manager.getActiveSumo();

        if (profile.isBusy() || profile.getParty() != null) {
            player.sendMessage(Locale.EVENT_NOTABLE_JOIN.toString());
            return;
        }

        if (activeSumo == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Sumo"));
            return;
        }

        if (activeSumo.getState() != SumoState.WAITING) {
            player.sendMessage(Locale.EVENT_ALREADY_STARTED.toString().replace("<event>", "Sumo"));
            return;
        }
        activeSumo.handleJoin(player);
    }


    @Command(name = "leave", aliases = "exit", desc = "Leave an on-going Sumo Event")
    @Require("array.event.admin")
    public void leave(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Sumo activeSumo = manager.getActiveSumo();

        if (activeSumo == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Sumo"));
            return;
        }

        if (!profile.isInSumo() || !activeSumo.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "Sumo"));
            return;
        }
        activeSumo.handleLeave(player);
    }

    @Command(name = "cooldown", desc = "Reset Sumo's Cooldown")
    @Require("array.event.admin")
    public void coolDown(@Sender CommandSender sender) {
        if (manager.getCooldown().hasExpired()) {
            sender.sendMessage(Locale.EVENT_NO_COOLDOWN.toString().replace("<event_name>", "Sumo"));
            return;
        }
        sender.sendMessage(Locale.EVENT_COOLDOW_RESET.toString().replace("<event_name>", "Sumo"));
        manager.setCooldown(new Cooldown(0));
    }

    @Command(name = "forcestart", desc = "Force start an on-going Sumo Event")
    @Require("array.event.admin")
    public void forceStart(@Sender CommandSender player) {
        Sumo activeSumo = manager.getActiveSumo();

        if (activeSumo == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Sumo"));
            return;
        }
        if (activeSumo.isFighting()) {
            player.sendMessage(Locale.EVENT_STARTED.toString());
        }
        activeSumo.onRound();
        player.sendMessage(Locale.EVENT_FORCESTART.toString().replace("<event_name>", "Sumo"));
    }

    @Command(name = "knockback", aliases = {"setkb", "kb"}, usage = "<knockback>", desc = "Force start an on-going Sumo Event")
    @Require("array.event.admin")
    public void knockback(@Sender CommandSender player, String kb) {
        manager.setSumoKnockbackProfile(kb);
        player.sendMessage(Locale.EVENT_KNOCKBACK.toString().replace("<knockback>", kb));
    }

    @Command(name = "setspawn", aliases = "location", desc = "Set Sumo's Spawn Points", usage = "<location>")
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
