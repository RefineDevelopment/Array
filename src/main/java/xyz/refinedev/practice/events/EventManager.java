package xyz.refinedev.practice.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;
import xyz.refinedev.practice.util.location.LocationUtil;
import xyz.refinedev.practice.util.other.Cooldown;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EventManager {

	private final BasicConfigurationFile config = Array.getInstance().getEventsConfig();

	private Event activeEvent;
	private Cooldown eventCooldown = new Cooldown(0);

	protected Location sumoSpawn1, sumoSpawn2, sumoSpectator;
	protected Location bracketsSpawn1, bracketsSpawn2, bracketsSpectator;
	protected Location gulagSpawn1, gulagSpawn2, gulagSpectator;
	protected Location lmsSpawn, parkourSpawn, spleefSpawn, omaSpawn;

	private String sumoKB, gulagKB, omaKB, spleefKB;

	protected List<Location> oitcSpawns = new ArrayList<>();
	protected Location oitcSpectator;

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
		this.activeEvent.handleJoin(event.getHost().getPlayer());
	}

	public void load() {
		String key = "EVENTS.";
		
		this.sumoSpawn1 = LocationUtil.deserialize(config.getString(key + "SUMO.SPAWN1"));
		this.sumoSpawn2 = LocationUtil.deserialize(config.getString(key + "SUMO.SPAWN2"));
		this.sumoSpectator = LocationUtil.deserialize(config.getString(key + "SUMO.SPECTATOR"));
		
	}

	public void save() {
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


}
