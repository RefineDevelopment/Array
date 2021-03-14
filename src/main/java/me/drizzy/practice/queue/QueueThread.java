package me.drizzy.practice.queue;

import me.drizzy.practice.Array;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.match.types.SoloMatch;
import me.drizzy.practice.match.types.SumoMatch;
import me.drizzy.practice.match.types.TheBridgeMatch;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.rank.RankType;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class QueueThread extends Thread {

    Arena arena;
    Kit kit;
    RankType rank = Array.getInstance().getRankManager();

    @Override
    public void run() {
        while (true) {
            try {
                for (Queue queue : Queue.getQueues()) {
                    queue.getPlayers().forEach(QueueProfile::tickRange);

                    if (queue.getPlayers().size() < 2) {
                        continue;
                    }

                    for (QueueProfile firstQueueProfile : queue.getPlayers()) {
                        final Player firstPlayer = Bukkit.getPlayer(firstQueueProfile.getPlayerUuid());

                        if (firstPlayer == null) {
                            continue;
                        }

                        final Profile firstProfile = Profile.getByUuid(firstQueueProfile.getPlayerUuid());

                        for (QueueProfile secondQueueProfile : queue.getPlayers()) {
                            if (firstQueueProfile.equals(secondQueueProfile)) {
                                continue;
                            }

                            Player secondPlayer=Bukkit.getPlayer(secondQueueProfile.getPlayerUuid());
                            Profile secondProfile=Profile.getByUuid(secondQueueProfile.getPlayerUuid());

                            if (secondPlayer == null) {
                                continue;
                            }

                            if (firstProfile.getSettings().isUsingPingFactor() ||
                                    secondProfile.getSettings().isUsingPingFactor()) {
                                if (PlayerUtil.getPing(firstPlayer) >= PlayerUtil.getPing(secondPlayer)) {
                                    if (PlayerUtil.getPing(firstPlayer) - PlayerUtil.getPing(secondPlayer) >= 50) {
                                        continue;
                                    }
                                } else {
                                    if (PlayerUtil.getPing(secondPlayer) - PlayerUtil.getPing(firstPlayer) >= 50) {
                                        continue;
                                    }
                                }
                            }

                            if (queue.getType() == QueueType.RANKED) {
                                if (!firstQueueProfile.isInRange(secondQueueProfile.getElo()) ||
                                        !secondQueueProfile.isInRange(firstQueueProfile.getElo())) {
                                    continue;
                                }
                            }
                            // Find arena
                            arena=Arena.getRandom(queue.getKit());

                            if (arena == null) {
                                queue.getPlayers().remove(firstQueueProfile);
                                queue.getPlayers().remove(secondQueueProfile);
                                firstPlayer.sendMessage(CC.translate("&cNo arenas available."));
                                secondPlayer.sendMessage(CC.translate("&cNo arenas available."));
                                continue;
                            }

                            if (arena.isActive()) continue;

                            if (queue.getKit().getGameRules().isBuild()) arena.setActive(true);

                            // Remove players from queue
                            queue.getPlayers().remove(firstQueueProfile);
                            queue.getPlayers().remove(secondQueueProfile);

                            TeamPlayer firstMatchPlayer=new TeamPlayer(firstPlayer);
                            TeamPlayer secondMatchPlayer=new TeamPlayer(secondPlayer);

                            if (queue.getType() == QueueType.RANKED) {
                                firstMatchPlayer.setElo(firstProfile.getStatisticsData().get(queue.getKit()).getElo());
                                secondMatchPlayer.setElo(secondProfile.getStatisticsData().get(queue.getKit()).getElo());
                                secondProfile.calculateGlobalElo();
                                firstProfile.calculateGlobalElo();
                            }
                            kit=queue.getKit();

                            // Create match
                            Match match;
                            if (queue.getKit().getGameRules().isSumo()) {
                                match = new SumoMatch(queue, firstMatchPlayer, secondMatchPlayer,
                                        queue.getKit(), arena, queue.getQueueType());
                            } else if (queue.getKit().getGameRules().isBuild() && queue.getKit().getGameRules().isBridge()) {
                                match = new TheBridgeMatch(queue, firstMatchPlayer, secondMatchPlayer,
                                        queue.getKit(), arena, queue.getQueueType());
                            } else {
                                match = new SoloMatch(queue, firstMatchPlayer, secondMatchPlayer,
                                        queue.getKit(), arena, queue.getQueueType(), 0, 0);
                            }
                            for ( String string : Array.getInstance().getMessagesConfig().getStringList("Match.Start-Message.Solo") ) {
                                final String opponentMessages=this.formatMessages(string, rank.getFullName(firstPlayer), rank.getFullName(secondPlayer), firstMatchPlayer.getElo(), secondMatchPlayer.getElo(), queue.getQueueType());
                                final String message=CC.translate(this.replace(opponentMessages));
                                firstPlayer.sendMessage(message);
                                secondPlayer.sendMessage(message);
                            }
                            new BukkitRunnable() {
                                public void run() {
                                    match.start();
                                }
                            }.runTask(Array.getInstance());
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
    private String formatMessages(final String string, final String player1, final String player2, final int player1Elo, final int player2Elo, final QueueType type) {
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
        return string.replace("{player1}", player1Format).replace("{player2}", player2Format);
    }

    public String replace(String string) {
        string = string.replace("{arena}", this.arena.getDisplayName())
                .replace("{kit}", this.kit.getDisplayName());
        return string;
    }
}
