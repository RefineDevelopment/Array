package me.array.ArrayPractice.party.menu;

import java.beans.ConstructorProperties;

import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.hcf.HCFMatch;
import me.array.ArrayPractice.match.impl.KoTHMatch;
import me.array.ArrayPractice.match.team.Team;
import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.party.PartyEvent;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import me.array.ArrayPractice.util.external.menu.Button;
import java.util.Map;
import org.bukkit.entity.Player;
import me.array.ArrayPractice.util.external.menu.Menu;

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
            return new ItemBuilder(this.partyEvent.getMaterial()).name("&b" + this.partyEvent.getName()).lore("&f" + this.partyEvent.getLore()).build();
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
                    arena = Arena.getRandom(Kit.getByName("NoDebuff"));
                }
                else {
                    arena = Arena.getRandom(Kit.getByName("KoTH"));
                }
                if (arena == null) {
                    player.sendMessage(CC.RED + "There are no available arenas.");
                    return;
                }
                arena.setActive(true);
                final Team teamA = new Team(new TeamPlayer(party.getPlayers().get(0)));
                final Team teamB = new Team(new TeamPlayer(party.getPlayers().get(1)));
                final List<Player> players = new ArrayList<>();
                players.addAll(party.getPlayers());
                Collections.shuffle(players);
                Match match;
                if (this.partyEvent.equals(PartyEvent.HCF)) {
                    match = new HCFMatch(teamA, teamB, arena);
                }
                else {
                    match = new KoTHMatch(teamA, teamB, arena);
                }
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
