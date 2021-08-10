package xyz.refinedev.practice.party.menu;

import lombok.AllArgsConstructor;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.party.enums.PartyEventType;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.FFAMatch;
import xyz.refinedev.practice.match.types.TeamMatch;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@AllArgsConstructor
public class PartySelectKitMenu extends Menu {

    private final PartyEventType partyEventType;

    @Override
    public String getTitle(Player player) {
        return "&cSelect a kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for (Kit kit : Kit.getKits()) {
            if (kit.isEnabled() && kit.isParty()) {
                if (this.getCheck(partyEventType, kit)) {
                    buttons.put(buttons.size(), new SelectKitButton(this.partyEventType, kit));
                }
            }
        }
        return buttons;
    }

    @AllArgsConstructor
    private static class SelectKitButton extends Button {

        private final PartyEventType partyEventType;
        private final Kit kit;

        @Override
        public ItemStack getButtonItem(final Player player) {
            return new ItemBuilder(this.kit.getDisplayIcon()).name("&c&l" + this.kit.getName()).build();
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
                player.sendMessage(CC.RED + "You do not have enough players in your party to start a party events.");
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
                Team team = new Team(new TeamPlayer(party.getLeader().getPlayer()));
                List<Player> players = new ArrayList<>(party.getPlayers());
                match = new FFAMatch(team, this.kit, arena);
                for (final Player otherPlayer : players) {
                    if (team.getLeader().getUuid().equals(otherPlayer.getUniqueId())) {
                        continue;
                    }
                    team.getTeamPlayers().add(new TeamPlayer(otherPlayer));
                }
            } else {
                Team teamA = new Team(new TeamPlayer(party.getPlayers().get(0)));
                Team teamB = new Team(new TeamPlayer(party.getPlayers().get(1)));
                List<Player> players2 = new ArrayList<>(party.getPlayers());
                Collections.shuffle(players2);
                match = new TeamMatch(teamA, teamB, this.kit, arena);
                for (Player otherPlayer2 : players2) {
                    if (!teamA.getLeader().getUuid().equals(otherPlayer2.getUniqueId())) {
                        if (teamB.getLeader().getUuid().equals(otherPlayer2.getUniqueId())) {
                            continue;
                        }
                        if (teamA.getTeamPlayers().size() > teamB.getTeamPlayers().size()) {
                            teamB.getTeamPlayers().add(new TeamPlayer(otherPlayer2));
                        } else {
                            teamA.getTeamPlayers().add(new TeamPlayer(otherPlayer2));
                        }
                    }
                }
            }
            match.start();
        }
    }

    public boolean getCheck(PartyEventType type, Kit kit) {
        if (type == PartyEventType.FFA && kit.getGameRules().isSumo()) {
            return false;
        }
        return true;
    }
}