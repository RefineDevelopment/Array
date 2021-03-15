package me.drizzy.practice.statistics;

import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.enums.HotbarType;
import me.drizzy.practice.kit.KitInventory;
import me.drizzy.practice.util.chat.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@Getter
@Setter
public class StatisticsData {

    private int elo = 1000;
    private int won = 0;
    private int lost = 0;
    private int kills = 0;
    private int deaths = 0;
    private KitInventory[] loadouts = new KitInventory[4];

    public void incrementWon() {
        this.won++;
    }

    public void incrementLost() {
        this.lost++;
    }

    public void incrementKills() {
        this.kills++;
    }

    public void incrementDeaths() {
        this.deaths++;
    }

    public KitInventory getLoadout(int index) {
        return loadouts[index];
    }

    public void replaceKit(int index, KitInventory loadout) {
        loadouts[index] = loadout;
    }

    public void deleteKit(KitInventory loadout) {
        for (int i = 0; i < 4; i++) {
            if (loadouts[i] != null && loadouts[i].equals(loadout)) {
                loadouts[i] = null;
                break;
            }
        }
    }

    public HashMap<Integer, ItemStack> getKitItems() {
        final HashMap<Integer, ItemStack> toReturn = new HashMap<>();

        List<KitInventory> reversedLoadouts = new ArrayList<>(Arrays.asList(this.loadouts));

        Collections.reverse(reversedLoadouts);

        for (int i = 0; i < this.loadouts.length; i++) {
            for (final KitInventory loadout : reversedLoadouts) {
                if (loadout != null) {
                    final ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
                    final ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(CC.AQUA + loadout.getCustomName() + CC.GRAY + " (Right-Click)");
                    itemMeta.setLore(Arrays.asList(ChatColor.GRAY + "Right click this book", ChatColor.GRAY + "to receive the kit."));
                    itemStack.setItemMeta(itemMeta);

                    if (!toReturn.containsValue(itemStack)) {
                        toReturn.put(i, itemStack);
                    }

                }
            }
        }

        if (toReturn.size() == 0) {
            toReturn.put(0, Hotbar.getItems().get(HotbarType.DEFAULT_KIT));
        }
        else {
            toReturn.put(8, Hotbar.getItems().get(HotbarType.DEFAULT_KIT));
        }

        return toReturn;
    }
}
