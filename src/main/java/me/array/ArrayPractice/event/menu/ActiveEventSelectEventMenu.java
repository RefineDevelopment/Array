package me.array.ArrayPractice.event.menu;

import lombok.AllArgsConstructor;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.event.EventType;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.event.impl.brackets.command.BracketsJoinCommand;
import me.array.ArrayPractice.event.impl.lms.command.FFAJoinCommand;
import me.array.ArrayPractice.event.impl.parkour.command.ParkourJoinCommand;
import me.array.ArrayPractice.event.impl.spleef.command.SpleefJoinCommand;
import me.array.ArrayPractice.event.impl.sumo.command.SumoJoinCommand;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActiveEventSelectEventMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "&7Select an active tournament";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;
		for ( EventType eventType : EventType.values()) {
			if (eventType.getTitle().equals("FFA")) {
				if (Array.get().getFfaManager().getActiveFFA() != null && Array.get().getFfaManager().getActiveFFA().isWaiting()) {
					buttons.put(i, new SelectEventButton(EventType.FFA));
					i++;
				}
			}
			if (eventType.getTitle().equals("Brackets")) {
				if (Array.get().getBracketsManager().getActiveBrackets() != null && Array.get().getBracketsManager().getActiveBrackets().isWaiting()) {
					buttons.put(i, new SelectEventButton(EventType.BRACKETS));
					i++;
				}
			}
			if (eventType.getTitle().equals("Sumo")) {
				if (Array.get().getSumoManager().getActiveSumo() != null && Array.get().getSumoManager().getActiveSumo().isWaiting()) {
					buttons.put(i, new SelectEventButton(EventType.SUMO));
				i++;
				}
			}
			if (eventType.getTitle().equals("Parkour")) {
				if (Array.get().getParkourManager().getActiveParkour() != null && Array.get().getParkourManager().getActiveParkour().isWaiting()) {
					buttons.put(i, new SelectEventButton(EventType.PARKOUR));
					i++;
				}
			}
			if (eventType.getTitle().equals("Spleef")) {
				if (Array.get().getSpleefManager().getActiveSpleef() != null && Array.get().getSpleefManager().getActiveSpleef().isWaiting()) {
					buttons.put(i, new SelectEventButton(EventType.SPLEEF));
					i++;
				}
			}
		}
		return buttons;
	}

	@AllArgsConstructor
	private static class SelectEventButton extends Button {

		private final EventType eventType;

		@Override
		public ItemStack getButtonItem(Player player) {
			List<String> lore = new ArrayList<>();

			switch (eventType.getTitle()) {
				case "Brackets":
					lore=Array.get().getBracketsManager().getActiveBrackets().getLore();
					break;
				case "Sumo":
					lore=Array.get().getSumoManager().getActiveSumo().getLore();
					break;
				case "FFA":
					lore=Array.get().getFfaManager().getActiveFFA().getLore();
					break;
				case "Parkour":
					lore=Array.get().getParkourManager().getActiveParkour().getLore();
					break;
				case "Spleef":
					lore=Array.get().getSpleefManager().getActiveSpleef().getLore();
					break;
			}

			lore.add(CC.MENU_BAR);
			lore.add("&7(Left-Click to join)");
			lore.add(CC.MENU_BAR);


			return new ItemBuilder(eventType.getMaterial())
					.name("&b" + eventType.getTitle())
					.lore(lore)
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
			player.closeInventory();
			switch (eventType.getTitle()) {
				case "Brackets":
					BracketsJoinCommand.execute(player);
					break;
				case "Sumo":
					SumoJoinCommand.execute(player);
					break;
				case "FFA":
					FFAJoinCommand.execute(player);
					break;
				case "Parkour":
					ParkourJoinCommand.execute(player);
					break;
				case "Spleef":
					SpleefJoinCommand.execute(player);
					break;
			}
		}

	}

}
