package me.drizzy.practice.statistics;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class StatisticsListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = Profile.getByUuid(player);

        if (profile.isInMatch()) {
            Match match = profile.getMatch();
            profile.getStatisticsData().get(match.getKit()).incrementDeaths();
            if (event.getEntity().getKiller() != null && match.getPlayers().contains(event.getEntity().getKiller())) {
                Player killer = event.getEntity().getKiller();
                Profile killerp = Profile.getByUuid(killer);
                killerp.getStatisticsData().get(match.getKit()).incrementKills();
            }
        }
        if (profile.isInEvent()) {
           if (profile.isInSumo()) {
               Kit kit = Kit.getByName("Sumo");
               profile.getStatisticsData().get(kit).incrementDeaths();
               if (event.getEntity().getKiller() != null && profile.getSumo().getPlayers().contains(event.getEntity().getKiller())) {
                   Player killer = event.getEntity().getKiller();
                   Profile killerp = Profile.getByUuid(killer);
                   killerp.getStatisticsData().get(kit).incrementKills();
               }
           }
            if (profile.isInBrackets()) {
                Kit kit = profile.getBrackets().getKit();
                profile.getStatisticsData().get(kit).incrementDeaths();
                if (event.getEntity().getKiller() != null && profile.getBrackets().getPlayers().contains(event.getEntity().getKiller())) {
                    Player killer = event.getEntity().getKiller();
                    Profile killerp = Profile.getByUuid(killer);
                    killerp.getStatisticsData().get(kit).incrementKills();
                }
            }
            if (profile.isInLMS()) {
                Kit kit = profile.getLms().getKit();
                profile.getStatisticsData().get(kit).incrementDeaths();
                if (event.getEntity().getKiller() != null && profile.getLms().getPlayers().contains(event.getEntity().getKiller())) {
                    Player killer = event.getEntity().getKiller();
                    Profile killerp = Profile.getByUuid(killer);
                    killerp.getStatisticsData().get(kit).incrementKills();
                }
            }
            if (profile.isInSpleef()) {
                Kit kit = Kit.getByName("NoDebuff");
                profile.getStatisticsData().get(kit).incrementDeaths();
            }
        }
        if (profile.isInTournament(player)) {
            Kit kit = Kit.getByName("NoDebuff");
            profile.getStatisticsData().get(kit).incrementDeaths();
            if (event.getEntity().getKiller() != null) {
                Player killer = event.getEntity().getKiller();
                Profile killerp = Profile.getByUuid(killer);
                killerp.getStatisticsData().get(kit).incrementKills();
            }
        }
    }



}
