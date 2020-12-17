package me.array.ArrayPractice.event.impl.sumo;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.util.external.Cooldown;
import me.array.ArrayPractice.util.external.LocationUtil;
import me.array.ArrayPractice.event.impl.sumo.task.SumoStartTask;

import java.io.IOException;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class SumoManager {

	@Getter private Sumo activeSumo;
	@Getter @Setter private Cooldown cooldown = new Cooldown(0);
	@Getter @Setter private Location sumoSpectator;
	@Getter @Setter private Location sumoSpawn1;
	@Getter @Setter private Location sumoSpawn2;
	@Getter @Setter private String sumoKnockbackProfile;

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
		FileConfiguration configuration = Array.get().getEventsConfig().getConfiguration();

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
		FileConfiguration configuration = Array.get().getEventsConfig().getConfiguration();

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
			configuration.save(Array.get().getEventsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
