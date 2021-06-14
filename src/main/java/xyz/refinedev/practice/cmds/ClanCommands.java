package xyz.refinedev.practice.cmds;

import com.mongodb.client.model.Filters;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.api.ArrayCache;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.clan.meta.ClanInvite;
import xyz.refinedev.practice.clan.meta.ClanProfile;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.rank.Rank;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.OptArg;
import xyz.refinedev.practice.util.command.annotation.Sender;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * This Project is the property of Refine Development © 2021
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
        ClanProfile clanProfile = targetProfile.getClanProfile();
        Clan clan = profile.getClan();

        if (clan == null || clanProfile == null || (!clan.getLeader().getUuid().equals(player.getUniqueId()) && !clan.getCaptains().contains(clanProfile))) {
            player.sendMessage(Locale.CLAN_NOT_CAPTAIN.toString());
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

        if (clan.getBannedPlayers().contains(target.getUniqueId())) {
            player.sendMessage(Locale.CLAN_IS_BANNED.toString());
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
            player.sendMessage(Locale.CLAN_LEADER_LEAVE.toString());
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

    public void kick() {

    }

    public void ban() {

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
