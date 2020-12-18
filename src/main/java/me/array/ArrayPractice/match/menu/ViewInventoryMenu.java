package me.array.ArrayPractice.match.menu;

import me.array.ArrayPractice.util.InventoryUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.PotionUtil;
import me.array.ArrayPractice.util.external.TimeUtil;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import me.array.ArrayPractice.util.external.menu.button.DisplayButton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class ViewInventoryMenu extends Menu {

	private final Player target;

	public ViewInventoryMenu(Player target) {
		this.target = target;
	}

	@Override
	public String getTitle(Player player) {
		return CC.AQUA + target.getName() + "'s Inventory";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		if (player == null) {
			return buttons;
		}

		ItemStack[] fixedContents = InventoryUtil.fixInventoryOrder(this.target.getInventory().getContents());

		for (int i = 0; i < fixedContents.length; ++i) {
			ItemStack itemStack = fixedContents[i];

			if (itemStack != null) {
				if (itemStack.getType() != Material.AIR) {
					buttons.put(i, new DisplayButton(itemStack, true));
				}
			}
		}

		for (int i = 0; i < target.getInventory().getArmorContents().length; ++i) {
			ItemStack itemStack = target.getInventory().getArmorContents()[i];

			if (itemStack != null && itemStack.getType() != Material.AIR) {
				buttons.put(39 - i, new DisplayButton(itemStack, true));
			}
		}

		int pos = 45;
		buttons.put(pos++, new HealthButton(target.getHealth() == 0.0 ? 0 : (int) Math.round(target.getHealth() / 2.0)));
		buttons.put(pos++, new HungerButton(target.getFoodLevel()));
		buttons.put(pos, new EffectsButton(target.getActivePotionEffects()));
		return buttons;
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}

	@AllArgsConstructor
	private class HealthButton extends Button {

		private int health;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.MELON)
					.name("&bHealth: &7" + health + "/10 " + StringEscapeUtils.unescapeJava("\u2764"))
					.amount(health == 0 ? 1 : health)
					.build();
		}

	}

	@AllArgsConstructor
	private class HungerButton extends Button {

		private int hunger;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.COOKED_BEEF)
					.name("&bHunger: &7e" + hunger + "/20")
					.amount(hunger == 0 ? 1 : hunger)
					.build();
		}

	}

	@AllArgsConstructor
	private class EffectsButton extends Button {

		private Collection<PotionEffect> effects;

		@Override
		public ItemStack getButtonItem(Player player) {
			ItemBuilder builder = new ItemBuilder(Material.POTION).name("&bPotion Effects");

			if (effects.isEmpty()) {
				builder.lore(CC.GRAY + "No effects");
			} else {
				List<String> lore = new ArrayList<>();

				effects.forEach(effect -> {
					String name = PotionUtil.getName(effect.getType()) + " " + (effect.getAmplifier() + 1);
					String duration = " (" + TimeUtil.millisToTimer(effect.getDuration() / 20 * 1000) + ")";
					lore.add(CC.AQUA + name + CC.WHITE + duration);
				});

				builder.lore(lore);
			}

			return builder.build();
		}

	}

}
