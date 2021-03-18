package me.drizzy.practice.statistics.task;

import me.drizzy.practice.profile.Profile;

public class EloRegulatorTask implements Runnable {
    @Override
    public void run() {
        for ( Profile profile :Profile.getProfiles().values()) {
            profile.save();
            profile.updateElo();
            profile.calculateGlobalElo();
        }

    }
}
