package xyz.refinedev.practice.events.types.parkour;

import lombok.Getter;
import lombok.Setter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.other.Cooldown;
import xyz.refinedev.practice.util.location.LocationUtil;
import xyz.refinedev.practice.events.types.parkour.task.ParkourStartTask;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

@Getter @Setter
public class ParkourManager {

	private Parkour activeParkour;
	private Cooldown cooldown = new Cooldown(0);
	private Location parkourSpawn;

	public ParkourManager() {
		load();
	}

	public void setActiveParkour(Parkour parkour) {
		if (activeParkour != null) {
			activeParkour.setEventTask(null);
		}

		if (parkour == null) {
			activeParkour = null;
			return;
		}

		activeParkour = parkour;
		activeParkour.setEventTask(new ParkourStartTask(parkour));
	}

	public void load() {
		FileConfiguration configuration = Array.getInstance().getEventsConfig().getConfiguration();

		if (configuration.contains("events.parkour.spawn")) {
			parkourSpawn = LocationUtil.deserialize(configuration.getString("events.parkour.spawn"));
		}
	}

	public void save() {
		FileConfiguration configuration = Array.getInstance().getEventsConfig().getConfiguration();

		if (parkourSpawn != null) {
			configuration.set("events.parkour.spectator", LocationUtil.serialize(parkourSpawn));
		}

		try {
			configuration.save(Array.getInstance().getEventsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
