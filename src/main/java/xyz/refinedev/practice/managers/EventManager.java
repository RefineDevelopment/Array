package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventHelperUtil;
import xyz.refinedev.practice.event.EventTeamSize;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.impl.brackets.solo.BracketsSolo;
import xyz.refinedev.practice.event.impl.brackets.team.BracketsTeam;
import xyz.refinedev.practice.event.impl.lms.LMS;
import xyz.refinedev.practice.event.impl.parkour.Parkour;
import xyz.refinedev.practice.event.impl.spleef.Spleef;
import xyz.refinedev.practice.event.impl.sumo.solo.SumoSolo;
import xyz.refinedev.practice.event.impl.sumo.team.SumoTeam;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.other.Cooldown;

import java.util.*;

@Getter @Setter
@RequiredArgsConstructor
public class EventManager {

	private final Map<UUID, Event> events = new HashMap<>();

	private final Array plugin;
	private final BasicConfigurationFile config;

	private Event activeEvent;
	private EventHelperUtil helper;
	private Cooldown cooldown = new Cooldown(0);

	/**
	 * Load event utilities and setup basic tasks
	 */
	public void init() {
		this.helper = new EventHelperUtil(config);
		this.helper.loadLocations();
	}

	/**
	 * Shutdown and clear each and every thing related to events
	 */
	public void shutdown() {
		this.events.values().forEach(Event::handleEnd);

		this.events.clear();

		this.helper.saveLocations();
	}

	/**
	 * Get a player's event by his uniqueId
	 *
	 * @param uuid {@link UUID} the event's id
	 * @return {@link Event} the player's event
	 */
	public Event getEventByUUID(UUID uuid) {
		return this.events.get(uuid);
	}

    public void setActiveEvent(Event event) {
		this.plugin.getServer().getOnlinePlayers().stream().map(plugin.getProfileManager()::getByPlayer).filter(profile -> profile.isInLobby() && !profile.getKitEditor().isActive()).forEach(plugin.getProfileManager()::refreshHotbar);

		if (this.activeEvent != null) {
			this.activeEvent.setEventTask(null);
		}

		if (event == null) {
			this.activeEvent = null;
			return;
		}

		this.activeEvent = event;
		this.activeEvent.handleStart();
	}

	public boolean hostByType(Player player, EventType type, EventTeamSize size) {
		if (!player.hasPermission("*") && !player.isOp() && !player.hasPermission("array.event." + type.getName().toLowerCase())) {
			player.sendMessage(Locale.EVENT_NO_PERMISSION.toString().replace("<store>", plugin.getConfigHandler().getSTORE()));
			return false;
		}

		if (this.getActiveEvent() != null) {
			player.sendMessage(Locale.EVENT_ON_GOING.toString());
			return false;
		}
		if (!this.getCooldown().hasExpired()) {
			player.sendMessage(Locale.EVENT_COOLDOWN_ACTIVE.toString().replace("<expire_time>", this.getCooldown().getTimeLeft()));
			return false;
		}

		Button.playNeutral(player);

		switch (type) {
			case SUMO: {
				if (size.equals(EventTeamSize.SOLO)) {
					SumoSolo sumoSolo = new SumoSolo(plugin, player);
					this.setActiveEvent(sumoSolo);
					return true;
				}
				SumoTeam sumoTeam = new SumoTeam(plugin, player, size);
				this.setActiveEvent(sumoTeam);
				return true;
			}
			case SPLEEF: {
				Spleef spleef = new Spleef(plugin, player);
				this.setActiveEvent(spleef);
				return true;
			}
			case PARKOUR: {
				Parkour parkour = new Parkour(plugin, player);
				this.setActiveEvent(parkour);
				return true;
			}
			case KOTH: {
				//Koth koth = new Koth(plugin, player, size);
				//this.setActiveEvent(koth);
				return true;
			}
		}
		return false;
	}

	public boolean hostByTypeAndKit(Player player, EventType type, EventTeamSize size, Kit kit) {
		if (!player.hasPermission("*") && !player.isOp() && !player.hasPermission("array.event." + type.getName().toLowerCase())) {
			player.sendMessage(Locale.EVENT_NO_PERMISSION.toString().replace("<store>", plugin.getConfigHandler().getSTORE()));
			return false;
		}

		if (this.getActiveEvent() != null) {
			player.sendMessage(Locale.EVENT_ON_GOING.toString());
			return false;
		}
		if (!this.getCooldown().hasExpired()) {
			player.sendMessage(Locale.EVENT_COOLDOWN_ACTIVE.toString().replace("<expire_time>", this.getCooldown().getTimeLeft()));
			return false;
		}

		Button.playNeutral(player);

		switch (type) {
			case BRACKETS: {
				if (size.equals(EventTeamSize.SOLO)) {
					BracketsSolo bracketsSolo = new BracketsSolo(plugin, player, kit);
					this.setActiveEvent(bracketsSolo);
					return true;
				}
				BracketsTeam bracketsTeam = new BracketsTeam(plugin, player, kit, size);
				this.setActiveEvent(bracketsTeam);
				return true;
			}
			case LMS: {
				LMS lms = new LMS(plugin, player, kit);
				this.setActiveEvent(lms);
				return true;
			}
		}
		return false;
	}

	public EventType getByName(String name) {
		for ( EventType type : EventType.values() ) {
			if (type.name().equalsIgnoreCase(name) || type.getName().equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}

}
