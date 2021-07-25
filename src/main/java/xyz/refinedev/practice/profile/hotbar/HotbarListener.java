package xyz.refinedev.practice.profile.hotbar;

import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.essentials.Essentials;
import xyz.refinedev.practice.events.Event;
import xyz.refinedev.practice.events.menu.EventTeamMenu;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.kit.kiteditor.menu.KitEditorSelectKitMenu;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.menu.ManagePartySettings;
import xyz.refinedev.practice.party.menu.OtherPartiesMenu;
import xyz.refinedev.practice.party.menu.PartyClassSelectMenu;
import xyz.refinedev.practice.party.menu.PartyEventSelectEventMenu;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.profile.menu.MainMenu;
import xyz.refinedev.practice.duel.RematchProcedure;
import xyz.refinedev.practice.queue.menu.QueueSelectKitMenu;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.other.PlayerUtil;
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
                 player.chat("/event join");
                 break;
                }
                case EVENT_TEAM: {
                    final Event activeEvent = Array.getInstance().getEventManager().getActiveEvent();
                    if (activeEvent == null) {
                        player.sendMessage(Locale.ERROR_NOTACTIVE.toString());
                        return;
                    }
                    if (!profile.isInEvent() || !activeEvent.getPlayers().contains(player)) {
                        player.sendMessage(Locale.ERROR_NOTPARTOF.toString());
                        return;
                    }
                    new EventTeamMenu(activeEvent).openMenu(player);
                    break;
                }
                case EVENT_LEAVE: {
                    final Event activeEvent = Array.getInstance().getEventManager().getActiveEvent();
                    if (activeEvent == null) {
                        player.sendMessage(Locale.ERROR_NOTACTIVE.toString());
                        return;
                    }
                    if (!profile.isInEvent() || !activeEvent.getPlayers().contains(player)) {
                        player.sendMessage(Locale.ERROR_NOTPARTOF.toString());
                        return;
                    }
                    activeEvent.handleLeave(player);
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
                    if (profile.getEvent() != null) {
                        profile.getEvent().removeSpectator(player);
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

