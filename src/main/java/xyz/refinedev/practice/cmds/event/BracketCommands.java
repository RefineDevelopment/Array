package xyz.refinedev.practice.cmds.event;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.menu.EventSelectKitMenu;
import xyz.refinedev.practice.events.types.brackets.Brackets;
import xyz.refinedev.practice.events.types.brackets.BracketsManager;
import xyz.refinedev.practice.events.types.brackets.BracketsState;
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
 * Created at 5/29/2021
 * Project: Array
 */

public class BracketCommands {
    
    private final static Array plugin = Array.getInstance();
    private final BracketsManager manager = plugin.getBracketsManager();


    @Command(name = "", aliases = "help", desc = "View Bracket Commands")
    @Require("array.event.admin")
    public void help(@Sender Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&c&lBrackets &8(&7&o&7Commands List&8)"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/brackets cancel &8(&7&o&7Cancel current Brackets Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets cooldown &8(&7&o&7Reset the Brackets Event cooldown&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets host &8(&7&o&7Host a Brackets Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets setknockback &8<&7knockback&8> &8(&7&o&7Set Brackets Knockback Profile&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets forcestart &8(&7&o&7Force start a Brackets Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets join &8(&7&o&7Join ongoing Brackets Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets leave &8(&7&o&7Leave ongoing Brackets Event&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets tp &8(&7&o&7Teleport to the Brackets Event Arena&8)"));
        player.sendMessage(CC.translate(" &8• &c/brackets setspawn &8<&7one|two|spec&8> &8(&7&o&7Set the spawn locations for Brackets&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "host", aliases = "start", desc = "Host a Brackets Event")
    @Require("array.host.brackets")
    public void host(@Sender Player player) {
        new EventSelectKitMenu("Brackets").openMenu(player);
    }
    
    @Command(name = "cancel", aliases = {"abort", "forfeit"}, desc = "Cancel an on-going Brackets Event")
    @Require("array.event.admin")
    public void cancel(@Sender CommandSender sender) {
        if (manager.getActiveBrackets() == null) {
            sender.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Brackets"));
            return;
        }
        Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
        manager.getActiveBrackets().end();
    }

    @Command(name = "join", aliases = {"join", "participate"}, desc = "Join an on-going Brackets Event")
    public void join(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Brackets activeBrackets = manager.getActiveBrackets();

        if (profile.isBusy() || profile.getParty() != null) {
            player.sendMessage(Locale.EVENT_NOTABLE_JOIN.toString());
            return;
        }

        if (activeBrackets == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Brackets"));
            return;
        }

        if (activeBrackets.getState() != BracketsState.WAITING) {
            player.sendMessage(Locale.EVENT_ALREADY_STARTED.toString().replace("<event>", "Brackets"));
            return;
        }
        activeBrackets.handleJoin(player);
    }


    @Command(name = "leave", aliases = "exit", desc = "Leave an on-going Brackets Event")
    @Require("array.event.admin")
    public void leave(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Brackets activeBrackets = manager.getActiveBrackets();

        if (activeBrackets == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Brackets"));
            return;
        }

        if (!profile.isInBrackets() || !activeBrackets.getEventPlayers().containsKey(player.getUniqueId())) {
            player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "Brackets"));
            return;
        }
        activeBrackets.handleLeave(player);
    }

    @Command(name = "cooldown", desc = "Reset Bracket's Cooldown")
    @Require("array.event.admin")
    public void coolDown(@Sender CommandSender sender) {
        if (manager.getCooldown().hasExpired()) {
            sender.sendMessage(Locale.EVENT_NO_COOLDOWN.toString().replace("<event_name>", "Brackets"));
            return;
        }
        sender.sendMessage(Locale.EVENT_COOLDOW_RESET.toString().replace("<event_name>", "Brackets"));
        manager.setCooldown(new Cooldown(0));
    }

    @Command(name = "forcestart", desc = "Force start an on-going Brackets Event")
    @Require("array.event.admin")
    public void forceStart(@Sender CommandSender player) {
        Brackets activeBrackets = manager.getActiveBrackets();

        if (activeBrackets == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Brackets"));
            return;
        }
        if (activeBrackets.isFighting()) {
            player.sendMessage(Locale.EVENT_STARTED.toString());
        }
        activeBrackets.onRound();
        player.sendMessage(Locale.EVENT_FORCESTART.toString().replace("<event_name>", "Brackets"));
    }

    @Command(name = "knockback", aliases = {"setkb", "kb"}, usage = "<knockback>", desc = "Force start an on-going Brackets Event")
    @Require("array.event.admin")
    public void knockback(@Sender CommandSender player, String kb) {
        manager.setBracketsKnockbackProfile(kb);
        player.sendMessage(Locale.EVENT_KNOCKBACK.toString().replace("<knockback>", kb));
    }

    @Command(name = "setspawn", aliases = "location", desc = "Set Bracket's Spawn Points", usage = "<location>")
    @Require("array.event.admin")
    public void setSpawn(@Sender Player player, String position) {
        switch (position) {
            case "one": {
                manager.setBracketsSpawn1(player.getLocation());
                break;
            }
            case "two": {
                manager.setBracketsSpawn2(player.getLocation());
                break;
            }
            case "spec": {
                manager.setBracketsSpectator(player.getLocation());
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

    @Command(name = "tp", aliases = "teleport", desc = "Teleport to Bracket's Spawn Location")
    @Require("array.event.admin")
    public void teleport(@Sender Player player) {
        player.teleport(manager.getBracketsSpectator());
        player.sendMessage(Locale.EVENT_TELEPORT.toString().replace("<event_name>", "Brackets"));
    }
}
