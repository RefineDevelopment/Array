package me.array.ArrayPractice.event.menu;

import lombok.AllArgsConstructor;
import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.event.EventType;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import me.array.ArrayPractice.event.impl.brackets.command.BracketsHostCommand;
import me.array.ArrayPractice.event.impl.brackets.command.BracketsJoinCommand;
import me.array.ArrayPractice.event.impl.lms.command.FFAHostCommand;
import me.array.ArrayPractice.event.impl.lms.command.FFAJoinCommand;
import me.array.ArrayPractice.event.impl.parkour.command.ParkourHostCommand;
import me.array.ArrayPractice.event.impl.parkour.command.ParkourJoinCommand;
import me.array.ArrayPractice.event.impl.spleef.command.SpleefHostCommand;
import me.array.ArrayPractice.event.impl.spleef.command.SpleefJoinCommand;
import me.array.ArrayPractice.event.impl.sumo.command.SumoHostCommand;
import me.array.ArrayPractice.event.impl.sumo.command.SumoJoinCommand;
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
		buttons.put(0, new SelectEventButton(EventType.FFA));
		buttons.put(2, new SelectEventButton(EventType.BRACKETS));
		buttons.put(4, new SelectEventButton(EventType.SUMO));
		buttons.put(6, new SelectEventButton(EventType.PARKOUR));
		buttons.put(8, new SelectEventButton(EventType.SPLEEF));
		return buttons;
	}

	@AllArgsConstructor
	private static class SelectEventButton extends Button {

		private final EventType eventType;

		@Override
		public ItemStack getButtonItem(Player player) {
			List<String> lore=new ArrayList<>();

			switch (eventType.getTitle()) {
				case "Brackets":
					if (Array.get().getBracketsManager().getActiveBrackets() != null)
						lore=Array.get().getBracketsManager().getActiveBrackets().getLore();
					else lore=getDefaultLore("Brackets");
					break;
				case "Sumo":
					if (Array.get().getSumoManager().getActiveSumo() != null)
						lore=Array.get().getSumoManager().getActiveSumo().getLore();
					else lore=getDefaultLore("Sumo");
					break;
				case "FFA":
					if (Array.get().getFfaManager().getActiveFFA() != null)
						lore=Array.get().getFfaManager().getActiveFFA().getLore();
					else lore=getDefaultLore("FFA");
					break;
				case "Parkour":
					if (Array.get().getParkourManager().getActiveParkour() != null)
						lore=Array.get().getParkourManager().getActiveParkour().getLore();
					else lore=getDefaultLore("Parkour");
					break;
				case "Spleef":
					if (Array.get().getSpleefManager().getActiveSpleef() != null)
						lore=Array.get().getSpleefManager().getActiveSpleef().getLore();
					else lore=getDefaultLore("Spleef");
					break;
			}

			lore.add(CC.MENU_BAR);
			lore.add("&7(Left-Click to join)");
			lore.add("&7(Right-Click to host)");
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
			if (clickType.isLeftClick()) {
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
					default:
						switch (eventType.getTitle()) {
							case "Brackets":
								if (player.hasPermission("practice.brackets.host")) BracketsHostCommand.execute(player);
								else player.sendMessage(ChatColor.RED + "No permission.");
								break;
							case "Sumo":
								if (player.hasPermission("practice.sumo.host")) SumoHostCommand.execute(player);
								else player.sendMessage(ChatColor.RED + "No permission.");
								break;
							case "FFA":
								if (player.hasPermission("practice.ffa.host")) FFAHostCommand.execute(player);
								else player.sendMessage(ChatColor.RED + "No permission.");
								break;
							case "Parkour":
								if (player.hasPermission("practice.parkour.host")) ParkourHostCommand.execute(player);
								else player.sendMessage(ChatColor.RED + "No permission.");
								break;
							case "Spleef":
								if (player.hasPermission("practice.spleef.host")) SpleefHostCommand.execute(player);
								else player.sendMessage(ChatColor.RED + "No permission.");
								break;
						}
						break;
				}
			}

		}

		private List<String> getDefaultLore(String name) {
			List<String> toReturn=new ArrayList<>();
			toReturn.add("&eState: &fNeeds to be hosted");
			switch (name) {
				case "FFA":
					toReturn.add("&eRank to host: &fDonator");
					break;
				case "Brackets":
					toReturn.add("&eRank to host: &fDonator+");
					break;
				case "Sumo":
					toReturn.add("&eRank to host: &fElite");
					break;
				case "Parkour":
					toReturn.add("&eRank to host: &fMaster");
					break;
				case "Spleef":
					toReturn.add("&eRank to host: &fGod");
					break;
			}
			return toReturn;
		}
	}
}
