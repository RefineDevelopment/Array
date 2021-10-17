package xyz.refinedev.practice.profile.statistics;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.kit.KitInventory;
import xyz.refinedev.practice.profile.hotbar.HotbarType;
import xyz.refinedev.practice.util.chat.CC;

import java.util.*;


//TODO: Recode this
@Getter @Setter
public class ProfileStatistics {

    private int elo = 1000;
    private int won = 0;
    private int lost = 0;

    private KitInventory[] kitInventories = new KitInventory[4];

    public void incrementWon() {
        this.won++;
    }

    public void incrementLost() {
        this.lost++;
    }

    public KitInventory getLoadout(int index) {
        return kitInventories[index];
    }

    public void replaceKit(int index, KitInventory loadout) {
        kitInventories[index] = loadout;
    }

    public void deleteKit(KitInventory loadout) {
        for (int i = 0; i < 4; i++) {
            if (kitInventories[i] != null && kitInventories[i].equals(loadout)) {
                kitInventories[i] = null;
                break;
            }
        }
    }

    public Map<Integer, ItemStack> getKitItems() {
        Map<Integer, ItemStack> toReturn = new HashMap<>();

        List<KitInventory> reversedLoadouts = new ArrayList<>(Arrays.asList(this.kitInventories));

        Collections.reverse(reversedLoadouts);

        for ( int i = 0; i < this.kitInventories.length; i++) {
            for (KitInventory loadout : reversedLoadouts) {
                if (loadout == null) continue;
                ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
                ItemMeta itemMeta = itemStack.getItemMeta();

                itemMeta.setDisplayName(CC.translate(loadout.getCustomName() + CC.GRAY + " (Right-Click)"));
                itemMeta.setLore(Arrays.asList(ChatColor.GRAY + "Right click this book", ChatColor.GRAY + "to receive the kit."));
                itemStack.setItemMeta(itemMeta);

                if (!toReturn.containsValue(itemStack)) {
                    toReturn.put(i, itemStack);
                }
            }
        }

        ItemStack defaultKit = Array.getInstance().getHotbarManager().getHotbarItem(HotbarType.DEFAULT_KIT).getItem();

        if (toReturn.size() == 0) {
            toReturn.put(0, defaultKit);
        } else {
            toReturn.put(8, defaultKit);
        }

        return toReturn;
    }
}
