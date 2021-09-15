package xyz.refinedev.practice.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.menu.PartyClassSelectMenu;
import xyz.refinedev.practice.party.menu.PartyDuelMenu;
import xyz.refinedev.practice.party.menu.PartyEventMenu;
import xyz.refinedev.practice.party.menu.PartySettingsMenu;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/25/2021
 * Project: Array
 */

public class PartyCommands {
    
    @Command(name = "", desc = "View Party Commands")
    public void party(@Sender CommandSender player) {
        Locale.PARTY_HELP.toList().forEach(player::sendMessage);
    }

    @Command(name = "create", desc = "Create a Party")
    public void partyCreate(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getParty() != null) {
            player.sendMessage(Locale.PARTY_ALREADYHAVE.toString());
            return;
        }
        if (!profile.isInLobby() || profile.isBusy()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }
        profile.setParty(new Party(player));
        profile.refreshHotbar();
        player.sendMessage(Locale.PARTY_CREATED.toString());
    }

    @Command(name = "disband", desc = "Disband your Party")
    public void partyDisband(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getParty() == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (!profile.getParty().isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (profile.getParty().isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        if (profile.getMatch() != null) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }
        profile.getParty().disband();
    }

    @Command(name = "invite", desc = "Invite a Profile to your Party", usage = "<profile>")
    public void partyInvite(@Sender Player player, Player target) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Profile targetData = Profile.getByUuid(target.getUniqueId());

        if (profile.getParty() == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (profile.getParty().getInvite(target.getUniqueId()) != null) {
            player.sendMessage(Locale.PARTY_ALREADYINVITED.toString());
            return;
        }
        if (profile.getParty().containsPlayer(target)) {
            player.sendMessage(Locale.PARTY_ALREADYINPARTY.toString());
            return;
        }
        if (profile.getParty().isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        if (targetData.isBusy()) {
            player.sendMessage(Locale.ERROR_BUSY.toString());
            return;
        }
        profile.getParty().invite(target);
    }

    @Command(name = "settings", desc = "Open Party Settings Menu")
    public void partySettings(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getParty() == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        } else if (!profile.getParty().isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (profile.getParty().isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        new PartySettingsMenu().openMenu(player);
    }

    @Command(name = "event", aliases = "event", desc = "Open Party Events Menu")
    public void partyEvents(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getParty() == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        } else if (!profile.getParty().isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (profile.getParty().isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        new PartyEventMenu().openMenu(player);
    }

    @Command(name = "classes", aliases = {"hcfkits", "hcfkit", "class"}, desc = "View HCF Class Menu")
    public void partyClasses(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getParty() == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        } else if (!profile.getParty().isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (profile.getParty().isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        new PartyClassSelectMenu().openMenu(player);
    }

    @Command(name = "info", aliases = "information", desc = "View Information about your Party")
    public void partyInfo(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getParty() == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        profile.getParty().sendInformation(player);
    }

    @Command(name = "duel", aliases = "otherparties", desc = "Duel other parties")
    public void partyDuel(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getParty() == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        } else if (!profile.getParty().isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (profile.getParty().isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        new PartyDuelMenu().openMenu(player);
    }

    @Command(name = "join", aliases = "accept" ,desc = "Join a party using its invitation", usage = "<leader>")
    public void partyJoin(@Sender Player player, Player target) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Profile targetProfile = Profile.getByUuid(target.getUniqueId());
        Party party = targetProfile.getParty();

        if (profile.isBusy()) {
            player.sendMessage(Locale.ERROR_BUSY.toString());
            return;
        }
        if (profile.getParty() != null) {
            player.sendMessage(Locale.PARTY_ALREADYHAVE.toString());
            return;
        }
        if (party == null) {
            player.sendMessage(Locale.PARTY_WRONG_LEADER.toString());
            return;
        }
        if (!party.isPublic() && party.getInvite(player.getUniqueId()) == null) {
            player.sendMessage(Locale.PARTY_NOT_INVITED.toString());
            return;
        }
        if (party.isInTournament()) {
            player.sendMessage(Locale.PARTY_TOURNAMENT.toString());
            return;
        }

        if (party.getPlayers().size() >= party.getLimit()) {
            player.sendMessage(Locale.PARTY_FULL.toString());
            return;
        }
        if (party.getBanned().contains(player)) {
            player.sendMessage(Locale.PARTY_BANNED.toString());
            return;
        }
        party.join(player);
    }

    @Command(name = "kick", aliases = "remove", desc = "Kick a player from the party", usage = "<target>")
    public void partyKick(@Sender Player player, Player target) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Party party = profile.getParty();

        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (profile.getParty().isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        if (!party.containsPlayer(target)) {
            player.sendMessage(Locale.PARTY_NOT_MEMBER.toString());
            return;
        }
        if (player.equals(target)) {
            player.sendMessage(Locale.PARTY_KICK_SELF.toString());
            return;
        }
        target.sendMessage(Locale.PARTY_KICKED.toString());
        party.leave(target, true);
    }
    
    @Command(name = "open", aliases = "public", desc = "Publicize your party")
    public void partyOpen(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Party party = profile.getParty();

        if (!player.hasPermission("array.party.privacy") && !player.isOp() && !player.hasPermission("*")) {
            Locale.PARTY_DONATOR.toList().stream().map(line -> line.replace("<store>", Array.getInstance().getConfigHandler().getSTORE())).forEach(player::sendMessage);
            return;
        }
        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (profile.getParty().isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        party.setPublic(!party.isPublic());
    }

    @Command(name = "promote", aliases = "leader", desc = "Promote a player to leader in your party", usage = "<target>")
    public void partyPromote(@Sender Player player, Player target) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getParty() == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (!profile.getParty().isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (!profile.getParty().containsPlayer(target)) {
            player.sendMessage(Locale.PARTY_NOT_MEMBER.toString());
            return;
        }
        if (player.equals(target)) {
            player.sendMessage(Locale.PARTY_ALREADYLEADER.toString());
            return;
        }
        profile.getParty().leader(player, target);
    }

    @Command(name = "ban", desc = "Ban a player from the party", usage = "<target>")
    public void partyBan(@Sender Player player, Player target) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (!player.hasPermission("array.party.ban")) {
            Locale.PARTY_DONATOR.toList().stream().map(line -> line.replace("<store>", Array.getInstance().getConfigHandler().getSTORE())).forEach(player::sendMessage);
            return;
        }
        if (profile.getParty() == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (target == null) {
            player.sendMessage(Locale.ERROR_PLAYERNOTFOUND.toString());
            return;
        }
        if (!profile.getParty().isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (profile.getParty().isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        if (player.equals(target)) {
            player.sendMessage(Locale.PARTY_BAN_SELF.toString());
            return;
        }
        if (profile.getParty().getBanned().contains(target)) {
            player.sendMessage(Locale.PARTY_ALREADYBANNED.toString());
            return;
        }
        if (profile.getParty().containsPlayer(target)) {
            profile.getParty().leave(target, true);
        }
        player.sendMessage(Locale.PARTY_BAN.toString().replace("<target>", target.getName()));
        profile.getParty().ban(target);
    }

    @Command(name = "unban", desc = "Unban a player from your party", usage = "<target>")
    public void partyUnban(@Sender Player player, Player target) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (!player.hasPermission("array.party.ban")) {
            Locale.PARTY_DONATOR.toList().forEach(player::sendMessage);
            return;
        }
        if (profile.getParty() == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (target == null) {
            player.sendMessage(Locale.ERROR_PLAYERNOTFOUND.toString());
            return;
        }
        if (!profile.getParty().isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (profile.getParty().isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        if (player.equals(target)) {
            player.sendMessage(Locale.PARTY_UNBAN_SELF.toString());
            return;
        }
        if (!profile.getParty().getBanned().contains(target)) {
            player.sendMessage(Locale.PARTY_NOT_BANNED.toString());
            return;
        }
        profile.getParty().unban(target);
    }

    @Command(name = "chat", desc = "Toggle party chat mode for your profile")
    public void partyChat(@Sender Player player) {
        Profile profile = Profile.getByPlayer(player);
        if (profile.getParty() == null) {
            player.sendMessage(CC.translate(Locale.PARTY_DONOTHAVE.toString()));
            return;
        }
        if (profile.getSettings().isClanChat()) {
            player.sendMessage(CC.translate("&8[&c&lClan&8] &7Your clan chat is currently enabled, please disable it first."));
            return;
        }
        profile.getSettings().setPartyChat(!profile.getSettings().isPartyChat());
        player.sendMessage(CC.translate((profile.getSettings().isPartyChat() ? Locale.PARTY_CHAT.toString() : Locale.PARTY_GLOBAL.toString())));
    }

    @Command(name = "close", desc = "Privatise your party to the public")
    public void partyClose(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Party party = profile.getParty();

        if (!player.hasPermission("array.party.privacy") && !player.isOp() && !player.hasPermission("*")) {
            Locale.PARTY_DONATOR.toList().stream().map(line -> line.replace("<store>", Array.getInstance().getConfigHandler().getSTORE())).forEach(player::sendMessage);
            return;
        }
        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        party.setPublic(!party.isPublic());
    }


    @Command(name = "leave", aliases = "resign", desc = "Leave your party")
    public void partyLeave(@Sender Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        Party party = profile.getParty();
        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (party.getLeader().getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_LEAVE_SELF.toString());
            return;
        }
        if (party.isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        profile.getParty().leave(player, false);
    }

}
