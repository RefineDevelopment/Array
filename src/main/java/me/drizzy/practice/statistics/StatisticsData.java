package me.drizzy.practice.statistics;

import me.drizzy.practice.hotbar.Hotbar;
import me.drizzy.practice.enums.HotbarType;
<<<<<<< Updated upstream
import me.drizzy.practice.kit.KitLoadout;
=======
import me.drizzy.practice.kit.KitInventory;
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
    private KitLoadout[] loadouts = new KitLoadout[4];
=======
    private KitInventory[] loadouts = new KitInventory[4];
>>>>>>> Stashed changes

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

<<<<<<< Updated upstream
    public KitLoadout getLoadout(int index) {
        return loadouts[index];
    }

    public void replaceKit(int index, KitLoadout loadout) {
        loadouts[index] = loadout;
    }

    public void deleteKit(KitLoadout loadout) {
=======
    public KitInventory getLoadout(int index) {
        return loadouts[index];
    }

    public void replaceKit(int index, KitInventory loadout) {
        loadouts[index] = loadout;
    }

    public void deleteKit(KitInventory loadout) {
>>>>>>> Stashed changes
        for (int i = 0; i < 4; i++) {
            if (loadouts[i] != null && loadouts[i].equals(loadout)) {
                loadouts[i] = null;
                break;
            }
        }
    }

    public HashMap<Integer, ItemStack> getKitItems() {
        final HashMap<Integer, ItemStack> toReturn = new HashMap<>();

<<<<<<< Updated upstream
        List<KitLoadout> reversedLoadouts = new ArrayList<>(Arrays.asList(this.loadouts));
=======
        List<KitInventory> reversedLoadouts = new ArrayList<>(Arrays.asList(this.loadouts));
>>>>>>> Stashed changes

        Collections.reverse(reversedLoadouts);

        for (int i = 0; i < this.loadouts.length; i++) {
<<<<<<< Updated upstream
            for (final KitLoadout loadout : reversedLoadouts) {
=======
            for (final KitInventory loadout : reversedLoadouts) {
>>>>>>> Stashed changes
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
