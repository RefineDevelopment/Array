package me.drizzy.practice.event.types.gulag;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.gulag.task.GulagStartTask;
import me.drizzy.practice.util.external.Cooldown;
import me.drizzy.practice.util.external.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

@Getter
public class GulagManager {

	private Gulag activeGulag;
	@Setter private Cooldown cooldown = new Cooldown(0);
	@Setter private Location gulagSpectator;
	@Setter private Location gulagSpawn1;
	@Setter private Location gulagSpawn2;
	@Setter private String gulagKnockbackProfile;

	public GulagManager() {
		load();
	}

	public void setActiveGulag(Gulag gulag) {
		if (activeGulag != null) {
			activeGulag.setEventTask(null);
		}

		if (gulag == null) {
			activeGulag= null;
			return;
		}

		activeGulag=gulag;
		activeGulag.setEventTask(new GulagStartTask(gulag));
	}

	public void load() {
		FileConfiguration configuration = Array.getInstance().getEventsConfig().getConfiguration();

		if (configuration.contains("events.gulag.spectator")) {
			gulagSpectator = LocationUtil.deserialize(configuration.getString("events.gulag.spectator"));
		}

		if (configuration.contains("events.gulag.spawn1")) {
			gulagSpawn1 = LocationUtil.deserialize(configuration.getString("events.gulag.spawn1"));
		}

		if (configuration.contains("events.gulag.spawn2")) {
			gulagSpawn2 = LocationUtil.deserialize(configuration.getString("events.gulag.spawn2"));
		}

		if (configuration.contains("events.gulag.knockback-profile")) {
			gulagKnockbackProfile = configuration.getString("events.gulag.knockback-profile");
		}
	}

	public void save() {
		FileConfiguration configuration = Array.getInstance().getEventsConfig().getConfiguration();

		if (gulagSpectator != null) {
			configuration.set("events.gulag.spectator", LocationUtil.serialize(gulagSpectator));
		}

		if (gulagSpawn1 != null) {
			configuration.set("events.gulag.spawn1", LocationUtil.serialize(gulagSpawn1));
		}

		if (gulagSpawn2 != null) {
			configuration.set("events.gulag.spawn2", LocationUtil.serialize(gulagSpawn2));
		}

		if (gulagKnockbackProfile != null) {
			configuration.set("events.gulag.knockback-profile", gulagKnockbackProfile);
		}

		try {
			configuration.save(Array.getInstance().getEventsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
