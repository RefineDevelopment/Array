

package me.array.ArrayPractice.profile.meta;

import me.array.ArrayPractice.kit.KitLoadout;
import me.array.ArrayPractice.profile.hotbar.Hotbar;
import me.array.ArrayPractice.profile.hotbar.HotbarItem;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class ProfileKitData
{
    private int elo;
    private int rankedWon;
    private int rankedLost;
    private int unrankedWon;
    private int unrankedLost;
    private KitLoadout[] loadouts;
    
    public ProfileKitData() {
        this.elo = 1000;
        this.rankedWon = 0;
        this.rankedLost = 0;
        this.unrankedWon = 0;
        this.unrankedLost = 0;
        this.loadouts = new KitLoadout[4];
    }
    
    public void incrementRankedWon() {
        ++this.rankedWon;
    }
    
    public void incrementRankedLost() {
        ++this.rankedLost;
    }
    
    public void incrementUnrankedWins() {
        ++this.unrankedWon;
    }
    
    public void incrementUnrankedLost() {
        ++this.unrankedLost;
    }
    
    public KitLoadout getLoadout(final int index) {
        return this.loadouts[index];
    }
    
    public void replaceKit(final int index, final KitLoadout loadout) {
        this.loadouts[index] = loadout;
    }
    
    public void deleteKit(final KitLoadout loadout) {
        for (int i = 0; i < 4; ++i) {
            if (this.loadouts[i] != null && this.loadouts[i].equals(loadout)) {
                this.loadouts[i] = null;
                break;
            }
        }
    }
    
    public List<ItemStack> getKitItems() {
        final List<ItemStack> toReturn = new ArrayList<ItemStack>();
        toReturn.add(Hotbar.getItems().get(HotbarItem.DEFAULT_KIT));
        for (final KitLoadout loadout : this.loadouts) {
            if (loadout != null) {
                final ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
                final ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(ChatColor.AQUA + loadout.getCustomName());
                itemMeta.setLore(Arrays.asList(ChatColor.GRAY + "Right-click with this book in your", ChatColor.GRAY + "hand to receive this kit."));
                itemStack.setItemMeta(itemMeta);
                toReturn.add(itemStack);
            }
        }
        return toReturn;
    }
    
    public List<ItemStack> getHCFKitItems() {
        final List<ItemStack> toReturn = new ArrayList<ItemStack>();
        toReturn.add(Hotbar.getItems().get(HotbarItem.DIAMOND_KIT));
        toReturn.add(Hotbar.getItems().get(HotbarItem.BARD_KIT));
        toReturn.add(Hotbar.getItems().get(HotbarItem.ARCHER_KIT));
        toReturn.add(Hotbar.getItems().get(HotbarItem.ROGUE_KIT));
        return toReturn;
    }
    
    public int getElo() {
        return this.elo;
    }
    
    public void setElo(final int elo) {
        this.elo = elo;
    }
    
    public int getRankedWon() {
        return this.rankedWon;
    }
    
    public void setRankedWon(final int rankedWon) {
        this.rankedWon = rankedWon;
    }
    
    public int getRankedLost() {
        return this.rankedLost;
    }
    
    public void setRankedLost(final int rankedLost) {
        this.rankedLost = rankedLost;
    }
    
    public int getUnrankedWon() {
        return this.unrankedWon;
    }
    
    public void setUnrankedWon(final int unrankedWon) {
        this.unrankedWon = unrankedWon;
    }
    
    public int getUnrankedLost() {
        return this.unrankedLost;
    }
    
    public void setUnrankedLost(final int unrankedLost) {
        this.unrankedLost = unrankedLost;
    }
    
    public KitLoadout[] getLoadouts() {
        return this.loadouts;
    }
    
    public void setLoadouts(final KitLoadout[] loadouts) {
        this.loadouts = loadouts;
    }
}
