package me.array.ArrayPractice.party.menu;

import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.impl.FFAMatch;
import me.array.ArrayPractice.match.impl.TeamMatch;
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
import java.beans.ConstructorProperties;
import java.util.HashMap;
import me.array.ArrayPractice.util.external.menu.Button;
import java.util.Map;
import org.bukkit.entity.Player;
import me.array.ArrayPractice.util.external.menu.Menu;

public class PartyEventSelectKitMenu extends Menu
{
    private final PartyEvent partyEvent;
    
    @Override
    public String getTitle(final Player player) {
        return "&bSelect a kit";
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons =new HashMap<>();
        for (final Kit kit : Kit.getKits()) {
            if (kit.isEnabled() && !kit.getGameRules().isSumo()) {
                buttons.put(buttons.size(), new SelectKitButton(this.partyEvent, kit));
            }
        }
        return buttons;
    }
    
    @ConstructorProperties({ "partyEvent" })
    public PartyEventSelectKitMenu(final PartyEvent partyEvent) {
        this.partyEvent = partyEvent;
    }
    
    private static class SelectKitButton extends Button
    {
        private final PartyEvent partyEvent;
        private final Kit kit;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            return new ItemBuilder(this.kit.getDisplayIcon()).name("&b&l" + this.kit.getName()).build();
        }
        
        @Override
        public void clicked(final Player player, final ClickType clickType) {
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
            player.closeInventory();
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getParty() == null) {
                player.sendMessage(CC.RED + "You are not in a party.");
                return;
            }
            if (profile.getParty().getTeamPlayers().size() <= 1) {
                player.sendMessage(CC.RED + "You do not have enough players in your party to start an sumo.");
                return;
            }
            final Party party = profile.getParty();
            final Arena arena = Arena.getRandom(this.kit);
            if (arena == null) {
                player.sendMessage(CC.RED + "There are no available arenas.");
                return;
            }
            arena.setActive(true);
            Match match;
            if (this.partyEvent == PartyEvent.FFA) {
                final Team team = new Team(new TeamPlayer(party.getLeader().getPlayer()));
                final List<Player> players = new ArrayList<>();
                players.addAll(party.getPlayers());
                match = new FFAMatch(team, this.kit, arena);
                for (final Player otherPlayer : players) {
                    if (team.getLeader().getUuid().equals(otherPlayer.getUniqueId())) {
                        continue;
                    }
                    team.getTeamPlayers().add(new TeamPlayer(otherPlayer));
                }
            }
            else {
                final Team teamA = new Team(new TeamPlayer(party.getPlayers().get(0)));
                final Team teamB = new Team(new TeamPlayer(party.getPlayers().get(1)));
                final List<Player> players2 =new ArrayList<>();
                players2.addAll(party.getPlayers());
                Collections.shuffle(players2);
                match = new TeamMatch(teamA, teamB, this.kit, arena);
                for (final Player otherPlayer2 : players2) {
                    if (!teamA.getLeader().getUuid().equals(otherPlayer2.getUniqueId())) {
                        if (teamB.getLeader().getUuid().equals(otherPlayer2.getUniqueId())) {
                            continue;
                        }
                        if (teamA.getTeamPlayers().size() > teamB.getTeamPlayers().size()) {
                            teamB.getTeamPlayers().add(new TeamPlayer(otherPlayer2));
                        }
                        else {
                            teamA.getTeamPlayers().add(new TeamPlayer(otherPlayer2));
                        }
                    }
                }
            }
            match.start();
        }
        
        @ConstructorProperties({ "partyEvent", "kit" })
        public SelectKitButton(final PartyEvent partyEvent, final Kit kit) {
            this.partyEvent = partyEvent;
            this.kit = kit;
        }
    }
}
