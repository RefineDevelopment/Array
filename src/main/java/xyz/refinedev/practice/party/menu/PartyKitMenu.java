package xyz.refinedev.practice.party.menu;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.party.enums.PartyEventType;
import xyz.refinedev.practice.party.menu.buttons.PartyKitButton;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.HashMap;
import java.util.Map;

public class PartyKitMenu extends Menu {

    private final PartyEventType partyEventType;

    public PartyKitMenu(Array plugin, PartyEventType type) {
        super(plugin);
        
        this.partyEventType = type;
    }

    /**
     * Get menu's title
     *
     * @param player {@link Player} viewing the menu
     * @return {@link String} the title of the menu
     */
    @Override
    public String getTitle(Player player) {
        return "&cSelect a kit";
    }

    /**
     * Map of slots and buttons on that particular slot
     *
     * @param player {@link Player} player viewing the menu
     * @return {@link Map}
     */
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        for (Kit kit : this.getPlugin().getKitManager().getKits()) {
            if (!kit.isEnabled() || !kit.isParty() || !this.valid(partyEventType, kit)) continue;

            buttons.put(buttons.size(), new PartyKitButton(this.partyEventType, kit));
        }
        return buttons;
    }

    /**
     * Small check to see if a kit is valid for PartyFFA
     *
     * @param type {@link PartyEventType} party event type
     * @param kit {@link Kit} kit being checked
     * @return {@link Boolean}
     */
    public boolean valid(PartyEventType type, Kit kit) {
        return (type == PartyEventType.PARTY_FFA && (kit.getGameRules().isSumo() || kit.getGameRules().isBoxing()));
    }
}