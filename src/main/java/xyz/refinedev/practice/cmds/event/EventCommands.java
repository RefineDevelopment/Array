package xyz.refinedev.practice.cmds.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.essentials.Essentials;
import xyz.refinedev.practice.events.Event;
import xyz.refinedev.practice.events.EventManager;
import xyz.refinedev.practice.events.EventState;
import xyz.refinedev.practice.events.EventType;
import xyz.refinedev.practice.events.impl.sumo.solo.SumoSolo;
import xyz.refinedev.practice.events.impl.sumo.team.SumoTeam;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;
import org.bukkit.command.CommandSender;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/29/2021
 * Project: Array
 */

public class EventCommands {

    private final EventManager eventManager = Array.getInstance().getEventManager();

    @Command(name = "", aliases = "help", desc = "View Event Commands")
    public void help(@Sender CommandSender player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate( "&cArray &7» Event Commands"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/event &8(&7&oView this message&8)"));
        player.sendMessage(CC.translate(" &8• &c/event host <event> &8(&7&oHost or View Team Selection Menu&8)"));
        player.sendMessage(CC.translate(" &8• &c/event forcestart &8(&7&oForcestart an active event&8)"));
        player.sendMessage(CC.translate(" &8• &c/event stop &8(&7&oStop an active event&8)"));
        player.sendMessage(CC.translate(" &8• &c/event join &8(&7&oJoin an active event&8)"));
        player.sendMessage(CC.translate(" &8• &c/event leave &8(&7&oLeave an active event&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "host", aliases = "start", desc = "Host an Event")
    public void host(@Sender Player player, EventType type) {
        if (eventManager.getActiveEvent() != null) {
            player.sendMessage(Locale.EVENT_ON_GOING.toString());
            return;
        }
        if (!eventManager.getEventCooldown().hasExpired()) {
            player.sendMessage(Locale.EVENT_COOLDOWN_ACTIVE.toString().replace("<expire_time>", Array.getInstance().getEventManager().getEventCooldown().getTimeLeft()));
            return;
        }
        switch (type) {
            case SUMO_SOLO: {
                if (!player.hasPermission("*") && !player.isOp() && !player.hasPermission("array.event.sumosolo")) {
                    player.sendMessage(Locale.EVENT_NO_PERMISSION.toString().replace("<store>", Essentials.getSocialMeta().getStore()));
                    return;
                }
                eventManager.setActiveEvent(new SumoSolo(player, 100));
                break;
            }
            case SUMO_TEAM: {
                if (!player.hasPermission("*") && !player.isOp() && !player.hasPermission("array.event.sumoteam")) {
                    player.sendMessage(Locale.EVENT_NO_PERMISSION.toString().replace("<store>", Essentials.getSocialMeta().getStore()));
                    return;
                }
                eventManager.setActiveEvent(new SumoTeam(player, 100));
                break;
            }
            case BRACKETS_SOLO:
            case BRACKETS_TEAM:
            case GULAG_SOLO:
            case GULAG_TEAM:
                break;
        }
        Bukkit.getOnlinePlayers().stream().map(Profile::getByPlayer).filter(profile -> profile.isInLobby() && !profile.getKitEditor().isActive()).forEach(Profile::refreshHotbar);
    }

    @Command(name = "cancel", aliases = "stop", desc = "Cancel an ongoing event")
    @Require("array.event.admin")
    public void cancel(@Sender CommandSender sender) {
        Event event = Array.getInstance().getEventManager().getActiveEvent();
        if (event == null) {
            sender.sendMessage(Locale.ERROR_NOTACTIVE.toString());
            return;
        }

        Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
        event.end();
    }

    @Command(name = "join", aliases = "participate", desc = "Join an active event")
    public void join(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Event event = Array.getInstance().getEventManager().getActiveEvent();

        if (profile.isBusy() || profile.getParty() != null) {
            player.sendMessage(Locale.EVENT_NOTABLE_JOIN.toString());
            return;
        }
        if (event == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString());
            return;
        }
        if (event.getState() != EventState.WAITING) {
            player.sendMessage(Locale.EVENT_ALREADY_STARTED.toString());
            return;
        }
        if (event.getEventManager().isUnfinished(event)) {
            player.sendMessage(Locale.EVENT_NOT_SETUP.toString());
            event.end();
            return;
        }

        event.handleJoin(player);
    }

    @Command(name = "leave", aliases = "quit", desc = "Leave an active event")
    public void leave(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Event event = Array.getInstance().getEventManager().getActiveEvent();

        if (event == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString());
            return;
        }
        if (!profile.isInEvent() || !event.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(Locale.ERROR_NOTPARTOF.toString());
            return;
        }
        event.handleLeave(player);
    }

    @Command(name = "forcestart", desc = "Force start the on-going event")
    @Require("array.event.admin")
    public void forceStart(@Sender CommandSender player) {
        Event event = Array.getInstance().getEventManager().getActiveEvent();

        if (event == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString());
            return;
        }
        if (event.getState() != EventState.WAITING) {
            player.sendMessage(Locale.EVENT_ALREADY_STARTED.toString());
            return;
        }
        event.onRound();
    }
}
