package me.drizzy.practice.events.types.sumo;

import me.drizzy.practice.Array;
import me.drizzy.practice.events.types.sumo.task.SumoStartTask;
import me.drizzy.practice.util.other.Cooldown;
import me.drizzy.practice.util.location.LocationUtil;

import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

@Getter @Setter
public class SumoManager {

	private Sumo activeSumo;
	private Cooldown cooldown = new Cooldown(0);
	private Location sumoSpectator;
	private Location sumoSpawn1;
	private Location sumoSpawn2;
	private String sumoKnockbackProfile;

	public SumoManager() {
		load();
	}

	public void setActiveSumo(Sumo sumo) {
		if (activeSumo != null) {
			activeSumo.setEventTask(null);
		}

		if (sumo == null) {
			activeSumo = null;
			return;
		}

		activeSumo = sumo;
		activeSumo.setEventTask(new SumoStartTask(sumo));
	}

	public void load() {
		FileConfiguration configuration = Array.getInstance().getEventsConfig().getConfiguration();

		if (configuration.contains("events.sumo.spectator")) {
			sumoSpectator = LocationUtil.deserialize(configuration.getString("events.sumo.spectator"));
		}

		if (configuration.contains("events.sumo.spawn1")) {
			sumoSpawn1 = LocationUtil.deserialize(configuration.getString("events.sumo.spawn1"));
		}

		if (configuration.contains("events.sumo.spawn2")) {
			sumoSpawn2 = LocationUtil.deserialize(configuration.getString("events.sumo.spawn2"));
		}

		if (configuration.contains("events.sumo.knockback-profile")) {
			sumoKnockbackProfile = configuration.getString("events.sumo.knockback-profile");
		}
	}

	public void save() {
		FileConfiguration configuration = Array.getInstance().getEventsConfig().getConfiguration();

		if (sumoSpectator != null) {
			configuration.set("events.sumo.spectator", LocationUtil.serialize(sumoSpectator));
		}

		if (sumoSpawn1 != null) {
			configuration.set("events.sumo.spawn1", LocationUtil.serialize(sumoSpawn1));
		}

		if (sumoSpawn2 != null) {
			configuration.set("events.sumo.spawn2", LocationUtil.serialize(sumoSpawn2));
		}

		if (sumoKnockbackProfile != null) {
			configuration.set("events.sumo.knockback-profile", sumoKnockbackProfile);
		}

		try {
			configuration.save(Array.getInstance().getEventsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
