package xyz.refinedev.practice.cmds;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.clan.ClanRoleType;
import xyz.refinedev.practice.clan.meta.ClanInvite;
import xyz.refinedev.practice.clan.meta.ClanProfile;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.OptArg;
import xyz.refinedev.practice.util.command.annotation.Sender;
import xyz.refinedev.practice.util.command.annotation.Text;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.StringUtils;

import java.util.UUID;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/25/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class ClanCommands {

    private final Array plugin;

    @Command(name = "", desc = "View Clan Commands")
    public void clan(@Sender CommandSender player) {
        Locale.CLAN_HELP.toList().forEach(player::sendMessage);
    }

    @Command(name = "create", aliases = "form", desc = "Create a Clan using a name", usage = "<name>")
    public void create(@Sender Player player, String name) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());

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

        if (profile.hasClan()) {
            Clan clan = this.plugin.getClanManager().getByUUID(profile.getClan());
            player.sendMessage(Locale.CLAN_ALREADY_PARTOF.toString().replace("<clan_name>", clan.getName()));
            return;
        }

        Clan existing = this.plugin.getClanManager().getByName(name);
        if (existing != null) {
            player.sendMessage(Locale.CLAN_NAME_ALREADYEXIST.toString().replace("<name>", name));
            return;
        }

        Clan clan = new Clan(name, player.getUniqueId(), UUID.randomUUID());
        ClanProfile clanProfile = new ClanProfile(player.getUniqueId(), clan, ClanRoleType.LEADER);

        this.plugin.getClanManager().getClans().put(clan.getUniqueId(), clan);
        this.plugin.getClanManager().getProfileMap().put(player.getUniqueId(), clanProfile);

        player.sendMessage(Locale.CLAN_CREATED.toString().replace("<name>", name));
    }


    @Command(name = "accept", aliases = "join", usage = "<clan/leader> [password]", desc = "Join a Clan using its leader or name")
    public void accept(@Sender Player player, String text, @OptArg() String password) {
        Profile profile = this.plugin.getProfileManager().getProfile(player);
        UUID leader = PlayerUtil.getUUIDByName(text);

        if (profile.hasClan()) {
            Clan clan = this.plugin.getClanManager().getByUUID(profile.getClan());
            player.sendMessage(Locale.CLAN_ALREADY_PARTOF.toString().replace("<clan_name>", clan.getName()));
            return;
        }

        if (leader == null && this.plugin.getClanManager().getByName(text) == null) {
            player.sendMessage(Locale.CLAN_DOESNT_EXIST.toString());
            return;
        }

        if (leader != null && this.plugin.getClanManager().getByLeader(leader) == null) {
            player.sendMessage(Locale.CLAN_DOESNT_EXIST.toString());
            return;
        }

        Clan clan;
        if (leader != null && this.plugin.getClanManager().getByLeader(leader) != null) {
            clan = this.plugin.getClanManager().getByLeader(leader);
        } else {
            clan = this.plugin.getClanManager().getByName(text);
        }

        ClanInvite clanInvite = this.plugin.getClanManager().getInvite(clan, player);
        if (clanInvite == null && clan.getPassword() != null && password == null) {
            player.sendMessage(Locale.CLAN_PASSWORD_REQURED.toString().replace("<clan_name>", clan.getName()));
            return;
        }

        if (password != null && !clan.getPassword().equalsIgnoreCase(password)) {
            player.sendMessage(Locale.CLAN_INCORRECT_PASS.toString());
            return;
        }

        this.plugin.getClanManager().join(clan, player, clanInvite);
    }

    @Command(name = "chat", desc = "Toggle clan chat mode for your profile")
    public void clanCaht(@Sender Player player) {
        Profile profile = this.plugin.getProfileManager().getProfile(player);
        if (profile.getClan() == null) {
            player.sendMessage(CC.translate(Locale.CLAN_DONOTHAVE.toString()));
            return;
        }
        if (profile.getSettings().isPartyChat()) {
            player.sendMessage(CC.translate("&8[&c&lParty&8] &7Your party chat is currently enabled, please disable it first."));
            return;
        }
        profile.getSettings().setClanChat(!profile.getSettings().isClanChat());
        player.sendMessage(CC.translate((profile.getSettings().isClanChat() ? Locale.SETTINGS_ENABLED.toString().replace("<settings_name>", "Clan Chat") : Locale.SETTINGS_DISABLED.toString().replace("<settings_name>", "Clan Chat"))));
    }

    @Command(name = "invite", usage = "<target>", desc = "Invite a player to your Clan")
    public void invite(@Sender Player player, Player target) {
        Profile profile = this.plugin.getProfileManager().getProfile(player);
        Profile targetProfile = this.plugin.getProfileManager().getProfile(target);
        UUID clanId = profile.getClan();

        if (clanId == null) {
            player.sendMessage(Locale.CLAN_DOESNOTHAVE.toString());
            return;
        }

        Clan clan = this.plugin.getClanManager().getByUUID(clanId);
        if (!clan.isLeader(player.getUniqueId()) || !clan.isCaptain(player.getUniqueId())) {
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

        if (this.plugin.getClanManager().getInvite(clan, target) != null) {
            player.sendMessage(Locale.CLAN_ALREADYINVITED.toString());
            return;
        }

        if (clan.getBannedPlayers().contains(target.getUniqueId())) {
            player.sendMessage(Locale.CLAN_IS_BANNED.toString());
            return;
        }

        this.plugin.getClanManager().invite(clan, target);
        clan.broadcast(Locale.CLAN_INVITED_BROADCAST.toString().replace("<invited>", this.plugin.getCoreHandler().getFullName(target)));
    }


    @Command(name = "leave", aliases = "resign", desc = "Leave your current clan")
    public void leave(@Sender Player player) {
        Profile profile = this.plugin.getProfileManager().getProfile(player);

        if (!profile.hasClan()) {
            player.sendMessage(Locale.CLAN_DONOTHAVE.toString());
            return;
        }

        Clan clan = this.plugin.getClanManager().getByUUID(profile.getClan());
        if (clan.getLeader().getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(Locale.CLAN_LEADER_LEAVE.toString());
            return;
        }

        this.plugin.getClanManager().leave(clan, player);
    }

    @Command(name = "disband", desc = "Disband your Clan")
    public void disband(@Sender Player player) {
       Profile profile = this.plugin.getProfileManager().getProfile(player);
       Clan clan = this.plugin.getClanManager().getByUUID(profile.getClan());
       ClanProfile clanProfile = this.plugin.getClanManager().getProfileByUUID(player.getUniqueId());

       if (clan == null) {
           player.sendMessage(Locale.CLAN_DONOTHAVE.toString());
           return;
       }

       if (profile.hasClan() && !clan.getLeader().getUniqueId().equals(player.getUniqueId())) {
           player.sendMessage(Locale.CLAN_NOT_LEADER.toString());
           return;
       }

       if (this.plugin.getClanManager().isInFight(clan)) {
           player.sendMessage(Locale.CLAN_IN_FIGHT.toString());
           return;
       }

       this.plugin.getClanManager().delete(clan);
    }

    @Command(name = "information", aliases = "info", desc = "View information about your Clan")
    public void information(@Sender Player player) {
        Profile profile = this.plugin.getProfileManager().getProfile(player);
        if (!profile.hasClan()) {
            player.sendMessage(Locale.CLAN_DONOTHAVE.toString());
            return;
        }

        Clan clan = this.plugin.getClanManager().getByUUID(profile.getClan());
        this.plugin.getClanManager().information(clan, player);
    }

    @Command(name = "kick", desc = "Kick a player from your clan")
    public void kick(@Sender Player player, String target) {
        Profile profile = this.plugin.getProfileManager().getProfile(player);
        Clan clan = this.plugin.getClanManager().getByUUID(profile.getClan());

        UUID uuid = PlayerUtil.getUUIDByName(target);
        Profile targetProfile = this.plugin.getProfileManager().getProfile(uuid);

        if (clan == null) {
            player.sendMessage(Locale.CLAN_DONOTHAVE.toString());
            return;
        }

        if (!clan.isLeader(player.getUniqueId()) && !clan.isCaptain(player.getUniqueId())) {
            player.sendMessage(Locale.CLAN_NO_PERM.toString());
            return;
        }

        if (!targetProfile.hasClan() || (targetProfile.hasClan() && !targetProfile.getClan().equals(clan.getUniqueId()))) {
            player.sendMessage(Locale.CLAN_NOT_PARTOF.toString());
            return;
        }

        this.plugin.getClanManager().kick(clan, uuid);
    }

    @Command(name = "kick", desc = "Kick a player from your clan")
    public void ban(@Sender Player player, String target) {
        Profile profile = plugin.getProfileManager().getProfile(player);
        Clan clan = this.plugin.getClanManager().getByUUID(profile.getClan());

        UUID uuid = PlayerUtil.getUUIDByName(target);
        Profile targetProfile = plugin.getProfileManager().getProfile(uuid);

        if (clan == null) {
            player.sendMessage(Locale.CLAN_DONOTHAVE.toString());
            return;
        }

        if (!clan.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.CLAN_NO_PERM.toString());
            return;
        }

        if (!targetProfile.hasClan() || (targetProfile.hasClan() && !targetProfile.getClan().equals(clan.getUniqueId()))) {
            player.sendMessage(Locale.CLAN_NOT_PARTOF.toString());
            return;
        }

        this.plugin.getClanManager().ban(clan, uuid);
    }

    @Command(name = "password", aliases = {"setpassword", "setpass", "pass"}, desc = "Set your clan's password")
    public void password(@Sender Player player, String password) {
        Profile profile = plugin.getProfileManager().getProfile(player);
        Clan clan = this.plugin.getClanManager().getByUUID(profile.getClan());

        if (clan == null) {
            player.sendMessage(Locale.CLAN_DONOTHAVE.toString());
            return;
        }

        if (!clan.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.CLAN_NO_PERM.toString());
            return;
        }

        if (password.length() > 16) {
            player.sendMessage(CC.translate("&7Password can not be longer than 16 characters."));
            return;
        }

        if (password.length() < 3) {
            player.sendMessage(CC.translate("&7Password can not be less than 3 characters."));
            return;
        }

        clan.setPassword(password);
        player.sendMessage(Locale.CLAN_SETPASS.toString());
    }

    @Command(name = "description", aliases = {"desc", "setdesc", "setdescription"}, desc = "Set your clan's description")
    public void description(@Sender Player player, @Text String description) {
        Profile profile = plugin.getProfileManager().getProfile(player);
        Clan clan = this.plugin.getClanManager().getByUUID(profile.getClan());

        if (clan == null) {
            player.sendMessage(Locale.CLAN_DONOTHAVE.toString());
            return;
        }

        if (!clan.isLeader(player.getUniqueId()) && !clan.isCaptain(player.getUniqueId())) {
            player.sendMessage(Locale.CLAN_NO_PERM.toString());
            return;
        }


        clan.setDescription(CC.translate(description));
        player.sendMessage(Locale.CLAN_SETDESC.toString());
    }

    @Command(name = "promote", desc = "Promote a member of your clan to Captain")
    public void promote(@Sender Player player,  String target) {
        Profile profile = this.plugin.getProfileManager().getProfile(player);
        Clan clan = this.plugin.getClanManager().getByUUID(profile.getClan());

        UUID uuid = PlayerUtil.getUUIDByName(target);
        Profile targetProfile = this.plugin.getProfileManager().getProfile(uuid);
        ClanProfile clanProfile = this.plugin.getClanManager().getProfileByUUID(uuid);

        if (clan == null) {
            player.sendMessage(Locale.CLAN_DONOTHAVE.toString());
            return;
        }

        if (!clan.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.CLAN_NO_PERM.toString());
            return;
        }

        if (clan.isLeader(uuid)) {
            player.sendMessage(Locale.CLAN_SELF_PROMOTE.toString());
            return;
        }

        if (!targetProfile.hasClan() || (targetProfile.hasClan() && !targetProfile.getClan().equals(clan.getUniqueId()))) {
            player.sendMessage(Locale.CLAN_NOT_PARTOF.toString());
            return;
        }

        this.plugin.getClanManager().promote(clan, clanProfile);
    }

    @Command(name = "demote", desc = "Demote a Captain to Member Role from your Clan")
    public void demote(@Sender Player player,  String target) {
        Profile profile = this.plugin.getProfileManager().getProfile(player);
        Clan clan = this.plugin.getClanManager().getByUUID(profile.getClan());

        UUID uuid = PlayerUtil.getUUIDByName(target);
        Profile targetProfile = this.plugin.getProfileManager().getProfile(uuid);
        ClanProfile clanProfile = this.plugin.getClanManager().getProfileByUUID(uuid);

        if (clan == null) {
            player.sendMessage(Locale.CLAN_DONOTHAVE.toString());
            return;
        }

        if (!clan.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.CLAN_NO_PERM.toString());
            return;
        }

        if (clan.isLeader(uuid)) {
            player.sendMessage(Locale.CLAN_SELF_PROMOTE.toString());
            return;
        }

        if (!targetProfile.hasClan() || (targetProfile.hasClan() && !targetProfile.getClan().equals(clan.getUniqueId()))) {
            player.sendMessage(Locale.CLAN_NOT_PARTOF.toString());
            return;
        }

        if (clan.isCaptain(uuid)) {
            player.sendMessage(CC.translate("&7That player is already a captain in your clan!"));
            return;
        }

        this.plugin.getClanManager().demote(clan, clanProfile);
    }

    @Command(name = "leader", desc = "Promote someone to Leader and demote yourself in your Clan")
    public void leader(@Sender Player player, String target) {
        Profile profile = this.plugin.getProfileManager().getProfile(player);
        Clan clan = this.plugin.getClanManager().getByUUID(profile.getClan());

        UUID uuid = PlayerUtil.getUUIDByName(target);
        Profile targetProfile = this.plugin.getProfileManager().getProfile(uuid);
        ClanProfile clanProfile = this.plugin.getClanManager().getProfileByUUID(uuid);

        if (clan == null) {
            player.sendMessage(Locale.CLAN_DONOTHAVE.toString());
            return;
        }

        if (!clan.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.CLAN_NO_PERM.toString());
            return;
        }

        if (!targetProfile.hasClan() || (targetProfile.hasClan() && !targetProfile.getClan().equals(clan.getUniqueId()))) {
            player.sendMessage(Locale.CLAN_NOT_PARTOF.toString());
            return;
        }

        if (clan.isLeader(uuid)) {
            player.sendMessage(Locale.CLAN_SELF_PROMOTE.toString());
            return;
        }

        this.plugin.getClanManager().leader(clan, clanProfile);
    }

}
