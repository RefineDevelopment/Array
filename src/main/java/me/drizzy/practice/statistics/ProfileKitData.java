package me.drizzy.practice.statistics;

import me.drizzy.practice.profile.hotbar.Hotbar;
import me.drizzy.practice.profile.hotbar.HotbarItem;
import me.drizzy.practice.kit.KitLoadout;
import me.drizzy.practice.util.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ProfileKitData {

    @Getter
    @Setter
    private int elo = 1000;
    @Getter
    @Setter
    private int won = 0;
    @Getter
    @Setter
    private int lost = 0;
    @Getter
    @Setter
    private KitLoadout[] loadouts = new KitLoadout[4];

    public void incrementWon() {
        this.won++;
    }

    public void incrementLost() {
        this.lost++;
    }

    public KitLoadout getLoadout(int index) {
        return loadouts[index];
    }

    public void replaceKit(int index, KitLoadout loadout) {
        loadouts[index] = loadout;
    }

    public void deleteKit(KitLoadout loadout) {
        for (int i = 0; i < 4; i++) {
            if (loadouts[i] != null && loadouts[i].equals(loadout)) {
                loadouts[i] = null;
                break;
            }
        }
    }

    public HashMap<Integer, ItemStack> getKitItems() {
        final HashMap<Integer, ItemStack> toReturn = new HashMap<>();

        List<KitLoadout> reversedLoadouts = new ArrayList<>(Arrays.asList(this.loadouts));

        Collections.reverse(reversedLoadouts);

        for (int i = 0; i < this.loadouts.length; i++) {
            for (final KitLoadout loadout : reversedLoadouts) {
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
            toReturn.put(0, Hotbar.getItems().get(HotbarItem.DEFAULT_KIT));
        }
        else {
            toReturn.put(8, Hotbar.getItems().get(HotbarItem.DEFAULT_KIT));
        }

        return toReturn;
        /*List<ItemStack> toReturn = new ArrayList<>();

        for (KitLoadout loadout : loadouts) {
            if (loadout != null) {

                ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
                ItemMeta itemMeta = itemStack.getItemMeta();

                itemMeta.setDisplayName(ChatColor.RED + "Kit: " + ChatColor.AQUA + loadout.getCustomName());
                itemMeta.setLore(Arrays.asList(
                        ChatColor.GRAY + "Right-click with this book in your",
                        ChatColor.GRAY + "hand to receive this kit."
                ));
                itemStack.setItemMeta(itemMeta);

                toReturn.add(itemStack);
            }
        }

        return toReturn;*/
    }

    public List<ItemStack> getHCFKitItems() {
        List<ItemStack> toReturn = new ArrayList<>();
        toReturn.add(Hotbar.getItems().get(HotbarItem.DIAMOND_KIT));
        toReturn.add(Hotbar.getItems().get(HotbarItem.BARD_KIT));
        toReturn.add(Hotbar.getItems().get(HotbarItem.ARCHER_KIT));
        toReturn.add(Hotbar.getItems().get(HotbarItem.ROGUE_KIT));


        return toReturn;
    }

}
