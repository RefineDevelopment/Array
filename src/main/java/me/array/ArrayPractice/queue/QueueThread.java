package me.array.ArrayPractice.queue;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.PlayerUtil;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import me.array.ArrayPractice.match.impl.SoloMatch;
import me.array.ArrayPractice.match.team.TeamPlayer;
import org.bukkit.Bukkit;

public class QueueThread extends Thread
{
    @Override
    public void run() {
        while (true) {
            try {
                for (final Queue queue : Queue.getQueues()) {
                    queue.getPlayers().forEach(QueueProfile::tickRange);
                    if (queue.getPlayers().size() < 2) {
                        continue;
                    }
                    for (final QueueProfile firstQueueProfile : queue.getPlayers()) {
                        final Player firstPlayer = Bukkit.getPlayer(firstQueueProfile.getPlayerUuid());
                        if (firstPlayer == null) {
                            continue;
                        }
                        final Profile firstProfile = Profile.getByUuid(firstQueueProfile.getPlayerUuid());
                        for (final QueueProfile secondQueueProfile : queue.getPlayers()) {
                            if (firstQueueProfile.equals(secondQueueProfile)) {
                                continue;
                            }
                            final Player secondPlayer = Bukkit.getPlayer(secondQueueProfile.getPlayerUuid());
                            final Profile secondProfile = Profile.getByUuid(secondQueueProfile.getPlayerUuid());
                            if (secondPlayer == null) {
                                continue;
                            }
                            if (queue.getType() == QueueType.RANKED) {
                                if (!firstQueueProfile.isInRange(secondQueueProfile.getElo())) {
                                    continue;
                                }
                                if (!secondQueueProfile.isInRange(firstQueueProfile.getElo())) {
                                    continue;
                                }
                            }
                            final Arena arena = Arena.getRandom(queue.getKit());
                            if (arena == null) {
                                continue;
                            }
                            if (arena.isActive()) {
                                continue;
                            }
                            if (queue.getKit().getGameRules().isBuild()) {
                                arena.setActive(true);
                            }
                            queue.getPlayers().remove(firstQueueProfile);
                            queue.getPlayers().remove(secondQueueProfile);
                            final TeamPlayer firstMatchPlayer = new TeamPlayer(firstPlayer);
                            final TeamPlayer secondMatchPlayer = new TeamPlayer(secondPlayer);
                            if (queue.getType() == QueueType.RANKED) {
                                firstMatchPlayer.setElo(firstProfile.getKitData().get(queue.getKit()).getElo());
                                secondMatchPlayer.setElo(secondProfile.getKitData().get(queue.getKit()).getElo());
                            }
                            final Match match = new SoloMatch(queue, firstMatchPlayer, secondMatchPlayer, queue.getKit(), arena, queue.getQueueType());
                            final String[] opponentMessages = this.formatMessages(firstPlayer.getName(), secondPlayer.getName(), firstMatchPlayer.getElo(), secondMatchPlayer.getElo(), queue.getQueueType());
                            firstPlayer.sendMessage(CC.AQUA + CC.BOLD + "Match Found!");
                            firstPlayer.sendMessage(CC.GRAY + "");
                            secondPlayer.sendMessage(CC.AQUA + CC.BOLD + "Match Found!");
                            firstPlayer.sendMessage(CC.GRAY + "");
                            secondPlayer.sendMessage(opponentMessages[1]);
                            new BukkitRunnable() {
                                public void run() {
                                    match.start();
                                }
                            }.runTask(Array.get());
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(200L);
                }
                catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
                continue;
            }
            try {
                Thread.sleep(200L);
            }
            catch (InterruptedException e3) {
                e3.printStackTrace();
            }
        }
    }

    private String[] formatMessages(final String player1, final String player2, final int player1Elo, final int player2Elo, final QueueType type) {
        String player1Format;
        String player2Format;
        if (type == QueueType.UNRANKED) {
            player1Format = player1;
            player2Format = player2;
        }
        else if (type == QueueType.RANKED) {
            player1Format = player1 + CC.GRAY + " (" + player1Elo + ")";
            player2Format = player2 + CC.GRAY + " (" + player2Elo + ")";
        }
        else {
            player1Format = player1;
            player2Format = player2;
        }
        return new String[] { CC.translate( "&b● &fPlayers: &b" + CC.AQUA + player1Format + CC.GRAY + " vs " + CC.AQUA + player2Format), CC.translate("&b● &fPlayers: &b" + player2Format + "&7 vs " + "&b" + player1Format) };
    }
}
