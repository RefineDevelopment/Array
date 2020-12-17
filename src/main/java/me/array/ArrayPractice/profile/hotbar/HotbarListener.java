

package me.array.ArrayPractice.profile.hotbar;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.event.menu.ActiveEventSelectEventMenu;
import me.array.ArrayPractice.kit.menu.KitEditorSelectKitMenu;
import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.party.PartyMessage;
import me.array.ArrayPractice.party.menu.ManagePartySettings;
import me.array.ArrayPractice.party.menu.OtherPartiesMenu;
import me.array.ArrayPractice.party.menu.PartyEventSelectEventMenu;
import me.array.ArrayPractice.party.menu.PartyListMenu;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.profile.ProfileState;
import me.array.ArrayPractice.profile.options.OptionsMenu;
import me.array.ArrayPractice.queue.QueueType;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import me.array.ArrayPractice.profile.meta.ProfileRematchData;
import me.array.ArrayPractice.event.impl.spleef.Spleef;
import me.array.ArrayPractice.event.impl.skywars.SkyWars;
import me.array.ArrayPractice.event.impl.wipeout.Wipeout;
import me.array.ArrayPractice.event.impl.parkour.Parkour;
import me.array.ArrayPractice.event.impl.juggernaut.Juggernaut;
import me.array.ArrayPractice.event.impl.ffa.FFA;
import me.array.ArrayPractice.event.impl.brackets.Brackets;
import me.array.ArrayPractice.event.impl.sumo.Sumo;
import org.bukkit.entity.Player;
import me.array.ArrayPractice.profile.stats.menu.RankedLeaderboardsMenu;
import me.array.ArrayPractice.profile.stats.menu.StatsMenu;
import me.array.ArrayPractice.queue.menu.QueueSelectKitMenu;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

public class HotbarListener implements Listener
{
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
            final Player player = event.getPlayer();
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            final HotbarItem hotbarItem = Hotbar.fromItemStack(event.getItem());
            if (hotbarItem == null) {
                return;
            }
            event.setCancelled(true);
            switch (hotbarItem) {
                case QUEUE_JOIN_RANKED: {
                    if (!profile.isBusy(player)) {
                        new QueueSelectKitMenu(QueueType.RANKED).openMenu(event.getPlayer());
                        break;
                    }
                    break;
                }
                case QUEUE_JOIN_UNRANKED: {
                    if (!profile.isBusy(player)) {
                        new QueueSelectKitMenu(QueueType.UNRANKED).openMenu(event.getPlayer());
                        break;
                    }
                    break;
                }
                case QUEUE_JOIN_KITPVP: {
                    if (!profile.isBusy(player)) {
                        player.sendMessage(CC.translate("&7This feature will be available in &b&lSeason 1&7!"));
                    }
                }
                case QUEUE_LEAVE: {
                    if (profile.isInQueue()) {
                        profile.getQueue().removePlayer(profile.getQueueProfile());
                        break;
                    }
                    break;
                }
                case PARTY_EVENTS: {
                    new PartyEventSelectEventMenu().openMenu(player);
                    break;
                }
                case OTHER_PARTIES: {
                    new OtherPartiesMenu().openMenu(event.getPlayer());
                    break;
                }
                case PARTY_MEMBERS: {
                    new PartyListMenu().openMenu(event.getPlayer());
                    break;
                }
                case PARTY_SETTINGS: {
                    new ManagePartySettings().openMenu(event.getPlayer());
                    break;
                }
                case SETTINGS_MENU: {
                    new OptionsMenu().openMenu(event.getPlayer());
                    break;
                }
                case LEADERBOARDS_MENU: {
                    new RankedLeaderboardsMenu().openMenu(event.getPlayer());
                    break;
                }
                case KIT_EDITOR: {
                    if (profile.isInLobby() || profile.isInQueue()) {
                        new KitEditorSelectKitMenu().openMenu(event.getPlayer());
                        break;
                    }
                    break;
                }
                case PARTY_CREATE: {
                    if (profile.getParty() != null) {
                        player.sendMessage(CC.RED + "You already have a party.");
                        return;
                    }
                    if (!profile.isInLobby()) {
                        player.sendMessage(CC.RED + "You must be in the lobby to create a party.");
                        return;
                    }
                    profile.setParty(new Party(player));
                    profile.refreshHotbar();
                    player.sendMessage(PartyMessage.CREATED.format(new Object[0]));
                    break;
                }
                case PARTY_DISBAND: {
                    if (profile.getParty() == null) {
                        player.sendMessage(CC.RED + "You do not have a party.");
                        return;
                    }
                    if (!profile.getParty().isLeader(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not the leader of your party.");
                        return;
                    }
                    profile.getParty().disband();
                    break;
                }
                case PARTY_INFORMATION: {
                    if (profile.getParty() == null) {
                        player.sendMessage(CC.RED + "You do not have a party.");
                        return;
                    }
                    profile.getParty().sendInformation(player);
                    break;
                }
                case PARTY_LEAVE: {
                    if (profile.getParty() == null) {
                        player.sendMessage(CC.RED + "You do not have a party.");
                        return;
                    }
                    if (profile.getParty().getLeader().getUuid().equals(player.getUniqueId())) {
                        profile.getParty().disband();
                        break;
                    }
                    profile.getParty().leave(player, false);
                    break;
                }
                case EVENT_JOIN: {
                    new ActiveEventSelectEventMenu().openMenu(player);
                    break;
                }
                case SUMO_LEAVE: {
                    final Sumo activeSumo = Array.get().getSumoManager().getActiveSumo();
                    if (activeSumo == null) {
                        player.sendMessage(CC.RED + "There is no active sumo.");
                        return;
                    }
                    if (!profile.isInSumo() || !activeSumo.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active sumo.");
                        return;
                    }
                    Array.get().getSumoManager().getActiveSumo().handleLeave(player);
                    break;
                }
                case BRACKETS_LEAVE: {
                    final Brackets activeBrackets = Array.get().getBracketsManager().getActiveBrackets();
                    if (activeBrackets == null) {
                        player.sendMessage(CC.RED + "There is no active brackets.");
                        return;
                    }
                    if (!profile.isInBrackets() || !activeBrackets.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active brackets.");
                        return;
                    }
                    Array.get().getBracketsManager().getActiveBrackets().handleLeave(player);
                    break;
                }
                case FFA_LEAVE: {
                    final FFA activeFfa = Array.get().getFfaManager().getActiveFFA();
                    if (activeFfa == null) {
                        player.sendMessage(CC.RED + "There is no active Juggernaut.");
                        return;
                    }
                    if (!profile.isInFfa() || !activeFfa.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active Juggernaut.");
                        return;
                    }
                    Array.get().getFfaManager().getActiveFFA().handleLeave(player);
                    break;
                }
                case JUGGERNAUT_LEAVE: {
                    final Juggernaut activeJuggernaut = Array.get().getJuggernautManager().getActiveJuggernaut();
                    if (activeJuggernaut == null) {
                        player.sendMessage(CC.RED + "There is no active Juggernaut.");
                        return;
                    }
                    if (!profile.isInJuggernaut() || !activeJuggernaut.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active Juggernaut.");
                        return;
                    }
                    Array.get().getJuggernautManager().getActiveJuggernaut().handleLeave(player);
                    break;
                }
                case PARKOUR_LEAVE: {
                    final Parkour activeParkour = Array.get().getParkourManager().getActiveParkour();
                    if (activeParkour == null) {
                        player.sendMessage(CC.RED + "There is no active Parkour.");
                        return;
                    }
                    if (!profile.isInParkour() || !activeParkour.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active Parkour.");
                        return;
                    }
                    Array.get().getParkourManager().getActiveParkour().handleLeave(player);
                    break;
                }
                case WIPEOUT_LEAVE: {
                    final Wipeout activeWipeout = Array.get().getWipeoutManager().getActiveWipeout();
                    if (activeWipeout == null) {
                        player.sendMessage(CC.RED + "There is no active Wipeout.");
                        return;
                    }
                    if (!profile.isInWipeout() || !activeWipeout.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active Wipeout.");
                        return;
                    }
                    Array.get().getWipeoutManager().getActiveWipeout().handleLeave(player);
                    break;
                }
                case SKYWARS_LEAVE: {
                    final SkyWars activeSkyWars = Array.get().getSkyWarsManager().getActiveSkyWars();
                    if (activeSkyWars == null) {
                        player.sendMessage(CC.RED + "There is no active SkyWars.");
                        return;
                    }
                    if (!profile.isInSkyWars() || !activeSkyWars.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active SkyWars.");
                        return;
                    }
                    Array.get().getSkyWarsManager().getActiveSkyWars().handleLeave(player);
                    break;
                }
                case SPLEEF_LEAVE: {
                    final Spleef activeSpleef = Array.get().getSpleefManager().getActiveSpleef();
                    if (activeSpleef == null) {
                        player.sendMessage(CC.RED + "There is no active Spleef.");
                        return;
                    }
                    if (!profile.isInSpleef() || !activeSpleef.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active Spleef.");
                        return;
                    }
                    Array.get().getSpleefManager().getActiveSpleef().handleLeave(player);
                    break;
                }
                case SPECTATE_STOP: {
                    if (profile.isInFight() && !profile.getMatch().getTeamPlayer(player).isAlive()) {
                        profile.getMatch().getTeamPlayer(player).setDisconnected(true);
                        profile.setState(ProfileState.IN_LOBBY);
                        profile.setMatch(null);
                        break;
                    }
                    if (!profile.isSpectating()) {
                        player.sendMessage(CC.RED + "You are not spectating a match.");
                        break;
                    }
                    if (profile.getMatch() != null) {
                        profile.getMatch().removeSpectator(player);
                        break;
                    }
                    if (profile.getSumo() != null) {
                        profile.getSumo().removeSpectator(player);
                        break;
                    }
                    if (profile.getBrackets() != null) {
                        profile.getBrackets().removeSpectator(player);
                        break;
                    }
                    if (profile.getFfa() != null) {
                        profile.getFfa().removeSpectator(player);
                        break;
                    }
                    if (profile.getJuggernaut() != null) {
                        profile.getJuggernaut().removeSpectator(player);
                        break;
                    }
                    if (profile.getParkour() != null) {
                        profile.getParkour().removeSpectator(player);
                        break;
                    }
                    if (profile.getWipeout() != null) {
                        profile.getWipeout().removeSpectator(player);
                        break;
                    }
                    if (profile.getSkyWars() != null) {
                        profile.getSkyWars().removeSpectator(player);
                        break;
                    }
                    if (profile.getSpleef() != null) {
                        profile.getSpleef().removeSpectator(player);
                        break;
                    }
                    break;
                }
                case REMATCH_REQUEST: {
                    if (profile.getRematchData() == null) {
                        player.sendMessage(CC.RED + "You do not have anyone to re-match.");
                        return;
                    }
                    profile.checkForHotbarUpdate();
                    if (profile.getRematchData() == null) {
                        player.sendMessage(CC.RED + "That player is no longer available.");
                        return;
                    }
                    final ProfileRematchData profileRematchData = profile.getRematchData();
                    if (profileRematchData.isReceive()) {
                        profileRematchData.accept();
                    }
                    else {
                        if (profileRematchData.isSent()) {
                            player.sendMessage(CC.RED + "You have already sent a rematch request to that player.");
                            return;
                        }
                        profileRematchData.request();
                    }
                    break;
                }
                case REMATCH_ACCEPT: {
                    if (profile.getRematchData() == null) {
                        player.sendMessage(CC.RED + "You do not have anyone to re-match.");
                        return;
                    }
                    profile.checkForHotbarUpdate();
                    if (profile.getRematchData() == null) {
                        player.sendMessage(CC.RED + "That player is no longer available.");
                        return;
                    }
                    final ProfileRematchData profileRematchData = profile.getRematchData();
                    if (profileRematchData.isReceive()) {
                        profileRematchData.accept();
                    }
                    else {
                        if (profileRematchData.isSent()) {
                            player.sendMessage(CC.RED + "You have already sent a rematch request to that player.");
                            return;
                        }
                        profileRematchData.request();
                    }
                    break;
                }
                default: {}
            }
        }
    }
}
