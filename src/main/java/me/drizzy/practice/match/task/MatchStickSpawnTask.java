package me.drizzy.practice.match.task;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.Teleporter;

import java.util.HashMap;
import java.util.List;

public class MatchStickSpawnTask extends BukkitRunnable {
    
    
  private final Match match;
  private final List<Player> players;
  private final HashMap<Player, Location> locations = new HashMap<>();

  public MatchStickSpawnTask(Match match, List<Player> players) {
      this.players = players;
      this.match = match;
          for(Player s : players) {
              if(s != null) {
                 Location l = s.getLocation();
                 locations.put(s, l);
                }
            }
        }
        
        @Override
        public void run() {
            for( Player name : players) {
                if(name != null) {
                    Profile profile = Profile.getByUuid(name);
                    boolean pf = profile.isInFight();
                    if(!pf) continue;
                    Kit kit = match.getKit();
                    if(kit != null && kit.getGameRules().isStickspawn() || kit !=null && !kit.getGameRules().isSumo()) {
                        if(this.locations.containsKey(name)) {
                            Location l = this.locations.get(name);
                            Location plLoc = name.getLocation();
                            plLoc.setY(0);
                            Location clone = l.clone();
                            clone.setY(0);
                            if(plLoc.getWorld().getName().equals(clone.getWorld().getName()) && plLoc.distanceSquared(clone) > 0.1) {
                                Teleporter.syncTeleport(name, l);
                            }
                        }
                        else {
                            Location l = name.getLocation();
                            this.locations.put(name, l);
                        }
                    }
                }
            }
        }
    }
