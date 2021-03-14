package me.drizzy.practice.party.menu;

import me.drizzy.practice.enums.PartyEventType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.types.FFAMatch;
import me.drizzy.practice.match.types.SumoTeamMatch;
import me.drizzy.practice.match.types.TeamMatch;
import me.drizzy.practice.match.team.Team;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.party.Party;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.Menu;

import java.beans.ConstructorProperties;
import java.util.*;

public class PartyEventSelectKitMenu extends Menu
{
    private final PartyEventType partyEventType;

    @Override
    public String getTitle(final Player player) {
        return "&bSelect a kit";
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons =new HashMap<>();
        for (final Kit kit : Kit.getKits()) {
            if (kit.isEnabled() && !kit.getGameRules().isSumo()) {
                buttons.put(buttons.size(), new SelectKitButton(this.partyEventType, kit));
            }
        }
        return buttons;
    }

    @ConstructorProperties({ "partyEventType" })
    public PartyEventSelectKitMenu(final PartyEventType partyEventType) {
        this.partyEventType=partyEventType;
    }

    private static class SelectKitButton extends Button
    {
        private final PartyEventType partyEventType;
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
            if (this.partyEventType == PartyEventType.FFA) {
                if (this.kit.getGameRules().isSumo()) {
                    player.sendMessage(CC.RED + "You cannot start an ffa with the kit sumo!");
                }
                final Team team = new Team(new TeamPlayer(party.getLeader().getPlayer()));
                final List<Player> players=new ArrayList<>(party.getPlayers());
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
                final List<Player> players2=new ArrayList<>(party.getPlayers());
                Collections.shuffle(players2);
                if (this.kit.getGameRules().isSumo()) {
                    match = new SumoTeamMatch(teamA, teamB, this.kit, arena);
                } else {
                    match=new TeamMatch(teamA, teamB, this.kit, arena);
                }
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

        @ConstructorProperties({ "partyEventType", "kit" })
        public SelectKitButton(final PartyEventType partyEventType, final Kit kit) {
            this.partyEventType=partyEventType;
            this.kit = kit;
        }
    }
}