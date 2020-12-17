package me.array.ArrayPractice.event.menu;

import lombok.AllArgsConstructor;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.event.EventType;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.event.impl.brackets.command.BracketsHostCommand;
import me.array.ArrayPractice.event.impl.brackets.command.BracketsJoinCommand;
import me.array.ArrayPractice.event.impl.ffa.command.FFAHostCommand;
import me.array.ArrayPractice.event.impl.ffa.command.FFAJoinCommand;
import me.array.ArrayPractice.event.impl.infected.command.InfectedHostCommand;
import me.array.ArrayPractice.event.impl.infected.command.InfectedJoinCommand;
import me.array.ArrayPractice.event.impl.juggernaut.command.JuggernautHostCommand;
import me.array.ArrayPractice.event.impl.juggernaut.command.JuggernautJoinCommand;
import me.array.ArrayPractice.event.impl.parkour.command.ParkourHostCommand;
import me.array.ArrayPractice.event.impl.parkour.command.ParkourJoinCommand;
import me.array.ArrayPractice.event.impl.skywars.command.SkyWarsHostCommand;
import me.array.ArrayPractice.event.impl.skywars.command.SkyWarsJoinCommand;
import me.array.ArrayPractice.event.impl.spleef.command.SpleefHostCommand;
import me.array.ArrayPractice.event.impl.spleef.command.SpleefJoinCommand;
import me.array.ArrayPractice.event.impl.sumo.command.SumoHostCommand;
import me.array.ArrayPractice.event.impl.sumo.command.SumoJoinCommand;
import me.array.ArrayPractice.event.impl.wipeout.command.WipeoutHostCommand;
import me.array.ArrayPractice.event.impl.wipeout.command.WipeoutJoinCommand;
import me.array.ArrayPractice.util.external.menu.Button;
import me.array.ArrayPractice.util.external.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EventSelectEventMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "&eSelect an event";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();
		buttons.put(14, new SelectEventButton(EventType.FFA));
		buttons.put(2, new SelectEventButton(EventType.BRACKETS));
		buttons.put(3, new SelectEventButton(EventType.SUMO));
		buttons.put(4, new SelectEventButton(EventType.JUGGERNAUT));
		buttons.put(5, new SelectEventButton(EventType.PARKOUR));
		buttons.put(6, new SelectEventButton(EventType.WIPEOUT));
		buttons.put(22, new SelectEventButton(EventType.SKYWARS));
		buttons.put(12, new SelectEventButton(EventType.SPLEEF));
		buttons.put(13, new SelectEventButton(EventType.INFECTED));
		return buttons;
	}

	@AllArgsConstructor
	private class SelectEventButton extends Button {

		private EventType eventType;

		@Override
		public ItemStack getButtonItem(Player player) {
			List<String> lore = new ArrayList<>();

			if (eventType.getTitle().equals("Brackets")) {
				if (Array.get().getBracketsManager().getActiveBrackets() != null) lore = Array.get().getBracketsManager().getActiveBrackets().getLore();
				else lore = getDefaultLore("Brackets");
			} else if (eventType.getTitle().equals("Sumo")) {
				if (Array.get().getSumoManager().getActiveSumo() != null) lore = Array.get().getSumoManager().getActiveSumo().getLore();
				else lore = getDefaultLore("Sumo");
			} else if (eventType.getTitle().equals("FFA")) {
				if (Array.get().getFfaManager().getActiveFFA() != null) lore = Array.get().getFfaManager().getActiveFFA().getLore();
				else lore = getDefaultLore("FFA");
			} else if (eventType.getTitle().equals("Juggernaut")) {
				if (Array.get().getJuggernautManager().getActiveJuggernaut() != null) lore = Array.get().getJuggernautManager().getActiveJuggernaut().getLore();
				else lore = getDefaultLore("Juggernaut");
			} else if (eventType.getTitle().equals("Parkour")) {
				if (Array.get().getParkourManager().getActiveParkour() != null) lore = Array.get().getParkourManager().getActiveParkour().getLore();
				else lore = getDefaultLore("Parkour");
			} else if (eventType.getTitle().equals("Wipeout")) {
				if (Array.get().getWipeoutManager().getActiveWipeout() != null) lore = Array.get().getWipeoutManager().getActiveWipeout().getLore();
				else lore = getDefaultLore("Wipeout");
			} else if (eventType.getTitle().equals("SkyWars")) {
				if (Array.get().getSkyWarsManager().getActiveSkyWars() != null) lore = Array.get().getSkyWarsManager().getActiveSkyWars().getLore();
				else lore = getDefaultLore("SkyWars");
			} else if (eventType.getTitle().equals("Spleef")) {
				if (Array.get().getSpleefManager().getActiveSpleef() != null) lore = Array.get().getSpleefManager().getActiveSpleef().getLore();
				else lore = getDefaultLore("Spleef");
			} else if (eventType.getTitle().equals("Infected")) {
				if (Array.get().getInfectedManager().getActiveInfected() != null) lore = Array.get().getInfectedManager().getActiveInfected().getLore();
				else lore = getDefaultLore("Infected");
			}

			lore.add("&7(Left-Click to join)");
			lore.add("&7(Right-Click to host)");
			lore.add(CC.MENU_BAR);


			return new ItemBuilder(eventType.getMaterial())
					.name("&b" + eventType.getTitle() + "&7 Event")
					.lore(lore)
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
			player.closeInventory();
			if (clickType.isLeftClick()) {
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
			} else {
				if (eventType.getTitle().equals("Brackets")) {
					if (player.hasPermission("practice.brackets.host")) BracketsHostCommand.execute(player);
					else player.sendMessage(ChatColor.RED + "No permission.");
				} else if (eventType.getTitle().equals("Sumo")) {
					if (player.hasPermission("practice.sumo.host"))SumoHostCommand.execute(player);
					else player.sendMessage(ChatColor.RED + "No permission.");
				} else if (eventType.getTitle().equals("FFA")) {
					if (player.hasPermission("practice.ffa.host"))FFAHostCommand.execute(player);
					else player.sendMessage(ChatColor.RED + "No permission.");
				} else if (eventType.getTitle().equals("Juggernaut")) {
					if (player.hasPermission("practice.juggernaut.host"))JuggernautHostCommand.execute(player);
					else player.sendMessage(ChatColor.RED + "No permission.");
				} else if (eventType.getTitle().equals("Parkour")) {
					if (player.hasPermission("practice.parkour.host"))ParkourHostCommand.execute(player);
					else player.sendMessage(ChatColor.RED + "No permission.");
				} else if (eventType.getTitle().equals("Wipeout")) {
					if (player.hasPermission("practice.wipeout.host"))WipeoutHostCommand.execute(player);
					else player.sendMessage(ChatColor.RED + "No permission.");
				} else if (eventType.getTitle().equals("SkyWars")) {
					if (player.hasPermission("practice.skywars.host"))SkyWarsHostCommand.execute(player);
					else player.sendMessage(ChatColor.RED + "No permission.");
				} else if (eventType.getTitle().equals("Spleef")) {
					if (player.hasPermission("practice.spleef.host"))SpleefHostCommand.execute(player);
					else player.sendMessage(ChatColor.RED + "No permission.");
				} else if (eventType.getTitle().equals("Infected")) {
					if (player.hasPermission("practice.infected.host")) InfectedHostCommand.execute(player);
					else player.sendMessage(ChatColor.RED + "No permission.");
				}
			}
		}

	}

	private List<String> getDefaultLore(String name) {
		List<String> toReturn = new ArrayList<>();
		toReturn.add("&eState: &fNeeds to be hosted");
		if (name.equals("FFA")) {
			toReturn.add("&eRank to host: &fDonator");
		} else if (name.equals("Brackets")) {
			toReturn.add("&eRank to host: &fDonator+");
		} else if (name.equals("Sumo")) {
			toReturn.add("&eRank to host: &fElite");
		} else if (name.equals("Juggernaut")) {
			toReturn.add("&eRank to host: &fUltra");
		} else if (name.equals("Parkour")) {
			toReturn.add("&eRank to host: &fMaster");
		} else if (name.equals("Wipeout")) {
			toReturn.add("&eRank to host: &fMaster");
		} else if (name.equals("SkyWars")) {
			toReturn.add("&eRank to host: &fGod");
		} else if (name.equals("Spleef")) {
			toReturn.add("&eRank to host: &fGod");
		}
		return toReturn;
	}

}
