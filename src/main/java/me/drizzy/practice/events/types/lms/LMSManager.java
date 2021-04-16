package me.drizzy.practice.events.types.lms;

import me.drizzy.practice.events.types.lms.task.LMSStartTask;
import me.drizzy.practice.Array;
import me.drizzy.practice.util.other.Cooldown;
import me.drizzy.practice.util.location.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

@Getter
@Setter
public class LMSManager {

    private LMS activeLMS;
    private Cooldown cooldown = new Cooldown(0);
    private Location lmsSpectator;
    private String lmsKnockbackProfile;

    public LMSManager() {
        load();
    }

    public void setActiveLMS(LMS LMS) {
        if (activeLMS != null) {
            activeLMS.setEventTask(null);
        }

        if (LMS == null) {
            activeLMS = null;
            return;
        }

        activeLMS = LMS;
        activeLMS.setEventTask(new LMSStartTask(LMS));
    }

    public void load() {
        FileConfiguration configuration = Array.getInstance().getEventsConfig().getConfiguration();

        if (configuration.contains("events.lms.spectator")) {
            lmsSpectator = LocationUtil.deserialize(configuration.getString("events.lms.spectator"));
        }

        if (configuration.contains("events.lms.knockback-profile")) {
            lmsKnockbackProfile = configuration.getString("events.lms.knockback-profile");
        }
    }

    public void save() {
        FileConfiguration configuration = Array.getInstance().getEventsConfig().getConfiguration();

        if (lmsSpectator != null) {
            configuration.set("events.lms.spectator", LocationUtil.serialize(lmsSpectator));
        }

        if (lmsKnockbackProfile != null) {
            configuration.set("events.lms.knockback-profile", lmsKnockbackProfile);
        }

        try {
            configuration.save(Array.getInstance().getEventsConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
