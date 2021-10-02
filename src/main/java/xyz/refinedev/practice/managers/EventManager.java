package xyz.refinedev.practice.managers;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.event.EventType;
import xyz.refinedev.practice.event.impl.spleef.Spleef;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.config.impl.BasicConfigurationFile;
import xyz.refinedev.practice.util.location.LocationUtil;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.other.Cooldown;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class EventManager {

	private final Array plugin;
	private final BasicConfigurationFile config;

	private Event activeEvent;
	private Cooldown cooldown = new Cooldown(0);

	private Location sumoSpawn1, sumoSpawn2, sumoSpectator;
	private Location bracketsSpawn1, bracketsSpawn2, bracketsSpectator;
	private Location gulagSpawn1, gulagSpawn2, gulagSpectator;
	private Location lmsSpawn, parkourSpawn, spleefSpawn, omaSpawn;

	private List<Location> OITCSpawns = new ArrayList<>();
	private Location OITCSpectator;

	private String sumoKB = "default", gulagKB = "default", omaKB = "default", spleefKB = "default";

    public EventManager(Array plugin) {
        this.plugin = plugin;
        this.config = plugin.getEventsConfig();
    }

    public void setActiveEvent(Event event) {
		plugin.getServer().getOnlinePlayers().stream().map(plugin.getProfileManager()::getByPlayer).filter(profile -> profile.isInLobby() && !profile.getKitEditor().isActive()).forEach(plugin.getProfileManager()::refreshHotbar);

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

	public void init() {
		String key = "EVENTS.";

		if (config.contains(key + "SUMO.SPAWN1")) this.sumoSpawn1 = LocationUtil.deserialize(config.getString(key + "SUMO.SPAWN1"));
		if (config.contains(key + "SUMO.SPAWN2")) this.sumoSpawn2 = LocationUtil.deserialize(config.getString(key + "SUMO.SPAWN2"));
		if (config.contains(key + "SUMO.SPECTATOR")) this.sumoSpectator = LocationUtil.deserialize(config.getString(key + "SUMO.SPECTATOR"));
		if (config.contains(key + "SUMO.KNOCKBACK")) this.sumoKB = config.getString(key + "SUMO.KNOCKBACK");

		if (config.contains(key + "BRACKETS.SPAWN1")) this.bracketsSpawn1 = LocationUtil.deserialize(config.getString(key + "BRACKETS.SPAWN1"));
		if (config.contains(key + "BRACKETS.SPAWN2")) this.bracketsSpawn2 = LocationUtil.deserialize(config.getString(key + "BRACKETS.SPAWN2"));
		if (config.contains(key + "BRACKETS.SPECTATOR")) this.bracketsSpectator = LocationUtil.deserialize(config.getString(key + "BRACKETS.SPECTATOR"));

		if (config.contains(key + "GULAG.SPAWN1")) this.gulagSpawn1 = LocationUtil.deserialize(config.getString(key + "GULAG.SPAWN1"));
		if (config.contains(key + "GULAG.SPAWN2")) this.gulagSpawn2 = LocationUtil.deserialize(config.getString(key + "GULAG.SPAWN2"));
		if (config.contains(key + "GULAG.SPECTATOR")) this.gulagSpectator = LocationUtil.deserialize(config.getString(key + "GULAG.SPECTATOR"));
		if (config.contains(key + "GULAG.KNOCKBACK")) this.gulagKB = config.getString(key + "GULAG.KNOCKBACK");

		if (config.contains(key + "LMS.SPAWN")) this.lmsSpawn = LocationUtil.deserialize(config.getString(key + "LMS.SPAWN"));

		if (config.contains(key + "PARKOUR.SPAWN")) this.parkourSpawn = LocationUtil.deserialize(config.getString(key + "PARKOUR.SPAWN"));

		if (config.contains(key + "SPLEEF.SPAWN")) this.spleefSpawn = LocationUtil.deserialize(config.getString(key + "SPLEEF.SPAWN"));
		if (config.contains(key + "SPLEEF.KNOCKBACK")) this.spleefKB = config.getString(key + "SPLEEF.KNOCKBACK");


		config.save();
	}

	public void save() {
		String key = "EVENTS.";

		if (sumoSpawn1 != null) config.set(key + "SUMO.SPAWN1", LocationUtil.serialize(sumoSpawn1));
		if (sumoSpawn2 != null) config.set(key + "SUMO.SPAWN2", LocationUtil.serialize(sumoSpawn2));
		if (sumoSpectator != null) config.set(key + "SUMO.SPECTATOR", LocationUtil.serialize(sumoSpectator));

		if (bracketsSpawn1 != null) config.set(key + "BRACKETS.SPAWN1", LocationUtil.serialize(bracketsSpawn1));
		if (bracketsSpawn2 != null) config.set(key + "BRACKETS.SPAWN2", LocationUtil.serialize(bracketsSpawn2));
		if (bracketsSpectator != null) config.set(key + "BRACKETS.SPECTATOR", LocationUtil.serialize(bracketsSpectator));

		if (gulagSpawn1 != null) config.set(key + "GULAG.SPAWN1", LocationUtil.serialize(gulagSpawn1));
		if (gulagSpawn2 != null) config.set(key + "GULAG.SPAWN2", LocationUtil.serialize(gulagSpawn2));
		if (gulagSpectator != null) config.set(key + "GULAG.SPECTATOR", LocationUtil.serialize(gulagSpectator));

		if (lmsSpawn != null) config.set(key + "LMS.SPAWN", LocationUtil.serialize(lmsSpawn));
		if (parkourSpawn != null) config.set(key + "PARKOUR.SPAWN", LocationUtil.serialize(parkourSpawn));
		if (spleefSpawn != null) config.set(key + "SPLEEF.SPAWN", LocationUtil.serialize(spleefSpawn));

		config.save();
	}

	public boolean hostByType(Player player, EventType type) {
		if (!player.hasPermission("*") && !player.isOp() && !player.hasPermission("array.event." + type.getName().toLowerCase())) {
			player.sendMessage(Locale.EVENT_NO_PERMISSION.toString().replace("<store>", plugin.getConfigHandler().getSTORE()));
			return false;
		}

		Button.playNeutral(player);

		switch (type) {
			case PARKOUR: {

				return true;
			}
			case KOTH: {
				return true;
			}
			case LMS: {
				return true;
			}
			case SPLEEF: {
				Spleef spleef = new Spleef(plugin, player);
				this.setActiveEvent(spleef);
				return true;
			}
		}
		return false;
	}

	public Location getSpawn1(Event event) {
		switch (event.getType()) {
			case SUMO_SOLO:
			case SUMO_TEAM:
				return sumoSpawn1;
			case BRACKETS_SOLO:
			case BRACKETS_TEAM:
				return bracketsSpawn1;
			case GULAG_SOLO:
			case GULAG_TEAM:
				return gulagSpawn1;
		}
		return null;
	}

	public Location getSpawn2(Event event) {
		switch (event.getType()) {
			case SUMO_SOLO:
			case SUMO_TEAM:
				return sumoSpawn2;
			case BRACKETS_SOLO:
			case BRACKETS_TEAM:
				return bracketsSpawn2;
			case GULAG_SOLO:
			case GULAG_TEAM:
				return gulagSpawn2;
		}
		return null;
	}

	public Location getSpawn(Event event) {
		switch (event.getType()) {
			case SPLEEF:
				return spleefSpawn;
			case PARKOUR:
				return parkourSpawn;
			case LMS:
				return lmsSpawn;
			case JUGGERNAUT:
				return omaSpawn;
		}
		return null;
	}

	public Location getSpectator(Event event) {
		switch (event.getType()) {
			case SUMO_SOLO:
			case SUMO_TEAM:
				return sumoSpectator;
			case BRACKETS_SOLO:
			case BRACKETS_TEAM:
				return bracketsSpectator;
			case GULAG_SOLO:
			case GULAG_TEAM:
				return gulagSpectator;
			case OITC:
				return OITCSpectator;
		}
		return null;
	}

	public boolean isUnfinished(Event event) {
		if (event.isFreeForAll()) {
			return this.getSpawn(event) == null;
		} else {
			return this.getSpawn1(event) == null || this.getSpawn2(event) == null || this.getSpectator(event) == null;
		}
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
