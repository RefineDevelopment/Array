package xyz.refinedev.practice.party.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.FFAMatch;
import xyz.refinedev.practice.match.types.TeamMatch;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.enums.PartyEventType;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.*;

@RequiredArgsConstructor
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

    @RequiredArgsConstructor
    private class SelectKitButton extends Button {

        private final PartyEventType partyEventType;
        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(this.kit.getDisplayIcon()).name("&c&l" + this.kit.getName()).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.closeInventory();

            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile.getParty() == null) {
                player.sendMessage(CC.RED + "You are not in a party.");
                return;
            }

            if (profile.getParty().getTeamPlayers().size() <= 1) {
                player.sendMessage(CC.RED + "You do not have enough players in your party to start a party event.");
                return;
            }

            Party party = profile.getParty();
            Arena arena = Arena.getRandom(this.kit);

            if (arena == null) {
                player.sendMessage(CC.RED + "There are no available arenas.");
                return;
            }

            arena.setActive(true);

            Match match;
            if (this.partyEventType == PartyEventType.PARTY_FFA) {
                Team team = new Team(new TeamPlayer(party.getLeader().getPlayer()));

                List<Player> players = new ArrayList<>(party.getPlayers());
                match = new FFAMatch(team, this.kit, arena);

                for (Player otherPlayer : players) {
                    if (team.getLeader().getUniqueId().equals(otherPlayer.getUniqueId())) {
                        continue;
                    }
                    team.getTeamPlayers().add(new TeamPlayer(otherPlayer));
                }
            } else {
                Team teamA = new Team(new TeamPlayer(party.getPlayers().get(0)));
                Team teamB = new Team(new TeamPlayer(party.getPlayers().get(1)));

                List<Player> shuffled = new ArrayList<>(party.getPlayers());
                //Shuffling twice cuz noobs saying its not random
                Collections.shuffle(shuffled);
                Collections.shuffle(shuffled);

                match = kit.createTeamKitMatch(teamA, teamB, this.kit, arena);

                for (Player shuffledPlayer : shuffled) {
                    if (!teamA.getLeader().getUniqueId().equals(shuffledPlayer.getUniqueId())) {
                        if (teamB.getLeader().getUniqueId().equals(shuffledPlayer.getUniqueId())) {
                            continue;
                        }
                        if (teamA.getTeamPlayers().size() > teamB.getTeamPlayers().size()) {
                            teamB.getTeamPlayers().add(new TeamPlayer(shuffledPlayer));
                        } else {
                            teamA.getTeamPlayers().add(new TeamPlayer(shuffledPlayer));
                        }
                    }
                }
            }
            TaskUtil.run(match::start);
        }
    }

    public boolean getCheck(PartyEventType type, Kit kit) {
        if (type == PartyEventType.PARTY_FFA && (kit.getGameRules().isSumo() || kit.getGameRules().isBoxing())) {
            return false;
        }
        return true;
    }
}