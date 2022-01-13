/*package xyz.refinedev.practice.event.impl.koth.task;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.refinedev.practice.event.impl.koth.Koth;
import xyz.refinedev.practice.event.meta.player.EventPlayer;
import xyz.refinedev.practice.util.location.KothPoint;


@RequiredArgsConstructor
public class KoTHDetectTask extends BukkitRunnable {

    private final Koth koth;

    @Override
    public void run() {
        KothPoint point = koth.getEventManager().getKothPoint();

        if (!koth.isFighting()) return;

        for (Player  player : koth.getPlayers()) {
            if (!point.getCuboid().contains(player.getLocation())) continue;

            EventPlayer eventPlayer = koth.getEventPlayer(player.getUniqueId());
            if (eventPlayer == null) continue;

            eventPlayer.setInKoth(true);
        }
    }
}
*/