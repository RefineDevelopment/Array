package me.array.ArrayPractice.party.menu;

import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.duel.DuelProcedure;
import me.array.ArrayPractice.duel.menu.DuelSelectKitMenu;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.party.OtherPartyEvent;
import me.array.ArrayPractice.party.Party;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import me.array.ArrayPractice.util.external.menu.Button;
import java.util.Map;

import org.bukkit.entity.Player;
import me.array.ArrayPractice.util.external.menu.Menu;

public class OtherPartiesSelectEventMenu extends Menu
{
    Player player;
    Player target;
    Party party;
    
    @Override
    public String getTitle(final Player player) {
        return "&fSelect an action for &b" + this.target.getName();
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<Integer, Button>();
        buttons.put(3, new SelectManageButton(OtherPartyEvent.KIT));
        buttons.put(5, new SelectManageButton(OtherPartyEvent.HCF));
        return buttons;
    }
    
    @ConstructorProperties({ "player", "target", "party" })
    public OtherPartiesSelectEventMenu(final Player player, final Player target, final Party party) {
        this.player = player;
        this.target = target;
        this.party = party;
    }
    
    private class SelectManageButton extends Button
    {
        private OtherPartyEvent partyManage;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            return new ItemBuilder((this.partyManage == OtherPartyEvent.KIT) ? Material.GOLD_SWORD : Material.GOLD_CHESTPLATE).name("&9&l" + this.partyManage.getName()).build();
        }
        
        @Override
        public void clicked(final Player player, final ClickType clickType) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.getParty() == null) {
                player.sendMessage(CC.RED + "You are not in a party.");
                return;
            }
            if (this.partyManage == OtherPartyEvent.KIT) {
                final Profile senderProfile = Profile.getByUuid(player.getUniqueId());
                final DuelProcedure procedure = new DuelProcedure(player, OtherPartiesSelectEventMenu.this.party.getLeader().getPlayer(), true);
                senderProfile.setDuelProcedure(procedure);
                new DuelSelectKitMenu().openMenu(player);
            }
            else {
                final Profile senderProfile = Profile.getByUuid(player.getUniqueId());
                final DuelProcedure procedure = new DuelProcedure(player, OtherPartiesSelectEventMenu.this.party.getLeader().getPlayer(), true);
                senderProfile.setDuelProcedure(procedure);
                final Arena arena = Arena.getRandom(Kit.getByName("NoDebuff"));
                procedure.setKit(Kit.getByName("HCFDIAMOND"));
                procedure.setArena(arena);
                Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
                player.closeInventory();
                procedure.send();
            }
        }
        
        @ConstructorProperties({ "partyManage" })
        public SelectManageButton(final OtherPartyEvent partyManage) {
            this.partyManage = partyManage;
        }
    }
}
