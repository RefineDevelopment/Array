package xyz.refinedev.practice.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.events.impl.sumo.solo.SumoSolo;
import xyz.refinedev.practice.events.impl.sumo.team.SumoTeam;
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
	private Cooldown eventCooldown = new Cooldown(0);

	Location sumoSpawn1, sumoSpawn2, sumoSpectator;
	Location bracketsSpawn1, bracketsSpawn2, bracketsSpectator;
	Location gulagSpawn1, gulagSpawn2, gulagSpectator;
	Location lmsSpawn, parkourSpawn, spleefSpawn, omaSpawn;

	List<Location> oitcSpawns = new ArrayList<>();
	Location oitcSpectator;

	private String sumoKB = "Default", gulagKB = "Default", omaKB = "Default", spleefKB = "Default";

    public EventManager(Array plugin) {
        this.plugin = plugin;
        this.config = plugin.getEventsConfig();
    }

    public void setActiveEvent(Event event) {
		plugin.getServer().getOnlinePlayers().stream().map(Profile::getByPlayer).filter(profile -> profile.isInLobby() && !profile.getKitEditor().isActive()).forEach(Profile::refreshHotbar);

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

		if (config.getString(key + "SUMO.SPAWN1") != null) this.sumoSpawn1 = LocationUtil.deserialize(config.getString(key + "SUMO.SPAWN1"));
		if (config.getString(key + "SUMO.SPAWN2") != null) this.sumoSpawn2 = LocationUtil.deserialize(config.getString(key + "SUMO.SPAWN2"));
		if (config.getString(key + "SUMO.SPECTATOR") != null) this.sumoSpectator = LocationUtil.deserialize(config.getString(key + "SUMO.SPECTATOR"));
		if (config.getString(key + "SUMO.KNOCKBACK") != null) this.sumoKB = config.getString(key + "SUMO.KNOCKBACK");

		if (config.getString(key + "BRACKETS.SPAWN1") != null) this.bracketsSpawn1 = LocationUtil.deserialize(config.getString(key + "BRACKETS.SPAWN1"));
		if (config.getString(key + "BRACKETS.SPAWN2") != null) this.bracketsSpawn2 = LocationUtil.deserialize(config.getString(key + "BRACKETS.SPAWN2"));
		if (config.getString(key + "BRACKETS.SPECTATOR") != null) this.bracketsSpectator = LocationUtil.deserialize(config.getString(key + "BRACKETS.SPECTATOR"));

		if (config.getString(key + "GULAG.SPAWN1") != null) this.gulagSpawn1 = LocationUtil.deserialize(config.getString(key + "GULAG.SPAWN1"));
		if (config.getString(key + "GULAG.SPAWN2") != null) this.gulagSpawn2 = LocationUtil.deserialize(config.getString(key + "GULAG.SPAWN2"));
		if (config.getString(key + "GULAG.SPECTATOR") != null) this.gulagSpectator = LocationUtil.deserialize(config.getString(key + "GULAG.SPECTATOR"));
		if (config.getString(key + "GULAG.KNOCKBACK") != null) this.gulagKB = config.getString(key + "GULAG.KNOCKBACK");

		if (config.getString(key + "LMS.SPAWN") != null) this.lmsSpawn = LocationUtil.deserialize(config.getString(key + "LMS.SPAWN"));

		if (config.getString(key + "PARKOUR.SPAWN") != null) this.parkourSpawn = LocationUtil.deserialize(config.getString(key + "PARKOUR.SPAWN"));

		if (config.getString(key + "SPLEEF.SPAWN") != null) this.spleefSpawn = LocationUtil.deserialize(config.getString(key + "SPLEEF.SPAWN"));
		if (config.getString(key + "SPLEEF.KNOCKBACK") != null) this.spleefKB = config.getString(key + "SPLEEF.KNOCKBACK");


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
		if (gulagSpawn2 != null) config.set(key + "GULAG.SPAWN2", LocationUtil.serialize(gulagSpawn1));
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
			case OMA:
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
				return oitcSpectator;
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


}
