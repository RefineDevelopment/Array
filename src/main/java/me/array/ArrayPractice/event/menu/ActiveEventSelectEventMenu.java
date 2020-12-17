package me.array.ArrayPractice.event.menu;

import lombok.AllArgsConstructor;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.event.EventType;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.event.impl.brackets.command.BracketsJoinCommand;
import me.array.ArrayPractice.event.impl.ffa.command.FFAJoinCommand;
import me.array.ArrayPractice.event.impl.infected.command.InfectedJoinCommand;
import me.array.ArrayPractice.event.impl.juggernaut.command.JuggernautJoinCommand;
import me.array.ArrayPractice.event.impl.parkour.command.ParkourJoinCommand;
import me.array.ArrayPractice.event.impl.skywars.command.SkyWarsJoinCommand;
import me.array.ArrayPractice.event.impl.spleef.command.SpleefJoinCommand;
import me.array.ArrayPractice.event.impl.sumo.command.SumoJoinCommand;
import me.array.ArrayPractice.event.impl.wipeout.command.WipeoutJoinCommand;
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
		return "&eSelect an active event";
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
			if (eventType.getTitle().equals("Juggernaut")) {
				if (Array.get().getJuggernautManager().getActiveJuggernaut() != null && Array.get().getJuggernautManager().getActiveJuggernaut().isWaiting()) {
					buttons.put(i, new SelectEventButton(EventType.JUGGERNAUT));
					i++;
				}
			}
			if (eventType.getTitle().equals("Parkour")) {
				if (Array.get().getParkourManager().getActiveParkour() != null && Array.get().getParkourManager().getActiveParkour().isWaiting()) {
					buttons.put(i, new SelectEventButton(EventType.PARKOUR));
					i++;
				}
			}
			if (eventType.getTitle().equals("Wipeout")) {
				if (Array.get().getWipeoutManager().getActiveWipeout() != null && Array.get().getWipeoutManager().getActiveWipeout().isWaiting()) {
					buttons.put(i, new SelectEventButton(EventType.WIPEOUT));
					i++;
				}
			}
//			if (eventType.getTitle().equals("SkyWars")) {
//				if (Array.get().getSkyWarsManager().getActiveSkyWars() != null && Array.get().getSkyWarsManager().getActiveSkyWars().isWaiting()) {
//					buttons.put(i, new SelectEventButton(EventType.SKYWARS));
//					i++;
//				}
//			}
			if (eventType.getTitle().equals("Spleef")) {
				if (Array.get().getSpleefManager().getActiveSpleef() != null && Array.get().getSpleefManager().getActiveSpleef().isWaiting()) {
					buttons.put(i, new SelectEventButton(EventType.SPLEEF));
					i++;
				}
			}
			if (eventType.getTitle().equals("Infected")) {
				if (Array.get().getInfectedManager().getActiveInfected() != null && Array.get().getInfectedManager().getActiveInfected().isWaiting()) {
					buttons.put(i, new SelectEventButton(EventType.INFECTED));
					i++;
				}
			}
		}
		return buttons;
	}

	@AllArgsConstructor
	private class SelectEventButton extends Button {

		private EventType eventType;

		@Override
		public ItemStack getButtonItem(Player player) {
			List<String> lore = new ArrayList<>();

			if (eventType.getTitle().equals("Brackets")) {
				lore = Array.get().getBracketsManager().getActiveBrackets().getLore();
			} else if (eventType.getTitle().equals("Sumo")) {
				lore = Array.get().getSumoManager().getActiveSumo().getLore();
			} else if (eventType.getTitle().equals("FFA")) {
				lore = Array.get().getFfaManager().getActiveFFA().getLore();
			} else if (eventType.getTitle().equals("Juggernaut")) {
				lore = Array.get().getJuggernautManager().getActiveJuggernaut().getLore();
			} else if (eventType.getTitle().equals("Parkour")) {
				lore = Array.get().getParkourManager().getActiveParkour().getLore();
			} else if (eventType.getTitle().equals("Wipeout")) {
				lore = Array.get().getWipeoutManager().getActiveWipeout().getLore();
			} else if (eventType.getTitle().equals("SkyWars")) {
				lore = Array.get().getSkyWarsManager().getActiveSkyWars().getLore();
			} else if (eventType.getTitle().equals("Spleef")) {
				lore = Array.get().getSpleefManager().getActiveSpleef().getLore();
			} else if (eventType.getTitle().equals("Infected")) {
				lore = Array.get().getInfectedManager().getActiveInfected().getLore();
			}

			lore.add("&7(Left-Click to join)");
			lore.add(CC.MENU_BAR);


			return new ItemBuilder(eventType.getMaterial())
					.name("&6" + eventType.getTitle() + "&f Event")
					.lore(lore)
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
			player.closeInventory();
			if (eventType.getTitle().equals("Brackets")) {
				BracketsJoinCommand.execute(player);
			} else if (eventType.getTitle().equals("Sumo")) {
				SumoJoinCommand.execute(player);
			} else if (eventType.getTitle().equals("FFA")) {
				FFAJoinCommand.execute(player);
			} else if (eventType.getTitle().equals("Juggernaut")) {
				JuggernautJoinCommand.execute(player);
			} else if (eventType.getTitle().equals("Parkour")) {
				ParkourJoinCommand.execute(player);
			} else if (eventType.getTitle().equals("Wipeout")) {
				WipeoutJoinCommand.execute(player);
			} else if (eventType.getTitle().equals("SkyWars")) {
				SkyWarsJoinCommand.execute(player);
			} else if (eventType.getTitle().equals("Spleef")) {
				SpleefJoinCommand.execute(player);
			} else if (eventType.getTitle().equals("Infected")) {
				InfectedJoinCommand.execute(player);
			}
		}

	}

}
