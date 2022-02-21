package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventLocations;
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
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.ProfileState;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.other.Cooldown;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
@RequiredArgsConstructor
public class EventManager {

	private final Map<UUID, Event> events = new HashMap<>();
	private final Map<UUID, Event> spectators = new HashMap<>();

	private final Array plugin;
	private final BasicConfigurationFile config;

	private Event activeEvent;
	private World eventWorld;
	private EventLocations helper;
	private Cooldown cooldown = new Cooldown(0);

	/**
	 * Load event utilities and setup basic tasks
	 */
	public void init() {
		this.helper = new EventLocations(config);
		this.helper.loadLocations();

		boolean newWorld = false;

		if (plugin.getServer().getWorld("event") == null) {
			plugin.logger("&7Event world &cnot found&7, creating...");
			WorldCreator creator = new WorldCreator("event")
					.type(WorldType.FLAT)
					.generatorSettings("2;0;1;");

			eventWorld = this.plugin.getServer().createWorld(creator);
			newWorld = true;
			plugin.logger("&7Created &cevent world&7 successfully!");
		} else {
			eventWorld = this.plugin.getServer().getWorld("event");
			plugin.logger("&7Found &cevent world&7!");
		}

		if (eventWorld != null) {
			if (newWorld) this.plugin.getServer().getWorlds().add(this.eventWorld);
			this.eventWorld.setTime(2000L);
			this.eventWorld.setGameRuleValue("doDaylightCycle", "false");
			this.eventWorld.setGameRuleValue("doMobSpawning", "false");
			this.eventWorld.setStorm(false);
			this.eventWorld.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
			plugin.logger("&7Successfully finished &csetting up &7event world!");
		}

		for ( EventType event : EventType.values()) {
			plugin.getServer().getPluginManager().addPermission(new Permission("array.event." + event.name(), PermissionDefault.OP));
		}
	}

	/**
	 * Shutdown and clear each and every thing related to events
	 */
	public void shutdown() {
		this.events.values().forEach(Event::handleEnd);
		this.spectators.clear();
		this.events.clear();
		this.helper.save();
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
		if (!this.cooldown.hasExpired()) {
			player.sendMessage(Locale.EVENT_COOLDOWN_ACTIVE.toString().replace("<expire_time>", this.cooldown.getTimeLeft()));
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
		}
		return false;
	}

	public boolean hostByTypeAndKit(Player player, EventType type, EventTeamSize size, Kit kit) {
		if (this.getActiveEvent() != null) {
			player.sendMessage(Locale.EVENT_ON_GOING.toString());
			return false;
		}
		if (!this.cooldown.hasExpired()) {
			player.sendMessage(Locale.EVENT_COOLDOWN_ACTIVE.toString().replace("<expire_time>", this.cooldown.getTimeLeft()));
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


	public void addSpectator(Event event, UUID uuid) {
		this.getSpectators().put(uuid, event);

		Player player = Bukkit.getPlayer(uuid);
		Profile profile = this.plugin.getProfileManager().getProfileByUUID(uuid);
		profile.setState(ProfileState.SPECTATING);
		profile.setEvent(event.getEventId());

		this.plugin.getProfileManager().refreshHotbar(profile);
		this.plugin.getProfileManager().handleVisibility(profile);

		player.teleport(event.isFreeForAll() ? helper.getSpawn(event) : helper.getSpectator(event));
	}

	public void removeSpectator(UUID uuid) {
		this.getSpectators().remove(uuid);

		Profile profile = this.plugin.getProfileManager().getProfileByUUID(uuid);
		profile.setState(ProfileState.IN_LOBBY);
		profile.setEvent(null);

		this.plugin.getProfileManager().teleportToSpawn(profile);
	}

	public void refreshHotbar() {
		this.plugin.getServer().getOnlinePlayers().stream()
				.map(plugin.getProfileManager()::getProfileByPlayer)
				.filter(profile -> profile.isInLobby() && !profile.getKitEditor().isActive())
				.forEach(plugin.getProfileManager()::refreshHotbar);
	}
}
