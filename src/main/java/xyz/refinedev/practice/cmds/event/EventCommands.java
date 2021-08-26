package xyz.refinedev.practice.cmds.event;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.Event;
import xyz.refinedev.practice.events.EventManager;
import xyz.refinedev.practice.events.EventState;
import xyz.refinedev.practice.events.EventType;
import xyz.refinedev.practice.events.impl.sumo.solo.SumoSolo;
import xyz.refinedev.practice.events.impl.sumo.team.SumoTeam;
import xyz.refinedev.practice.events.menu.EventTeamMenu;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;

import java.util.stream.Collectors;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/29/2021
 * Project: Array
 */

public class EventCommands {

    private final Array plugin = Array.getInstance();
    private final EventManager eventManager = plugin.getEventManager();

    @Command(name = "", aliases = "help", desc = "View Event Commands")
    public void help(@Sender CommandSender sender) {
        Locale.EVENT_HELP.toList().forEach(sender::sendMessage);
    }

    @Command(name = "teamselect", aliases = "teams", desc = "Choose a Team for your Event")
    public void teamSelect(@Sender Player player) {
        final Profile profile = Profile.getByPlayer(player);
        final Event activeEvent = eventManager.getActiveEvent();
        if (activeEvent == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString());
            return;
        }
        if (!profile.isInEvent() || !activeEvent.getPlayers().contains(player)) {
            player.sendMessage(Locale.ERROR_NOTPARTOF.toString());
            return;
        }
        if (!activeEvent.isTeam()) {
            player.sendMessage(Locale.EVENT_NOT_TEAM.toString());
            return;
        }
        new EventTeamMenu(activeEvent).openMenu(player);
    }

    @Command(name = "host", aliases = "start", usage = "<event>", desc = "Host an Event")
    public void host(@Sender Player player, EventType type) {
        if (eventManager.getActiveEvent() != null) {
            player.sendMessage(Locale.EVENT_ON_GOING.toString());
            return;
        }
        if (!eventManager.getEventCooldown().hasExpired()) {
            player.sendMessage(Locale.EVENT_COOLDOWN_ACTIVE.toString().replace("<expire_time>", eventManager.getEventCooldown().getTimeLeft()));
            return;
        }
        switch (type) {
            case SUMO_SOLO: {
                if (!player.hasPermission("*") && !player.isOp() && !player.hasPermission("array.event.sumosolo")) {
                    player.sendMessage(Locale.EVENT_NO_PERMISSION.toString().replace("<store>", plugin.getConfigHandler().getSTORE()));
                    return;
                }
                eventManager.setActiveEvent(new SumoSolo(player, 100));
                break;
            }
            case SUMO_TEAM: {
                if (!player.hasPermission("*") && !player.isOp() && !player.hasPermission("array.event.sumoteam")) {
                    player.sendMessage(Locale.EVENT_NO_PERMISSION.toString().replace("<store>", plugin.getConfigHandler().getSTORE()));
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
        final Event event = Array.getInstance().getEventManager().getActiveEvent();
        if (event == null) {
            sender.sendMessage(Locale.ERROR_NOTACTIVE.toString());
            return;
        }

        Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
        event.end();
    }

    @Command(name = "join", aliases = "participate", desc = "Join an active event")
    public void join(@Sender Player player) {
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        final Event event =  eventManager.getActiveEvent();

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
        final Profile profile = Profile.getByUuid(player.getUniqueId());
        final Event event = eventManager.getActiveEvent();
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
        final Event event = eventManager.getActiveEvent();
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

    @Command(name = "info", aliases = "information", desc = "View Information about the current event")
    public void info(@Sender CommandSender sender) {
        final Event event = eventManager.getActiveEvent();
        if (event == null) {
            sender.sendMessage(Locale.ERROR_NOTACTIVE.toString());
            return;
        }

        Locale.EVENT_INFO.toList().stream().map(line -> {
            return line
                    .replace("<event_state>", event.getState().name())
                    .replace("<event_host>", event.getHost().getUsername())
                    .replace("<event_alive_players>", String.valueOf(event.getRemainingPlayers().size()))
                    .replace("<event_max_players>", String.valueOf(event.getMaxPlayers()))
                    .replace("<event_type>", event.getType().getReadable())
                    .replace("<event_name>", event.getName());

        }).collect(Collectors.toList()).forEach(sender::sendMessage);
    }
}
