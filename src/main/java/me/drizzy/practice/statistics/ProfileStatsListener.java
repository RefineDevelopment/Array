package me.drizzy.practice.statistics;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.tournament.Tournament;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ProfileStatsListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = Profile.getByUuid(player);

        if (profile.isInMatch()) {
            Match match = profile.getMatch();
            profile.getKitData().get(match.getKit()).incrementDeaths();
            if (event.getEntity().getKiller() != null && match.getPlayers().contains(event.getEntity().getKiller())) {
                Player killer = event.getEntity().getKiller();
                Profile killerp = Profile.getByUuid(killer);
                killerp.getKitData().get(match.getKit()).incrementKills();
            }
        }
        if (profile.isInEvent()) {
           if (profile.isInSumo()) {
               Kit kit = Kit.getByName("Sumo");
               profile.getKitData().get(kit).incrementDeaths();
               if (event.getEntity().getKiller() != null && profile.getSumo().getPlayers().contains(event.getEntity().getKiller())) {
                   Player killer = event.getEntity().getKiller();
                   Profile killerp = Profile.getByUuid(killer);
                   killerp.getKitData().get(kit).incrementKills();
               }
           }
            if (profile.isInBrackets()) {
                Kit kit = profile.getBrackets().getKit();
                profile.getKitData().get(kit).incrementDeaths();
                if (event.getEntity().getKiller() != null && profile.getBrackets().getPlayers().contains(event.getEntity().getKiller())) {
                    Player killer = event.getEntity().getKiller();
                    Profile killerp = Profile.getByUuid(killer);
                    killerp.getKitData().get(kit).incrementKills();
                }
            }
            if (profile.isInLMS()) {
                Kit kit = profile.getLms().getKit();
                profile.getKitData().get(kit).incrementDeaths();
                if (event.getEntity().getKiller() != null && profile.getLms().getPlayers().contains(event.getEntity().getKiller())) {
                    Player killer = event.getEntity().getKiller();
                    Profile killerp = Profile.getByUuid(killer);
                    killerp.getKitData().get(kit).incrementKills();
                }
            }
            if (profile.isInSpleef()) {
                Kit kit = Kit.getByName("NoDebuff");
                profile.getKitData().get(kit).incrementDeaths();
            }
        }
        if (profile.isInTournament(player)) {
            Tournament tournament = Tournament.CURRENT_TOURNAMENT;
            Kit kit = Kit.getByName("NoDebuff");
            profile.getKitData().get(kit).incrementDeaths();
            if (event.getEntity().getKiller() != null) {
                Player killer = event.getEntity().getKiller();
                Profile killerp = Profile.getByUuid(killer);
                killerp.getKitData().get(kit).incrementKills();
            }
        }
    }



}
