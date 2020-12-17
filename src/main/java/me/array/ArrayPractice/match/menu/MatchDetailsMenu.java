package me.array.ArrayPractice.match.menu;

import me.array.ArrayPractice.match.MatchSnapshot;
import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.util.InventoryUtil;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.util.external.PotionUtil;
import me.array.ArrayPractice.util.external.TimeUtil;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import me.array.ArrayPractice.util.external.menu.button.DisplayButton;

import java.util.*;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

@AllArgsConstructor
public class MatchDetailsMenu extends Menu {

	private MatchSnapshot snapshot;

	@Override
	public String getTitle(Player player) {
		return "&e&lInventory of &6" + snapshot.getTeamPlayer().getUsername();
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();
		ItemStack[] fixedContents = InventoryUtil.fixInventoryOrder(snapshot.getContents());

		for (int i = 0; i < fixedContents.length; i++) {
			ItemStack itemStack = fixedContents[i];

			if (itemStack != null && itemStack.getType() != Material.AIR) {
				buttons.put(i, new DisplayButton(itemStack, true));
			}
		}

		for (int i = 0; i < snapshot.getArmor().length; i++) {
			ItemStack itemStack = snapshot.getArmor()[i];

			if (itemStack != null && itemStack.getType() != Material.AIR) {
				buttons.put(39 - i, new DisplayButton(itemStack, true));
			}
		}

		int pos = 45;

		buttons.put(pos++, new HealthButton(snapshot.getHealth()));
		buttons.put(pos++, new HungerButton(snapshot.getHunger()));
		buttons.put(pos++, new EffectsButton(snapshot.getEffects()));

		if (snapshot.shouldDisplayRemainingPotions()) {
			buttons.put(pos++, new PotionsButton(snapshot.getTeamPlayer().getUsername(), snapshot.getRemainingPotions()));
		}

		buttons.put(pos, new StatisticsButton(snapshot.getTeamPlayer()));

		if (this.snapshot.getSwitchTo() != null) {
			buttons.put(53, new SwitchInventoryButton(this.snapshot.getSwitchTo()));
		}

		return buttons;
	}

	@Override
	public void onOpen(Player player) {
		player.sendMessage(CC.YELLOW + "You are viewing " + CC.GOLD + snapshot.getTeamPlayer().getUsername() +
		                   CC.YELLOW + "'s inventory.");
	}

	@AllArgsConstructor
	private class SwitchInventoryButton extends Button {

		private TeamPlayer switchTo;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.LEVER)
					.name("&6Opponent's Inventory")
					.lore("&fSwitch to &e" + switchTo.getUsername() + "&f's inventory")
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			MatchSnapshot cachedInventory;

			try {
				cachedInventory = MatchSnapshot.getByUuid(UUID.fromString(switchTo.getUuid().toString()));
			} catch (Exception e) {
				cachedInventory = MatchSnapshot.getByName(switchTo.getUuid().toString());
			}

			if (cachedInventory == null) {
				player.sendMessage(CC.RED + "Couldn't find an inventory for that ID.");
				return;
			}

			new MatchDetailsMenu(cachedInventory).openMenu(player);
		}

	}

	@AllArgsConstructor
	private class HealthButton extends Button {

		private int health;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.MELON)
					.name("&6Health: &7" + health + "/10&c" + StringEscapeUtils.unescapeJava("\u2764"))
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
					.name("&6Hunger: &7" + hunger + "/20")
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
				builder.lore("&6No potion effects");
			} else {
				List<String> lore = new ArrayList<>();

				effects.forEach(effect -> {
					String name = PotionUtil.getName(effect.getType()) + " " + (effect.getAmplifier() + 1);
					String duration = " (" + TimeUtil.millisToTimer((effect.getDuration() / 20) * 1000) + ")";
					lore.add(CC.WHITE + name + duration);
				});

				builder.lore(lore);
			}

			return builder.build();
		}

	}

	@AllArgsConstructor
	private class PotionsButton extends Button {

		private String name;
		private int potions;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.POTION)
					.durability(16421)
					.amount(potions == 0 ? 1 : potions)
					.name("&6Potions")
					.lore("&f" + name + " had " + potions + " potion" + (potions == 1 ? "" : "s") + " left.")
					.build();
		}

	}

	@AllArgsConstructor
	private class StatisticsButton extends Button {

		private TeamPlayer teamPlayer;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.PAPER)
					.name("&6Statistics")
					.lore(Arrays.asList(
							"&eTotal Hits: &d" + teamPlayer.getHits(),
							"&eLongest Combo: &d" + teamPlayer.getLongestCombo(),
							"&ePotions Thrown: &d" + teamPlayer.getPotionsThrown(),
							"&ePotions Missed: &d" + teamPlayer.getPotionsMissed(),
							"&ePotion Accuracy: &d" + teamPlayer.getPotionAccuracy()
					))
					.build();
		}

	}

}
