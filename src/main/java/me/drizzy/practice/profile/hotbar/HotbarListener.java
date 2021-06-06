package me.drizzy.practice.profile.hotbar;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.essentials.Essentials;
import me.drizzy.practice.queue.QueueType;
import me.drizzy.practice.events.menu.ActiveEventSelectEventMenu;
import me.drizzy.practice.events.types.brackets.Brackets;
import me.drizzy.practice.events.types.gulag.Gulag;
import me.drizzy.practice.events.types.lms.LMS;
import me.drizzy.practice.events.types.parkour.Parkour;
import me.drizzy.practice.events.types.spleef.Spleef;
import me.drizzy.practice.events.types.sumo.Sumo;
import me.drizzy.practice.kit.kiteditor.menu.KitEditorSelectKitMenu;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.party.menu.ManagePartySettings;
import me.drizzy.practice.party.menu.OtherPartiesMenu;
import me.drizzy.practice.party.menu.PartyClassSelectMenu;
import me.drizzy.practice.party.menu.PartyEventSelectEventMenu;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.ProfileState;
import me.drizzy.practice.profile.menu.MainMenu;
import me.drizzy.practice.duel.RematchProcedure;
import me.drizzy.practice.queue.menu.QueueSelectKitMenu;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.other.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class HotbarListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
            final Player player = event.getPlayer();
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            final HotbarType hotbarType = Hotbar.fromItemStack(event.getItem());
            if (hotbarType == null) {
                return;
            }
            event.setCancelled(true);
            switch (hotbarType) {
                case QUEUE_JOIN_RANKED: {
                    if (!Essentials.getMeta().isRankedEnabled()) {
                        player.sendMessage(Locale.RANKED_DISABLED.toString());
                        break;
                    }
                    if (Essentials.getMeta().isLimitPing()) {
                        if (PlayerUtil.getPing(player) > Essentials.getMeta().getPingLimit()) {
                            player.sendMessage(CC.translate("&cYou're ping is too high to join ranked queue!"));
                            break;
                        }
                    }
                    if (!player.hasPermission("array.profile.ranked")) {
                        if (Essentials.getMeta().isRequireKills()) {
                            if (profile.getTotalWins() < Essentials.getMeta().getRequiredKills()) {
                                for ( String error : Locale.RANKED_REQUIRED.toList()) {
                                    player.sendMessage(CC.translate(error));
                                }
                                break;
                            }
                        }
                    }
                    if (!profile.isBusy()) {
                        new QueueSelectKitMenu(QueueType.RANKED).openMenu(event.getPlayer());
                        break;
                    }
                    break;
                }
                case QUEUE_JOIN_UNRANKED: {
                    if (!profile.isBusy()) {
                        new QueueSelectKitMenu(QueueType.UNRANKED).openMenu(event.getPlayer());
                        break;
                    }
                    break;
                }
                case QUEUE_JOIN_CLAN: {
                    if (!profile.hasClan()) {
                        player.sendMessage(CC.translate("&cYou don't have a Clan!"));
                        break;
                    }
                    if (!profile.isBusy()) {
                        new QueueSelectKitMenu(QueueType.CLAN).openMenu(event.getPlayer());
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
                case PARTY_CLASSES:
                    new PartyClassSelectMenu().openMenu(player);
                    break;
                case PARTY_SETTINGS: {
                    new ManagePartySettings().openMenu(event.getPlayer());
                    break;
                }
                case MAIN_MENU: {
                    new MainMenu().openMenu(event.getPlayer());
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
                        player.sendMessage(Locale.PARTY_ALREADYHAVE.toString());
                        return;
                    }
                    if (!profile.isInLobby()) {
                        player.sendMessage(Locale.PARTY_NOTLOBBY.toString());
                        return;
                    }
                    profile.setParty(new Party(player));
                    profile.refreshHotbar();
                    player.sendMessage(Locale.PARTY_CREATED.toString());
                    break;
                }
                case PARTY_DISBAND: {
                    if (profile.getParty() == null) {
                        player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
                        return;
                    }
                    if (!profile.getParty().isLeader(player.getUniqueId())) {
                        player.sendMessage(Locale.PARTY_NOTLEADER.toString());
                        return;
                    }
                    profile.getParty().disband();
                    break;
                }
                case PARTY_INFORMATION: {
                    if (profile.getParty() == null) {
                        player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
                        return;
                    }
                    profile.getParty().sendInformation(player);
                    break;
                }
                case PARTY_LEAVE: {
                    if (profile.getParty() == null) {
                        player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
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
                        player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "sumo"));
                        return;
                    }
                    if (!profile.isInSumo() || !activeSumo.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "sumo"));
                        return;
                    }
                    Array.getInstance().getSumoManager().getActiveSumo().handleLeave(player);
                    break;
                }
                case BRACKETS_LEAVE: {
                    final Brackets activeBrackets=Array.getInstance().getBracketsManager().getActiveBrackets();
                    if (activeBrackets == null) {
                        player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "brackets"));
                        return;
                    }
                    if (!profile.isInBrackets() || !activeBrackets.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "brackets"));
                        return;
                    }
                    Array.getInstance().getBracketsManager().getActiveBrackets().handleLeave(player);
                    break;
                }
                case LMS_LEAVE: {
                    final LMS activeLMS = Array.getInstance().getLMSManager().getActiveLMS();
                    if (activeLMS == null) {
                        player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "LMS"));
                        return;
                    }
                    if (!profile.isInLMS() || !activeLMS.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "LMS"));
                        return;
                    }
                    Array.getInstance().getLMSManager().getActiveLMS().handleLeave(player);
                    break;
                }
                case PARKOUR_SPAWN: {
                    final Parkour activeParkour = Array.getInstance().getParkourManager().getActiveParkour();
                    if (activeParkour == null) {
                        player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "parkour"));
                        return;
                    }
                    if (!profile.isInParkour() || !activeParkour.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "parkour"));
                        return;
                    }
                    if (activeParkour.getEventPlayer(player).getLastLocation() != null) {
                        player.teleport(activeParkour.getEventPlayer(player).getLastLocation());
                    } else {
                        player.teleport(Array.getInstance().getParkourManager().getParkourSpawn());
                    }
                    break;
                }
                case PARKOUR_LEAVE: {
                    final Parkour activeParkour = Array.getInstance().getParkourManager().getActiveParkour();
                    if (activeParkour == null) {
                        player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "parkour"));
                        return;
                    }
                    if (!profile.isInParkour() || !activeParkour.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "parkour"));
                        return;
                    }
                    Array.getInstance().getParkourManager().getActiveParkour().handleLeave(player);
                    break;
                }
                case GULAG_LEAVE: {
                    final Gulag activeGulag = Array.getInstance().getGulagManager().getActiveGulag();
                    if (activeGulag == null) {
                        player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "gulag"));
                        return;
                    }
                    if (!profile.isInGulag() || !activeGulag.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "gulag"));
                        return;
                    }
                    Array.getInstance().getGulagManager().getActiveGulag().handleLeave(player);
                    break;
                }
                case SPLEEF_LEAVE: {
                    final Spleef activeSpleef = Array.getInstance().getSpleefManager().getActiveSpleef();
                    if (activeSpleef == null) {
                        player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "spleef"));
                        return;
                    }
                    if (!profile.isInSpleef() || !activeSpleef.getEventPlayers().containsKey(player.getUniqueId())) {
                        player.sendMessage(Locale.ERROR_NOTPARTOF.toString().replace("<event>", "spleef"));
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
                        player.sendMessage(Locale.ERROR_NOTSPECTATING.toString());
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
                    if (profile.getGulag() != null) {
                        profile.getGulag().removeSpectator(player);
                        break;
                    }
                    break;
                }
                case SPECTATOR_SHOW: {
                    if (!profile.isInMatch()) {
                        player.sendMessage(CC.translate("&7You are not in any match."));
                        break;
                    }
                    if (!profile.isSpectating()) {
                        player.sendMessage(CC.translate("&7You are not spectating any match."));
                    }
                    profile.getMatch().toggleSpectators(player);
                }
                case SPECTATOR_HIDE: {
                    if (!profile.isInMatch()) {
                        player.sendMessage(CC.translate("&7You are not in any match."));
                        break;
                    }
                    if (!profile.isSpectating()) {
                        player.sendMessage(CC.translate("&7You are not spectating any match."));
                    }
                    profile.getMatch().toggleSpectators(player);
                }
                case REMATCH_REQUEST:
                case REMATCH_ACCEPT: {
                    if (profile.getRematchData() == null) {
                        player.sendMessage(Locale.ERROR_NOREMATCH.toString());
                        return;
                    }
                    profile.checkForHotbarUpdate();
                    if (profile.getRematchData() == null) {
                        player.sendMessage(Locale.ERROR_EXPIREREMATCH.toString());
                        return;
                    }
                    final RematchProcedure rematchProcedure= profile.getRematchData();
                    if (rematchProcedure.isReceive()) {
                        rematchProcedure.accept();
                    }
                    else {
                        if (rematchProcedure.isSent()) {
                            player.sendMessage(Locale.ERROR_REMATCHSENT.toString());
                            return;
                        }
                        rematchProcedure.request();
                    }
                    break;
                }
                default: {}
            }
        }
    }
}

