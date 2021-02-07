package me.drizzy.practice.party.menu;

import java.beans.ConstructorProperties;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.types.HCFMatch;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.party.PartyEvent;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.external.ItemBuilder;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import me.drizzy.practice.util.external.menu.Button;
import java.util.Map;
import org.bukkit.entity.Player;
import me.drizzy.practice.util.external.menu.Menu;

public class PartyEventSelectEventMenu extends Menu
{
    @Override
    public String getTitle(final Player player) {
        return "&bSelect a party event";
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
            buttons.put(2, new SelectEventButton(PartyEvent.SPLIT));
            buttons.put(4, new SelectEventButton(PartyEvent.FFA));
            buttons.put(6, new SelectEventButton(PartyEvent.HCF));
        return buttons;
    }

    private static class SelectEventButton extends Button
    {
        private final PartyEvent partyEvent;

        @Override
        public ItemStack getButtonItem(final Player player) {
            List<String> lore = new ArrayList<>();
            lore.add(CC.GRAY + CC.STRIKE_THROUGH + "------------------------");
            if (this.partyEvent == PartyEvent.FFA) {
                lore.add(CC.translate("&7Unleash your party in"));
                lore.add(CC.translate("&7an &b&lFFA Match&7, Let them"));
                lore.add(CC.translate("&7fight for themselves"));
                lore.add(CC.translate("&7Last player standing wins!"));
            }
            if (this.partyEvent == PartyEvent.SPLIT) {
                lore.add(CC.translate("&7Split your party into"));
                lore.add(CC.translate("&b&lTwo Teams &7and let them"));
                lore.add(CC.translate("&7duel as Team vs Team,"));
                lore.add(CC.translate("&7Last Team standing wins!"));
            }
            if (this.partyEvent == PartyEvent.HCF) {
                lore.add(CC.translate("&7Split your party into"));
                lore.add(CC.translate("&7team teams and let them duel"));
                lore.add(CC.translate("&7with the usual &b&lHCF Kits"));
                lore.add(CC.translate("&7Last Team standing wins!"));
            }
            lore.add(CC.GRAY + CC.STRIKE_THROUGH + "------------------------");
            return new ItemBuilder(this.partyEvent.getMaterial()).name("&b&l" + this.partyEvent.getName()).lore(lore).build();
        }

        @Override
        public void clicked(final Player player, final ClickType clickType) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getParty() == null) {
                player.sendMessage(CC.RED + "You are not in a party.");
                return;
            }
            if (this.partyEvent == PartyEvent.FFA || this.partyEvent == PartyEvent.SPLIT) {
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
                new PartyEventSelectKitMenu(this.partyEvent).openMenu(player);
            }
            else {
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
                player.closeInventory();
                if (profile.getParty() == null) {
                    player.sendMessage(CC.RED + "You are not in a party.");
                    return;
                }
                if (profile.getParty().getTeamPlayers().size() <= 1) {
                    player.sendMessage(CC.RED + "You do not have enough players in your party to start a party event.");
                    return;
                }
                final Party party = profile.getParty();
                Arena arena;
                if (this.partyEvent.equals(PartyEvent.HCF)) {
                    arena = Arena.getRandom(Kit.getByName("HCFTeamFight"));
                }
                else {
                    player.sendMessage(CC.RED + "There are no available arenas.");
                    return;
                }
                if (arena == null) {
                    player.sendMessage(CC.RED + "There are no available arenas.");
                    return;
                }
                arena.setActive(true);
                final Team teamA = new Team(new TeamPlayer(party.getPlayers().get(0)));
                final Team teamB = new Team(new TeamPlayer(party.getPlayers().get(1)));
                final List<Player> players = new ArrayList<>(party.getPlayers());
                Collections.shuffle(players);
                Match match;
                match = new HCFMatch(teamA, teamB, arena);
                for (final Player otherPlayer : players) {
                    if (!teamA.getLeader().getUuid().equals(otherPlayer.getUniqueId())) {
                        if (teamB.getLeader().getUuid().equals(otherPlayer.getUniqueId())) {
                            continue;
                        }
                        if (teamA.getTeamPlayers().size() > teamB.getTeamPlayers().size()) {
                            teamB.getTeamPlayers().add(new TeamPlayer(otherPlayer));
                        }
                        else {
                            teamA.getTeamPlayers().add(new TeamPlayer(otherPlayer));
                        }
                    }
                }
                match.start();
            }
        }

        @ConstructorProperties({ "partyEvent" })
        public SelectEventButton(final PartyEvent partyEvent) {
            this.partyEvent = partyEvent;
        }
    }
}
