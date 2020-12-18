package me.array.ArrayPractice.event.menu;

import lombok.AllArgsConstructor;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.event.impl.brackets.Brackets;
import me.array.ArrayPractice.event.impl.lms.FFA;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class EventSelectKitMenu extends Menu {

	private String event;

	@Override
	public String getTitle(Player player) {
		return "&6Select a kit to host " + event;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();
		for ( Kit kit : Kit.getKits()) {
			if (kit.isEnabled() && !kit.getGameRules().isNoitems() && !kit.getGameRules().isLavakill() && !kit.getGameRules().isWaterkill() && !kit.getGameRules().isSpleef() && !kit.getGameRules().isBuild() && !kit.getGameRules().isSumo()) {
				buttons.put(buttons.size(), new SelectKitButton(event, kit));
			}
		}
		return buttons;
	}

	@AllArgsConstructor
	private class SelectKitButton extends Button {

		private String event;
		private Kit kit;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(kit.getDisplayIcon())
					.name("&3" + kit.getName())
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			if (event.equals("Brackets")) {
				if (Array.get().getBracketsManager().getActiveBrackets() != null) {
					player.sendMessage(CC.RED + "There is already an active Brackets Event.");
					return;
				}

				if (!Array.get().getBracketsManager().getCooldown().hasExpired()) {
					player.sendMessage(CC.RED + "There is an active cooldown for the Brackets Event.");
					return;
				}

				Array.get().getBracketsManager().setActiveBrackets(new Brackets(player, kit));

				for (Player other : Array.get().getServer().getOnlinePlayers()) {
					Profile profile = Profile.getByUuid(other.getUniqueId());

					if (profile.isInLobby()) {
						if (!profile.getKitEditor().isActive()) {
							profile.refreshHotbar();
						}
					}
				}
			} else {
				if (Array.get().getFfaManager().getActiveFFA() != null) {
					player.sendMessage(CC.RED + "There is already an active Brackets Event.");
					return;
				}

				if (!Array.get().getFfaManager().getCooldown().hasExpired()) {
					player.sendMessage(CC.RED + "There is an active cooldown for the Brackets Event.");
					return;
				}

				Array.get().getFfaManager().setActiveFFA(new FFA(player, kit));

				for (Player other : Array.get().getServer().getOnlinePlayers()) {
					Profile profile = Profile.getByUuid(other.getUniqueId());

					if (profile.isInLobby()) {
						if (!profile.getKitEditor().isActive()) {
							profile.refreshHotbar();
						}
					}
				}
			}
			Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
			player.closeInventory();
		}

	}

}
