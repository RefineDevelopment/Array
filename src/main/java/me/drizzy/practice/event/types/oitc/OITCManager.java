package me.drizzy.practice.event.types.oitc;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.event.types.oitc.task.OITCStartTask;
import me.drizzy.practice.util.external.Cooldown;
import me.drizzy.practice.util.external.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

public class OITCManager {

    @Getter private OITC activeOITC;
    @Getter @Setter private Cooldown cooldown = new Cooldown(0);
    @Getter @Setter private Location OITCSpectator;
    @Getter @Setter private String OITCKnockbackProfile;

    public OITCManager() {
        load();
    }

    public void setActiveOITC(OITC OITC) {
        if (activeOITC != null) {
            activeOITC.setEventTask(null);
        }

        if (OITC == null) {
            activeOITC = null;
            return;
        }

        activeOITC = OITC;
        activeOITC.setEventTask(new OITCStartTask(OITC));
    }

    public void load() {
        FileConfiguration configuration = Array.getInstance().getEventsConfig().getConfiguration();

        if (configuration.contains("events.oitc.spectator")) {
            OITCSpectator = LocationUtil.deserialize(configuration.getString("events.oitc.spectator"));
        }

        if (configuration.contains("events.oitc.knockback-profile")) {
            OITCKnockbackProfile = configuration.getString("events.oitc.knockback-profile");
        }
    }

    public void save() {
        FileConfiguration configuration = Array.getInstance().getEventsConfig().getConfiguration();

        if (OITCSpectator != null) {
            configuration.set("events.oitc.spectator", LocationUtil.serialize(OITCSpectator));
        }

        if (OITCKnockbackProfile != null) {
            configuration.set("events.oitc.knockback-profile", OITCKnockbackProfile);
        }

        try {
            configuration.save(Array.getInstance().getEventsConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
