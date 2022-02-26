package xyz.refinedev.practice.cmds;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.menu.PartyDuelMenu;
import xyz.refinedev.practice.party.menu.PartyEventMenu;
import xyz.refinedev.practice.party.menu.PartyPvPClassMenu;
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

@RequiredArgsConstructor
public class PartyCommands {

    private final Array plugin;
    
    @Command(name = "", desc = "View Party Commands")
    public void party(@Sender CommandSender player) {
        Locale.PARTY_HELP.toList().forEach(player::sendMessage);
    }

    @Command(name = "create", desc = "Create a Party")
    public void partyCreate(@Sender Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());

        if (profile.hasParty()) {
            player.sendMessage(Locale.PARTY_ALREADYHAVE.toString());
            return;
        }

        if (!profile.isInLobby() || profile.isBusy()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }

        Party party = new Party(player);
        profile.setParty(party.getUniqueId());

        this.plugin.getPartyManager().getParties().put(party.getUniqueId(), party);
        this.plugin.getProfileManager().refreshHotbar(profile);

        player.sendMessage(Locale.PARTY_CREATED.toString());
    }

    @Command(name = "disband", desc = "Disband your Party")
    public void partyDisband(@Sender Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());
        
        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (party.isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        if (profile.getMatch() != null) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }
        this.plugin.getPartyManager().disband(party);
    }

    @Command(name = "invite", desc = "Invite a Profile to your Party", usage = "<profile>")
    public void partyInvite(@Sender Player player, Player target) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Profile targetData = plugin.getProfileManager().getProfile(target.getUniqueId());
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());

        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (party.getInvites().containsKey(target.getUniqueId())) {
            player.sendMessage(Locale.PARTY_ALREADYINVITED.toString());
            return;
        }
        if (party.containsPlayer(target)) {
            player.sendMessage(Locale.PARTY_ALREADYINPARTY.toString());
            return;
        }
        if (party.isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        if (targetData.isBusy()) {
            player.sendMessage(Locale.ERROR_BUSY.toString());
            return;
        }
        this.plugin.getPartyManager().invite(target, party);
    }

    @Command(name = "settings", desc = "Open Party Settings Menu")
    public void partySettings(@Sender Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());
        
        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        } else if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (party.isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        new PartySettingsMenu(plugin).openMenu(player);
    }

    @Command(name = "event", aliases = "event", desc = "Open Party Events Menu")
    public void partyEvents(@Sender Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());
        
        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        } else if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (party.isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        new PartyEventMenu(plugin).openMenu(player);
    }

    @Command(name = "classes", aliases = {"hcfkits", "hcfkit", "class"}, desc = "View HCF Class Menu")
    public void partyClasses(@Sender Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());
        
        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        } else if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (party.isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        new PartyPvPClassMenu(plugin).openMenu(player);
    }

    @Command(name = "info", aliases = "information", desc = "View Information about your Party")
    public void partyInfo(@Sender Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());
        
        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        party.sendInformation(player);
    }

    @Command(name = "duel", aliases = "otherparties", desc = "Duel other parties")
    public void partyDuel(@Sender Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());
        
        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        } else if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (party.isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        PartyDuelMenu menu = new PartyDuelMenu(plugin);
        menu.openMenu(player);
    }

    @Command(name = "join", aliases = "accept" ,desc = "Join a party using its invitation", usage = "<leader>")
    public void partyJoin(@Sender Player player, Player target) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Profile targetprofile = this.plugin.getProfileManager().getProfile(target.getUniqueId());
        
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());
        Party targetParty = this.plugin.getPartyManager().getPartyByUUID(targetprofile.getParty());

        if (profile.isBusy()) {
            player.sendMessage(Locale.ERROR_BUSY.toString());
            return;
        }
        if (party != null) {
            player.sendMessage(Locale.PARTY_ALREADYHAVE.toString());
            return;
        }
        if (targetParty == null) {
            player.sendMessage(Locale.PARTY_WRONG_LEADER.toString());
            return;
        }
        if (!targetParty.isPublic() && targetParty.getInvite(player.getUniqueId()) == null) {
            player.sendMessage(Locale.PARTY_NOT_INVITED.toString());
            return;
        }
        if (targetParty.isInTournament()) {
            player.sendMessage(Locale.PARTY_TOURNAMENT.toString());
            return;
        }

        if (targetParty.getPlayers().size() >= targetParty.getLimit()) {
            player.sendMessage(Locale.PARTY_FULL.toString());
            return;
        }
        if (targetParty.getBanned().contains(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_BANNED.toString());
            return;
        }
        this.plugin.getPartyManager().join(player, targetParty);
    }

    @Command(name = "kick", aliases = "remove", desc = "Kick a player from the party", usage = "<target>")
    public void partyKick(@Sender Player player, Player target) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());

        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (party.isInTournament()) {
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
        this.plugin.getPartyManager().leave(target, party);
    }
    
    @Command(name = "open", aliases = "public", desc = "Publicize your party")
    public void partyOpen(@Sender Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());

        if (!player.hasPermission("array.party.privacy") && !player.isOp() && !player.hasPermission("*")) {
            Locale.PARTY_DONATOR.toList().stream().map(line -> line.replace("<store>", this.plugin.getConfigHandler().getSTORE())).forEach(player::sendMessage);
            return;
        }
        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (party.isInTournament()) {
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
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());

        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (!party.containsPlayer(target)) {
            player.sendMessage(Locale.PARTY_NOT_MEMBER.toString());
            return;
        }
        if (player.equals(target)) {
            player.sendMessage(Locale.PARTY_ALREADYLEADER.toString());
            return;
        }
        this.plugin.getPartyManager().leader(target, party);
    }

    @Command(name = "ban", desc = "Ban a player from the party", usage = "<target>")
    public void partyBan(@Sender Player player, Player target) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());

        if (!player.hasPermission("array.party.ban") && !player.isOp() && !player.hasPermission("*")) {
            Locale.PARTY_DONATOR.toList().stream().map(line -> line.replace("<store>", this.plugin.getConfigHandler().getSTORE())).forEach(player::sendMessage);
            return;
        }
        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (target == null) {
            player.sendMessage(Locale.ERROR_PLAYERNOTFOUND.toString());
            return;
        }
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (party.isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        if (player.equals(target)) {
            player.sendMessage(Locale.PARTY_BAN_SELF.toString());
            return;
        }
        if (party.getBanned().contains(target.getUniqueId())) {
            player.sendMessage(Locale.PARTY_ALREADYBANNED.toString());
            return;
        }
        if (party.containsPlayer(target)) {
            plugin.getPartyManager().leave(target, party);
        }
        player.sendMessage(Locale.PARTY_BAN.toString().replace("<target>", target.getName()));
        this.plugin.getPartyManager().ban(target, party);
    }

    @Command(name = "unban", desc = "Unban a player from your party", usage = "<target>")
    public void partyUnban(@Sender Player player, Player target) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());

        if (!player.hasPermission("array.party.ban")) {
            Locale.PARTY_DONATOR.toList().forEach(player::sendMessage);
            return;
        }
        if (party == null) {
            player.sendMessage(CC.translate(Locale.PARTY_DONOTHAVE.toString()));
            return;
        }
        if (target == null) {
            player.sendMessage(Locale.ERROR_PLAYERNOTFOUND.toString());
            return;
        }
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOTLEADER.toString());
            return;
        }
        if (party.isInTournament()) {
            player.sendMessage(Locale.PARTY_IN_TOURNAMENT.toString());
            return;
        }
        if (player.equals(target)) {
            player.sendMessage(Locale.PARTY_UNBAN_SELF.toString());
            return;
        }
        if (!party.getBanned().contains(target.getUniqueId())) {
            player.sendMessage(Locale.PARTY_NOT_BANNED.toString());
            return;
        }
        this.plugin.getPartyManager().unban(target, party);
    }

    @Command(name = "chat", desc = "Toggle party chat mode for your profile")
    public void partyChat(@Sender Player player) {
        Profile profile = this.plugin.getProfileManager().getProfile(player);
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());

        if (party == null) {
            player.sendMessage(CC.translate(Locale.PARTY_DONOTHAVE.toString()));
            return;
        }
        if (profile.getSettings().isClanChat()) {
            player.sendMessage(Locale.CLAN_CHAT_ERROR.toString());
            return;
        }
        profile.getSettings().setPartyChat(!profile.getSettings().isPartyChat());
        player.sendMessage(CC.translate((profile.getSettings().isPartyChat() ? Locale.PARTY_CHAT.toString() : Locale.PARTY_GLOBAL.toString())));
    }

    @Command(name = "close", desc = "Privatise your party to the public")
    public void partyClose(@Sender Player player) {
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());

        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }
        if (!player.hasPermission("array.party.privacy") && !player.isOp() && !player.hasPermission("*")) {
            Locale.PARTY_DONATOR.toList().stream().map(line -> line.replace("<store>", this.plugin.getConfigHandler().getSTORE())).forEach(player::sendMessage);
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
        ProfileManager profileManager = this.plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getPartyByUUID(profile.getParty());

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
        this.plugin.getPartyManager().leave(player, party);
    }

}
