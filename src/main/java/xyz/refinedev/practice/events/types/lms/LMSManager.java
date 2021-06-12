package xyz.refinedev.practice.events.types.lms;

import xyz.refinedev.practice.events.types.lms.task.LMSStartTask;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.util.other.Cooldown;
import xyz.refinedev.practice.util.location.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;

@Getter @Setter
public class LMSManager {

    private LMS activeLMS;
    private Cooldown cooldown = new Cooldown(0);
    private Location lmsSpawn;
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

        if (configuration.contains("events.lms.spawn")) {
            lmsSpawn= LocationUtil.deserialize(configuration.getString("events.lms.spawn"));
        }

        if (configuration.contains("events.lms.knockback-profile")) {
            lmsKnockbackProfile = configuration.getString("events.lms.knockback-profile");
        }
    }

    public void save() {
        FileConfiguration configuration = Array.getInstance().getEventsConfig().getConfiguration();

        if (lmsSpawn != null) {
            configuration.set("events.lms.spectator", LocationUtil.serialize(lmsSpawn));
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
