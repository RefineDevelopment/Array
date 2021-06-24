package xyz.refinedev.practice.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.config.BasicConfigurationFile;
import xyz.refinedev.practice.util.other.Cooldown;

@Getter
@Setter
public class EventManager {

	private final BasicConfigurationFile config = Array.getInstance().getEventsConfig();

	private Event activeEvent;
	private Cooldown eventCooldown = new Cooldown(0);

	//Locations for each event, I'm going to probably redo this in the future
	private Location sumoSpectator, sumoSpawn1, sumoSpawn2,
			         bracketsSpectator, bracketsSpawn1, bracketsSpawn2,
	                 gulagSpectator, gulagSpawn1, gulagSpawn2,
	                 spleefSpawn,
	                 lmsSpawn;

	//Kit based events will get their knockback profiles the kits
	private String sumoKbProfile, gulagKbProfile, spleefKbProfile;

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

	}

	public void save() {

	}

	public Location getSpawn1(Event event) {
		switch (event.getName()) {
			case "Sumo": {

			}
		}
	}

}
