package xyz.refinedev.practice.cmds.event;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.menu.EventSelectKitMenu;
import xyz.refinedev.practice.events.types.lms.LMS;
import xyz.refinedev.practice.events.types.lms.LMSManager;
import xyz.refinedev.practice.events.types.lms.LMSState;
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

public class LMSCommands {

    private final static Array plugin = Array.getInstance();
    private final LMSManager manager = plugin.getLMSManager();


    @Command(name = "", aliases = "help", desc = "View LMSEvent Commands")
    @Require("array.event.admin")
    public void help(@Sender Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c&lLMS &8(&7&o&7Commands List&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/lms cancel &8(&7&o&7Cancel current LMSEvent Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms cooldown &8(&7&o&7Reset the LMSEvent Event cooldown&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms host &8(&7&o&7Host a LMSEvent Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms setknockback &8<&7knockback&8> &8(&7&o&7Set LMSEvent Knockback Profile&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms forcestart &8(&7&o&7Force start a LMSEvent Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms join &8(&7&o&7Join ongoing LMSEvent Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms leave &8(&7&o&7Leave ongoing LMSEvent Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms tp &8(&7&o&7Teleport to the LMSEvent Event Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/lms setspawn &8(&7&o&7Set the spawn location for LMSEvent&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "host", aliases = "start", desc = "Host a LMSEvent Event")
    @Require("array.host.lms")
    public void host(@Sender Player player) {
        new EventSelectKitMenu("LMSEvent").openMenu(player);
    }

    @Command(name = "cancel", aliases = {"abort", "forfeit"}, desc = "Cancel an on-going LMSEvent Event")
    @Require("array.event.admin")
    public void cancel(@Sender CommandSender sender) {
        if (manager.getActiveLMS() == null) {
            sender.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "LMSEvent"));
            return;
        }
        Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
        manager.getActiveLMS().end();
    }

    @Command(name = "join", aliases = "participate", desc = "Join an on-going LMSEvent Event")
    public void join(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        LMS activeLMS = manager.getActiveLMS();

        if (profile.isBusy() || profile.getParty() != null) {
            player.sendMessage(Locale.EVENT_NOTABLE_JOIN.toString());
            return;
        }

        if (activeLMS == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "LMSEvent"));
            return;
        }

        if (activeLMS.getState() != LMSState.WAITING) {
            player.sendMessage(Locale.EVENT_ALREADY_STARTED.toString().replace("<event>", "LMSEvent"));
            return;
        }
        activeLMS.handleJoin(player);
    }


    @Command(name = "leave", aliases = "exit", desc = "Leave an on-going LMSEvent Event")
    @Require("array.event.admin")
    public void leave(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        LMS activeLMS = manager.getActiveLMS();

        if (activeLMS == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "LMSEvent"));
            return;
        }

        if (!profile.isInLMS() || !activeLMS.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "LMSEvent"));
            return;
        }
        activeLMS.handleLeave(player);
    }

    @Command(name = "cooldown", desc = "Reset LMSEvent's Cooldown")
    @Require("array.event.admin")
    public void coolDown(@Sender CommandSender sender) {
        if (manager.getCooldown().hasExpired()) {
            sender.sendMessage(Locale.EVENT_NO_COOLDOWN.toString().replace("<event_name>", "LMSEvent"));
            return;
        }
        sender.sendMessage(Locale.EVENT_COOLDOW_RESET.toString().replace("<event_name>", "LMSEvent"));
        manager.setCooldown(new Cooldown(0));
    }

    @Command(name = "forcestart", desc = "Force start an on-going LMSEvent Event")
    @Require("array.event.admin")
    public void forceStart(@Sender CommandSender player) {
        LMS activeLMS = manager.getActiveLMS();

        if (activeLMS == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "LMSEvent"));
            return;
        }
        if (activeLMS.isFighting()) {
            player.sendMessage(Locale.EVENT_STARTED.toString());
        }
        activeLMS.onRound();
        player.sendMessage(Locale.EVENT_FORCESTART.toString().replace("<event_name>", "LMSEvent"));
    }

    @Command(name = "knockback", aliases = {"setkb", "kb"}, usage = "<knockback>", desc = "Force start an on-going LMSEvent Event")
    @Require("array.event.admin")
    public void knockback(@Sender CommandSender player, String kb) {
        manager.setLmsKnockbackProfile(kb);
        player.sendMessage(Locale.EVENT_KNOCKBACK.toString().replace("<knockback>", kb));
    }

    @Command(name = "setspawn", aliases = "spawn", desc = "Set LMSEvent's Spawn Points")
    @Require("array.event.admin")
    public void setSpawn(@Sender Player player) {
        manager.setLmsSpawn(player.getLocation());
        player.sendMessage(Locale.EVENT_SPAWN.toString().replace("<position>", "Main"));
        manager.save();
    }

    @Command(name = "tp", aliases = "teleport", desc = "Teleport to LMSEvent's Spawn Location")
    @Require("array.event.admin")
    public void teleport(@Sender Player player) {
        player.teleport(manager.getLmsSpawn());
        player.sendMessage(Locale.EVENT_TELEPORT.toString().replace("<event_name>", "LMSEvent"));
    }
}    