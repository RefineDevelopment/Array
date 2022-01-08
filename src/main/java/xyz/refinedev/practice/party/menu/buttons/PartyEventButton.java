package xyz.refinedev.practice.party.menu.buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.kit.TeamFightMatch;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.enums.PartyEventType;
import xyz.refinedev.practice.party.menu.PartySelectKitMenu;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/1/2021
 * Project: Array
 */

public class PartyEventButton extends Button {

    private final Array plugin = this.getPlugin();

    private final FoldersConfigurationFile config;
    private final PartyEventType partyEventType;
    private final String key;

    public PartyEventButton(FoldersConfigurationFile config, PartyEventType partyEventType) {
        this.config = config;
        this.partyEventType = partyEventType;
        this.key = "BUTTONS." + partyEventType.name() + ".";
    }

    /**
     * Get itemStack of the Button
     *
     * @param player {@link Player} viewing the menu
     * @return {@link ItemStack}
     */
    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.valueOf(config.getString(key + "MATERIAL")))
                .name(config.getString(key + "NAME"))
                .lore(config.getStringList(key + "LORE"))
                .clearFlags()
                .build();
    }

    /**
     * This method is called upon clicking an
     * item on the menu
     *
     * @param player {@link Player} clicking
     * @param clickType {@link ClickType}
     */
    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        Party party = profile.getParty();

        player.closeInventory();

        if (party == null) {
            player.sendMessage(Locale.PARTY_DONOTHAVE.toString());
            return;
        }

        if (party.isFighting() || party.isInTournament()) {
            player.sendMessage(Locale.PARTY_BUSY.toString());
            return;
        }

        if (party.getPlayers().size() < 2) {
            player.sendMessage(Locale.PARTY_EVENT_NEED.toString());
            return;
        }

        if (this.partyEventType == PartyEventType.PARTY_FFA || this.partyEventType == PartyEventType.PARTY_SPLIT) {
            new PartySelectKitMenu(this.partyEventType).openMenu(player);
            return;
        }

        Arena arena = plugin.getArenaManager().getByKit(plugin.getKitManager().getTeamFight());

        if (arena == null) {
            player.sendMessage(Locale.ERROR_NO_ARENAS.toString());
            return;
        }

        Team teamA = new Team(new TeamPlayer(party.getPlayers().get(0)));
        Team teamB = new Team(new TeamPlayer(party.getPlayers().get(1)));

        List<Player> players = new ArrayList<>(party.getPlayers());
        Collections.shuffle(players);

        Match match = new TeamFightMatch(teamA, teamB, arena);

        //Add players to the newly created teams
        for ( Player otherPlayer : players ) {
            if (!teamA.getLeader().getUniqueId().equals(otherPlayer.getUniqueId())) {
                if (teamB.getLeader().getUniqueId().equals(otherPlayer.getUniqueId())) {
                    continue;
                }
                if (teamA.getTeamPlayers().size() > teamB.getTeamPlayers().size()) {
                    teamB.getTeamPlayers().add(new TeamPlayer(otherPlayer));
                } else {
                    teamA.getTeamPlayers().add(new TeamPlayer(otherPlayer));
                }
            }
        }
        TaskUtil.run(match::start);
    }
}