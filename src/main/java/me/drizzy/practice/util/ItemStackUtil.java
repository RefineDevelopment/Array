package me.drizzy.practice.util;

import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemStackUtil {

	public static ItemStack createItem(Material material, String name) {
		ItemStack is = new ItemStack(material);
		if(material == Material.AIR) return is;
		ItemMeta itemMeta = is.getItemMeta();
		itemMeta.setDisplayName(name);
		is.setItemMeta(itemMeta);
		return is;
	}

	public static ItemStack createItem(Material material, String name, byte dur) {
		ItemStack is = new ItemStack(material);
		if(material == Material.AIR) return is;
		ItemMeta itemMeta = is.getItemMeta();
		itemMeta.setDisplayName(name);
		is.setItemMeta(itemMeta);
		is.setDurability(dur);
		return is;
	}

	public static void removeItems(Inventory inventory, ItemStack item, int amount){
		int size = inventory.getSize();
		for (int slot = 0; slot < size; slot++){
			ItemStack is = inventory.getItem(slot);
			if ((is != null) && (item.getType() == is.getType() && item.getDurability() == is.getDurability())){
				int newAmount = is.getAmount() - amount;
				if (newAmount > 0){
					is.setAmount(newAmount);
				}
				else{
					inventory.setItem(slot, new ItemStack(Material.AIR));
					amount = -newAmount;
					if (amount == 0) {
						break;
					}
				}
			}
		}
	}

	public static ItemStack createPlayerSkull(String name){
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta im = (SkullMeta) item.getItemMeta();
		im.setOwner(ChatColor.stripColor(name));
		im.setDisplayName(name);
		item.setItemMeta(im);
		return item;
	}

	public static String itemToString(ItemStack is) {
		if(is == null) is = new ItemStack(Material.AIR);
		String enchantments = "";
		String comma = "";
		for(Entry<Enchantment, Integer> e : is.getEnchantments().entrySet()) {
			enchantments += comma;
			enchantments += e.getKey().getName() + "-" + e.getValue();
			comma = ";";
		}
		if(enchantments != null && !enchantments.equals("")) {
			return is.getType() + "/" + is.getDurability() + "/" + is.getAmount() + "/" + enchantments;
		}
		return is.getType() + "/" + is.getDurability() + "/" + is.getAmount();
	}

	public static ItemStack fromString(String string) {
		try{
			String[] s = string.split("/");
			if(string == null || !(s.length >= 3)) {
				return new ItemStack(Material.AIR);
			}
			ItemStack is = new ItemStack(Material.getMaterial(s[0]));
			is.setDurability(Short.parseShort(s[1]));
			is.setAmount(Integer.parseInt(s[2]));
			if(s.length > 3) {
				for(String enchantment : s[3].split(";")) {
					String[] ench = enchantment.split("-");
					is.addUnsafeEnchantment(Enchantment.getByName(ench[0]), Integer.parseInt(ench[1]));
				}
			}
			return is;
		}catch(Exception e) {}
		return new ItemStack(Material.AIR);
	}
	
	public static ItemStack[] getContents(Player p) {
		for(int i = 0; i < p.getInventory().getSize(); i++) {
			if(p.getInventory().getItem(i) == null) {
				p.getInventory().setItem(i, new ItemStack(Material.AIR));
			}
		}
		return p.getInventory().getContents();
	}

}
