package me.drizzy.practice.cmds;

import com.mongodb.client.model.Filters;
import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.api.ArrayCache;
import me.drizzy.practice.clan.Clan;
import me.drizzy.practice.clan.ClanProfileType;
import me.drizzy.practice.clan.meta.ClanInvite;
import me.drizzy.practice.clan.meta.ClanProfile;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.rank.Rank;
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
        Locale.CLAN_HELP.toList().forEach(player::sendMessage);
    }

    @Command(name = "create", aliases = "form", desc = "Create a Clan using a name", usage = "<name>")
    public void create(@Sender Player player, String name) {
        if (name.length() < 2) {
            player.sendMessage(Locale.CLAN_NAME_LENGTH.toString());
            return;
        }

        if (!StringUtils.isAlpha(name)) {
            player.sendMessage(Locale.CLAN_NAME_LETTER.toString());
            return;
        }

        if (name.length() > 8) {
            player.sendMessage(Locale.CLAN_NAME_LENGTH.toString());
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        if (profile.hasClan()) {
            player.sendMessage(Locale.CLAN_ALREADY_PARTOF.toString().replace("<clan_name>", profile.getClan().getName()));
            return;
        }

        Clan clan = Clan.getByName(name);

        if (clan != null) {
            player.sendMessage(Locale.CLAN_NAME_ALREADYEXIST.toString().replace("<name>", name));
            return;
        }

        clan = new Clan(name, player.getUniqueId(), UUID.randomUUID());
        player.sendMessage(Locale.CLAN_CREATED.toString().replace("<name>", name));
    }


    @Command(name = "accept", aliases = "join", usage = "<clan/leader> [password]", desc = "Join a Clan using its leader or name")
    public void accept(@Sender Player player, String text, @OptArg("none") String password) {
        Profile profile = Profile.getByPlayer(player);
        Clan clan;

        if (profile.hasClan()) {
            player.sendMessage(Locale.CLAN_ALREADY_PARTOF.toString().replace("<clan_name>", profile.getClan().getName()));
            return;
        }

        if (ArrayCache.getUUID(text) == null && Clan.getByName(text) == null) {
            player.sendMessage(Locale.CLAN_DOESNT_EXIST.toString());
            return;
        }

        if (Clan.getByLeader(ArrayCache.getUUID(text)) == null || Clan.getByName(text) == null) {
            player.sendMessage(Locale.CLAN_DOESNT_EXIST.toString());
            return;
        }

        if (Clan.getByLeader(ArrayCache.getUUID(text)) != null) {
            clan = Clan.getByLeader(ArrayCache.getUUID(text));
        } else {
            clan = Clan.getByName(text);
        }

        ClanInvite clanInvite = clan.getInvite(player);

        if (clanInvite == null && clan.getPassword() != null && password.equals("none")) {
            player.sendMessage(Locale.CLAN_PASSWORD_REQURED.toString().replace("<clan_name>", clan.getName()));
            return;
        }

        if (!password.equals("none") && !clan.getPassword().equalsIgnoreCase(password)) {
            player.sendMessage(Locale.CLAN_INCORRECT_PASS.toString());
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
            player.sendMessage(Locale.CLAN_NOT_LEADER.toString());
            return;
        }

        if (target == player) {
            player.sendMessage(Locale.CLAN_INVITE_SELF.toString());
            return;
        }

        if (targetProfile.hasClan()) {
            player.sendMessage(Locale.CLAN_ALREADYHAVE.toString());
            return;
        }

        if (clan.getInvite(target) != null) {
            player.sendMessage(Locale.CLAN_ALREADYINVITED.toString());
            return;
        }

        clan.invite(target);
        clan.broadcast(Locale.CLAN_INVITED_BROADCAST.toString().replace("<invited>", Rank.getRankType().getFullName(target)));
    }


    @Command(name = "leave", aliases = "resign", desc = "Leave your current clan")
    public void leave(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);

        if (profile.getClan() == null) {
            player.sendMessage(Locale.CLAN_DONOTHAVE.toString());
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
           player.sendMessage(Locale.CLAN_DONOTHAVE.toString());
           return;
       }

       if (profile.hasClan() && !clan.getLeader().getUuid().equals(player.getUniqueId())) {
           player.sendMessage(Locale.CLAN_NOT_LEADER.toString());
           return;
       }

       clan.getAllMembers().forEach(member -> {
           Profile playerProfile = Profile.getByUuid(member.getUuid());

           playerProfile.setClan(null);
           playerProfile.setClanProfile(null);

           if (playerProfile.getPlayer().isOnline()) {
               if (playerProfile.isInLobby()) {
                   playerProfile.refreshHotbar();
               }
               playerProfile.getPlayer().sendMessage(Locale.CLAN_DISBANDED.toString());
           }
       });

       //Completely wipe the Clan
       Clan.getClans().remove(clan);
       Clan.getCollection().deleteOne(Filters.eq("_id", clan.getUuid()));
       clan.getMembers().clear();
       clan.getCaptains().clear();

       Clan.getClans().forEach(Clan::save);
    }

    @Command(name = "information", aliases = "info", desc = "View information about your Clan")
    public void information(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);
        Clan clan = profile.getClan();

        if (clan == null) {
            player.sendMessage(Locale.CLAN_DONOTHAVE.toString());
            return;
        }

        clan.information(player);
    }

    @Command(name = "kick", desc = "Kick a player out of the clan", usage = "<target>")
    public void kick(@Sender Player player, Player target) {
        if (target == null) {
            player.sendMessage(CC.translate("That player does not exist!"));
            return;
        }

        Profile sender = Profile.getByUuid(player.getUniqueId());
        Profile profile = Profile.getByUuid(target.getUniqueId());

        Clan senderClan = sender.getClan();
        Clan targetClan = profile.getClan();

        if (!targetClan.getName().equals(senderClan.getName())) {
            player.sendMessage(CC.translate("&cThis player is not in your clan!"));
            return;
        }

        targetClan.broadcast("&c" + profile.getName() + " has been kicked from the clan!");
        targetClan.kick(target.getUniqueId());
    }

    @Command(name = "ban", desc = "Bans a player from the clan", usage = "<target>")
    public void ban(@Sender Player player, Player target) {
        if (target == null) {
            player.sendMessage(CC.translate("That player does not exist!"));
            return;
        }

        Profile sender = Profile.getByUuid(player.getUniqueId());
        Profile profile = Profile.getByUuid(target.getUniqueId());

        Clan senderClan = sender.getClan();
        Clan targetClan = profile.getClan();

        if (!targetClan.getName().equals(senderClan.getName())) {
            player.sendMessage(CC.translate("&cThat player is not in your clan!"));
            return;
        }

        targetClan.broadcast("&c" + profile.getName() + " has been banned from the clan!");
        targetClan.ban(target.getUniqueId());
    }

    public void password() {

    }

    public void description() {

    }

    public void promote() {

    }

    public void demote() {

    }

    public void leader() {

    }



}
