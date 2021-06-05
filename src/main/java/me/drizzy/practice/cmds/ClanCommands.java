package me.drizzy.practice.cmds;

import com.mongodb.client.model.Filters;
import me.drizzy.practice.Array;
import me.drizzy.practice.api.ArrayCache;
import me.drizzy.practice.clan.Clan;
import me.drizzy.practice.clan.ClanProfileType;
import me.drizzy.practice.clan.meta.ClanInvite;
import me.drizzy.practice.clan.meta.ClanProfile;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.annotation.Command;
import me.drizzy.practice.util.command.annotation.OptArg;
import me.drizzy.practice.util.command.annotation.Sender;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * This Project is the property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/25/2021
 * Project: Array
 */

public class ClanCommands {

    @Command(name = "", desc = "View Clan Commands")
    public void clan(@Sender CommandSender player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&cArray &7» Clan Commands"));
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate(" &8• &c/clan create &8<&7name&8> &8(&7&oCreate a Clan&8)"));
        player.sendMessage(CC.translate(" &8• &c/clan disband &8(&7&oDisband your Clan&8)"));
        player.sendMessage(CC.translate(" &8• &c/clan chat &8(&7&oToggle your Clan Chat Mode&8)"));
        player.sendMessage(CC.translate(" &8• &c/clan accept &8<&7leader&8> &8(&7&oAccept a Clan Invitation&8)"));
        player.sendMessage(CC.translate(" &8• &c/clan leave &8(&7&oLeave your Current Clan&8)"));
        player.sendMessage(CC.translate(" &8• &c/clan info &8(&7&oView information about your Clan&8)"));
        player.sendMessage(CC.translate(" &8• &c/clan invite &8<&7profile&8> &8(&7&oInvite a Profile to your Clan&8)"));
        player.sendMessage(CC.translate(" &8• &c/clan kick &8<&7profile&8> &8(&7&oKick a Profile from your Clan&8)"));
        player.sendMessage(CC.translate(" &8• &c/clan ban &8<&7profile&8> &8(&7&oBan a Profile from your Clan&8)"));
        player.sendMessage(CC.translate(" &8• &c/clan promote &8<&7profile&8> &8(&7&oPromote a Profile to Captain&8)"));
        player.sendMessage(CC.translate(" &8• &c/clan demote &8<&7profile&8> &8(&7&oDemote a Profile to Member&8)"));
        player.sendMessage(CC.translate(" &8• &c/clan leader &8<&7profile&8> &8(&7&oPromote a Profile to Leader&8)"));
        player.sendMessage(CC.translate(" &8• &c/clan description &8<&7text&8> &8(&7&oSet your Clan's Description&8)"));
        player.sendMessage(CC.CHAT_BAR);
    }

    @Command(name = "create", aliases = "form", desc = "Create a Clan using a name", usage = "<name>")
    public void create(@Sender Player player, String name) {
        if (name.length() < 2) {
            player.sendMessage(CC.RED + "Clan names must be greater than or equal to 2 characters long.");
            return;
        }

        if (!StringUtils.isAlpha(name)) {
            player.sendMessage(CC.RED + "Clan names must only contain alpha characters (letters only).");
            return;
        }

        if (name.length() > 8) {
            player.sendMessage(CC.RED + "Clan names must be less than or equal to 8 characters long.");
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        if (profile.hasClan()) {
            player.sendMessage(CC.translate("&cYou are already a part of " + profile.getClan().getName() + " Clan!"));
            return;
        }

        Clan clan = Clan.getByName(name);

        if (clan != null) {
            player.sendMessage(CC.RED + "A clan with that name already exists!");
            return;
        }

        clan = new Clan(name, player.getUniqueId(), UUID.randomUUID());
        player.sendMessage(CC.translate("&aSuccessfully created a Clan with the name '" + name + "'&7."));
    }


    @Command(name = "accept", aliases = "join", usage = "<clan/leader> [password]", desc = "Join a Clan using its leader or name")
    public void accept(@Sender Player player, String text, @OptArg("none") String password) {
        Profile profile = Profile.getByPlayer(player);
        Clan clan;

        if (profile.getClan() != null) {
            player.sendMessage(CC.translate("&7You already have a clan."));
            return;
        }

        if (ArrayCache.getUUID(text) == null && Clan.getByName(text) == null) {
            player.sendMessage(CC.translate("&7That Clan does not exist."));
            return;
        }

        if (Clan.getByLeader(ArrayCache.getUUID(text)) == null || Clan.getByName(text) == null) {
            player.sendMessage(CC.translate("&7That Clan does not exist."));
            return;
        }

        if (Clan.getByLeader(ArrayCache.getUUID(text)) != null) {
            clan = Clan.getByLeader(ArrayCache.getUUID(text));
        } else {
            clan = Clan.getByName(text);
        }

        ClanInvite clanInvite = clan.getInvite(player);

        if (clanInvite == null && clan.getPassword() != null && password.equals("none")) {
            player.sendMessage(CC.RED + "You need the password or an invitation to join this clan.");
            player.sendMessage(CC.GRAY + "To join with a password, use " + CC.RED + "/clan join " + clan.getName() + " <password>");
            return;
        }

        if (!password.equals("none") && !clan.getPassword().equalsIgnoreCase(password)) {
            player.sendMessage(CC.RED + "The password you have entered is incorrect.");
            return;
        }

        clan.join(player, clanInvite);
    }


    @Command(name = "invite", usage = "<target>", desc = "Invite a player to your Clan")
    public void invite(@Sender Player player, Player target) {
        Profile profile = Profile.getByPlayer(player);
        Profile targetProfile = Profile.getByPlayer(target);
        Clan clan = profile.getClan();

        if (clan == null || !clan.getLeader().getUuid().equals(player.getUniqueId())) {
            player.sendMessage(CC.translate("&7You are not the leader of a clan!"));
            return;
        }

        if (target == player) {
            player.sendMessage(CC.RED + "Yoy may not invite yourself to a clan!");
            return;
        }

        if (targetProfile.hasClan()) {
            player.sendMessage(CC.translate("&7That player already has a clan!"));
            return;
        }

        if (clan.getInvite(target) != null) {
            player.sendMessage(target.getDisplayName() + CC.RED + " has already been invited to the clan within the last 60 seconds.");
            return;
        }

        clan.invite(target);
        clan.broadcast(CC.translate("&8[&c&lClan&8] &c" + target.getName() + " &7has been invited to the clan!"));
    }


    @Command(name = "leave", aliases = "resign", desc = "Leave your current clan")
    public void leave(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);

        if (profile.getClan() == null) {
            player.sendMessage(CC.RED + "You are not in a clan!");
            return;
        }

        Clan clan = profile.getClan();
        if (clan.getLeader().getUuid().equals(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are the clan leader. You must disband the clan or promote someone else.");
            return;
        }

        clan.leave(player);
    }

    @Command(name = "disband", desc = "Disband your Clan")
    public void disband(@Sender Player player) {
       Profile profile = Profile.getByPlayer(player);
       ClanProfile clanProfile = profile.getClanProfile();
       Clan clan = profile.getClan();

       if (profile.getClan() == null) {
           player.sendMessage(CC.translate("&7You are not in a Clan!"));
           return;
       }

       if (profile.hasClan() && !clan.getLeader().equals(clanProfile)) {
           player.sendMessage(CC.translate("&7You are not the leader of your clan!"));
           return;
       }

       player.sendMessage(CC.RED + CC.BOLD + "You have successfully disbanded your clan!");

       Clan.getClans().remove(clan);
       Clan.getCollection().deleteOne(Filters.eq("_id", clan.getUuid()));

       clan.getAllMembers().forEach(member -> {
           Profile playerProfile = Profile.getByUuid(member.getUuid());

           playerProfile.setClan(null);
           playerProfile.setClanProfile(null);

           if (playerProfile.getPlayer().isOnline()) {
               if (playerProfile.isInLobby()) {
                   playerProfile.refreshHotbar();
               }
               playerProfile.getPlayer().sendMessage(CC.translate("&cYour clan was disbanded!"));
           }
       });

       Clan.getClans().forEach(Clan::save);
    }



}
