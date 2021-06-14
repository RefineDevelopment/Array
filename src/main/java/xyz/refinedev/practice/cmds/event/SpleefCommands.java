package xyz.refinedev.practice.cmds.event;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.menu.EventSelectKitMenu;
import xyz.refinedev.practice.events.types.spleef.Spleef;
import xyz.refinedev.practice.events.types.spleef.SpleefManager;
import xyz.refinedev.practice.events.types.spleef.SpleefState;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.other.Cooldown;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/30/2021
 * Project: Array
 */

public class SpleefCommands {

    private final static Array plugin = Array.getInstance();
    private final SpleefManager manager = plugin.getSpleefManager();


    @Command(name = "", aliases = "help", desc = "View Spleef Commands")
    @Require("array.event.admin")
    public void help(@Sender Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c&lSpleef &8(&7&o&7Commands List&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/spleef cancel &8(&7&o&7Cancel current Spleef Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef cooldown &8(&7&o&7Reset the Spleef Event cooldown&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef host &8(&7&o&7Host a Spleef Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef setknockback &8<&7knockback&8> &8(&7&o&7Set Spleef Knockback Profile&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef forcestart &8(&7&o&7Force start a Spleef Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef join &8(&7&o&7Join ongoing Spleef Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef leave &8(&7&o&7Leave ongoing Spleef Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef tp &8(&7&o&7Teleport to the Spleef Event Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/spleef setspawn &8(&7&o&7Set the spawn location for Spleef&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "host", aliases = "start", desc = "Host a Spleef Event")
    @Require("array.host.Spleef")
    public void host(@Sender Player player) {
        new EventSelectKitMenu("Spleef").openMenu(player);
    }

    @Command(name = "cancel", aliases = {"abort", "forfeit"}, desc = "Cancel an on-going Spleef Event")
    @Require("array.event.admin")
    public void cancel(@Sender CommandSender sender) {
        if (manager.getActiveSpleef() == null) {
            sender.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Spleef"));
            return;
        }
        Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
        manager.getActiveSpleef().end();
    }

    @Command(name = "knockback", aliases = {"setkb", "kb"}, usage = "<knockback>", desc = "Force start an on-going Spleef Event")
    @Require("array.event.admin")
    public void knockback(@Sender CommandSender player, String kb) {
        manager.setSpleefKnockbackProfile(kb);
        player.sendMessage(Locale.EVENT_KNOCKBACK.toString().replace("<knockback>", kb));
    }

    @Command(name = "join", aliases = "participate", desc = "Join an on-going Spleef Event")
    public void join(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Spleef activeSpleef = manager.getActiveSpleef();

        if (profile.isBusy() || profile.getParty() != null) {
            player.sendMessage(Locale.EVENT_NOTABLE_JOIN.toString());
            return;
        }

        if (activeSpleef == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Spleef"));
            return;
        }

        if (activeSpleef.getState() != SpleefState.WAITING) {
            player.sendMessage(Locale.EVENT_ALREADY_STARTED.toString().replace("<event>", "Spleef"));
            return;
        }
        activeSpleef.handleJoin(player);
    }


    @Command(name = "leave", aliases = "exit", desc = "Leave an on-going Spleef Event")
    @Require("array.event.admin")
    public void leave(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Spleef activeSpleef = manager.getActiveSpleef();

        if (activeSpleef == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Spleef"));
            return;
        }

        if (!profile.isInSpleef() || !activeSpleef.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "Spleef"));
            return;
        }
        activeSpleef.handleLeave(player);
    }

    @Command(name = "cooldown", desc = "Reset Spleef's Cooldown")
    @Require("array.event.admin")
    public void coolDown(@Sender CommandSender sender) {
        if (manager.getCooldown().hasExpired()) {
            sender.sendMessage(Locale.EVENT_NO_COOLDOWN.toString().replace("<event_name>", "Spleef"));
            return;
        }
        sender.sendMessage(Locale.EVENT_COOLDOW_RESET.toString().replace("<event_name>", "Spleef"));
        manager.setCooldown(new Cooldown(0));
    }

    @Command(name = "forcestart", desc = "Force start an on-going Spleef Event")
    @Require("array.event.admin")
    public void forceStart(@Sender CommandSender player) {
        Spleef activeSpleef = manager.getActiveSpleef();

        if (activeSpleef == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Spleef"));
            return;
        }
        if (activeSpleef.getState() == SpleefState.ROUND_FIGHTING) {
            player.sendMessage(Locale.EVENT_STARTED.toString());
        }
        activeSpleef.onRound();
        player.sendMessage(Locale.EVENT_FORCESTART.toString().replace("<event_name>", "Spleef"));
    }

    @Command(name = "setspawn", aliases = "spawn", desc = "Set Spleef's Spawn Points")
    @Require("array.event.admin")
    public void setSpawn(@Sender Player player) {
        manager.setSpleefSpawn(player.getLocation());
        player.sendMessage(Locale.EVENT_SPAWN.toString().replace("<position>", "Main"));
        manager.save();
    }

    @Command(name = "tp", aliases = "teleport", desc = "Teleport to Spleef's Spawn Location")
    @Require("array.event.admin")
    public void teleport(@Sender Player player) {
        player.teleport(manager.getSpleefSpawn());
        player.sendMessage(Locale.EVENT_TELEPORT.toString().replace("<event_name>", "Spleef"));
    }
}    
