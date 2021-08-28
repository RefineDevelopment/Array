package xyz.refinedev.practice.party.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.Team;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.HCFMatch;
import xyz.refinedev.practice.party.Party;
import xyz.refinedev.practice.party.enums.PartyEventType;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.custom.ButtonData;
import xyz.refinedev.practice.util.menu.custom.button.CustomButton;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.*;

public class PartyEventMenu extends Menu {

    private final List<ButtonData> customButtons = new ArrayList<>();

    private final Array plugin = Array.getInstance();
    private final FoldersConfigurationFile config = plugin.getMenuManager().getConfigByName("party_events");

    public PartyEventMenu() {
        List<ButtonData> custom = plugin.getMenuManager().loadCustomButtons(config);
        if (custom != null && !custom.isEmpty()) {
            this.customButtons.addAll(custom);
        }
    }

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Player player) {
        return config.getString("TITLE");
    }

    /**
     * Size of the inventory
     *
     * @return {@link Integer}
     */
    @Override
    public int getSize() {
        return config.getInteger("SIZE");
    }

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link HashMap}
     */
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        if (plugin.getConfigHandler().isHCF_ENABLED()) {
            for ( PartyEventType type : PartyEventType.values() ) {
                buttons.put(config.getInteger("BUTTONS." + type.name() + ".SLOT"), new SelectEventButton(type));
            }
        } else {
            buttons.put(config.getInteger("BUTTONS.PARTY_SPLIT.HCF_DISABLED_SLOT"), new SelectEventButton(PartyEventType.PARTY_SPLIT));
            buttons.put(config.getInteger("BUTTONS.PARTY_SPLIT.HCF_DISABLED_SLOT"), new SelectEventButton(PartyEventType.PARTY_FFA));
        }
        for ( ButtonData customButton : customButtons ) {
            buttons.put(customButton.getSlot(), new CustomButton(customButton));
        }
        return buttons;
    }

    private class SelectEventButton extends Button {

        private final PartyEventType partyEventType;
        private final String key;

        public SelectEventButton(PartyEventType partyEventType) {
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
            Profile profile = Profile.getByUuid(player.getUniqueId());
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

            Arena arena = Arena.getRandom(Kit.getHCFTeamFight());

            if (arena == null) {
                player.sendMessage(Locale.ERROR_NO_ARENAS.toString());
                return;
            }

            Team teamA = new Team(new TeamPlayer(party.getPlayers().get(0)));
            Team teamB = new Team(new TeamPlayer(party.getPlayers().get(1)));

            List<Player> players = new ArrayList<>(party.getPlayers());
            Collections.shuffle(players);

            Match match = new HCFMatch(teamA, teamB, arena);

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
}
