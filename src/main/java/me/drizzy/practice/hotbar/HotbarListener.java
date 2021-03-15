package me.drizzy.practice.hotbar;

import me.drizzy.practice.enums.HotbarType;
import me.drizzy.practice.event.menu.ActiveEventSelectEventMenu;
import me.drizzy.practice.event.types.lms.LMS;
import me.drizzy.practice.event.types.parkour.Parkour;
import me.drizzy.practice.event.types.wizard.Wizard;
import me.drizzy.practice.profile.meta.ProfileRematchData;
import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.brackets.Brackets;
import me.drizzy.practice.event.types.spleef.Spleef;
import me.drizzy.practice.event.types.sumo.Sumo;
import me.drizzy.practice.kiteditor.menu.KitEditorSelectKitMenu;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.enums.PartyMessageType;
import me.drizzy.practice.party.menu.ManagePartySettings;
import me.drizzy.practice.party.menu.OtherPartiesMenu;
import me.drizzy.practice.party.menu.PartyEventSelectEventMenu;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.profile.menu.PlayerMenu;
import me.drizzy.practice.settings.SettingsMenu;
import me.drizzy.practice.queue.QueueType;
import me.drizzy.practice.queue.menu.QueueSelectKitMenu;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.config.BasicConfigurationFile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import me.drizzy.practice.util.PlayerUtil;

public class HotbarListener implements Listener
{

    BasicConfigurationFile config = Array.getInstance().getMessagesConfig();

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
            final Player player = event.getPlayer();
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            final HotbarType hotbarType= Hotbar.fromItemStack(event.getItem());
            if (hotbarType == null) {
                return;
            }
            event.setCancelled(true);
            switch (hotbarType) {
                case QUEUE_JOIN_RANKED: {
                    if (!player.hasPermission("array.donator")) {
                        if (Array.getInstance().getMainConfig().getBoolean("Ranked.Require-Kills")) {
                            if (profile.getTotalWins() < Array.getInstance().getMainConfig().getInteger("Ranked.Required-Kills")) {
                                for ( String error : Array.getInstance().getMessagesConfig().getStringList("Ranked.Required") ) {
                                    player.sendMessage(CC.translate(error));
                                }
                                break;
                            }
                        }
                    }
                    if (!Array.getInstance().getMainConfig().getBoolean("Ranked.Enabled")) {
                        player.sendMessage(CC.translate(Array.getInstance().getMessagesConfig().getString("Ranked.Disabled")));
                        break;
                    }
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
                case PARTY_INFO: {
                    profile.getParty().sendInformation(player);
                    break;
                }
                case PARTY_SETTINGS: {
                    new ManagePartySettings().openMenu(event.getPlayer());
                    break;
                }
                case SETTINGS_MENU: {
                    new SettingsMenu().openMenu(event.getPlayer());
                    break;
                }
                case LEADERBOARDS_MENU: {
                    new PlayerMenu().openMenu(event.getPlayer());
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
                        player.sendMessage(CC.translate(config.getString("Party.Already-Have-Party")));
                        return;
                    }
                    if (!profile.isInLobby()) {
                        player.sendMessage(CC.translate(config.getString("Party.Not-In-Lobby")));
                        return;
                    }
                    profile.setParty(new Party(player));
                    PlayerUtil.reset(player, false);
                    profile.refreshHotbar();
                    player.sendMessage(PartyMessageType.CREATED.format());
                    break;
                }
                case PARTY_DISBAND: {
                    if (profile.getParty() == null) {
                        player.sendMessage(CC.translate(config.getString("Party.Dont-Have-Party")));
                        return;
                    }
                    if (!profile.getParty().isLeader(player.getUniqueId())) {
                        player.sendMessage(CC.translate(config.getString("Party.Not-Leader")));
                        return;
                    }
                    profile.getParty().disband();
                    break;
                }
                case PARTY_INFORMATION: {
                    if (profile.getParty() == null) {
                        player.sendMessage(CC.translate(config.getString("Party.Dont-Have-Party")));
                        return;
                    }
                    profile.getParty().sendInformation(player);
                    break;
                }
                case PARTY_LEAVE: {
                    if (profile.getParty() == null) {
                        player.sendMessage(CC.translate(config.getString("Party.Dont-Have-Party")));
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
                    final Sumo activeSumo = Array.getInstance().getSumoManager().getActiveSumo();
                    if (activeSumo == null) {
                        player.sendMessage(CC.RED + "There is no active sumo.");
                        return;
                    }
                    if (!profile.isInSumo() || !activeSumo.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active sumo.");
                        return;
                    }
                    Array.getInstance().getSumoManager().getActiveSumo().handleLeave(player);
                    break;
                }
                case BRACKETS_LEAVE: {
                    final Brackets activeBrackets = Array.getInstance().getBracketsManager().getActiveBrackets();
                    if (activeBrackets == null) {
                        player.sendMessage(CC.RED + "There is no active brackets.");
                        return;
                    }
                    if (!profile.isInBrackets() || !activeBrackets.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active brackets.");
                        return;
                    }
                    Array.getInstance().getBracketsManager().getActiveBrackets().handleLeave(player);
                    break;
                }
                case LMS_LEAVE: {
                    final LMS activeLMS = Array.getInstance().getLMSManager().getActiveLMS();
                    if (activeLMS == null) {
                        player.sendMessage(CC.RED + "There is no active KoTH.");
                        return;
                    }
                    if (!profile.isInLMS() || !activeLMS.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active KoTH.");
                        return;
                    }
                    Array.getInstance().getLMSManager().getActiveLMS().handleLeave(player);
                    break;
                }
                case PARKOUR_LEAVE: {
                    final Parkour activeParkour = Array.getInstance().getParkourManager().getActiveParkour();
                    if (activeParkour == null) {
                        player.sendMessage(CC.RED + "There is no active Parkour.");
                        return;
                    }
                    if (!profile.isInParkour() || !activeParkour.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active Parkour.");
                        return;
                    }
                    Array.getInstance().getParkourManager().getActiveParkour().handleLeave(player);
                    break;
                }
                case WIZARD_LEAVE: {
                    final Wizard activeWizard = Array.getInstance().getWizardManager().getActiveWizard();
                    if (activeWizard == null) {
                        player.sendMessage(CC.RED + "There is no active Wizard.");
                        return;
                    }
                    if (!profile.isInParkour() || !activeWizard.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active Wizard.");
                        return;
                    }
                    Array.getInstance().getWizardManager().getActiveWizard().handleLeave(player);
                    break;
                }
                case PARKOUR_SPAWN: {
                    final Parkour activeParkour = Array.getInstance().getParkourManager().getActiveParkour();
                    if (activeParkour == null) {
                        player.sendMessage(CC.RED + "There is no active Parkour.");
                        return;
                    }
                    if (!profile.isInParkour() || !activeParkour.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active Parkour.");
                        return;
                    }
                    if (profile.getParkour().getEventPlayer(player).getLastLocation() != null) {
                        player.teleport(profile.getParkour().getEventPlayer(player).getLastLocation());
                        break;
                    } else {
                        player.teleport(Array.getInstance().getParkourManager().getParkourSpawn());
                    }
                }
                case SPLEEF_LEAVE: {
                    final Spleef activeSpleef = Array.getInstance().getSpleefManager().getActiveSpleef();
                    if (activeSpleef == null) {
                        player.sendMessage(CC.RED + "There is no active Spleef.");
                        return;
                    }
                    if (!profile.isInSpleef() || !activeSpleef.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(CC.RED + "You are not apart of the active Spleef.");
                        return;
                    }
                    Array.getInstance().getSpleefManager().getActiveSpleef().handleLeave(player);
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
                    if (profile.getLms() != null) {
                        profile.getLms().removeSpectator(player);
                        break;
                    }
                    if (profile.getParkour() != null) {
                        profile.getParkour().removeSpectator(player);
                        break;
                    }
                    if (profile.getSpleef() != null) {
                        profile.getSpleef().removeSpectator(player);
                        break;
                    }
                    break;
                }
                case REMATCH_REQUEST:
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

