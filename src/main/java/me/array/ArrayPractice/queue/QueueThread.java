package me.array.ArrayPractice.queue;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.impl.SoloMatch;
import me.array.ArrayPractice.match.impl.SumoMatch;
import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class QueueThread extends Thread {

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

                            Player secondPlayer = Bukkit.getPlayer(secondQueueProfile.getPlayerUuid());
                            Profile secondProfile = Profile.getByUuid(secondQueueProfile.getPlayerUuid());

                            if (secondPlayer == null) {
                                continue;
                            }

//							if (firstProfile.getOptions().isUsingPingFactor() ||
//							    secondProfile.getOptions().isUsingPingFactor()) {
//								if (firstPlayer.getPing() >= secondPlayer.getPing()) {
//									if (firstPlayer.getPing() - secondPlayer.getPing() >= 50) {
//										continue;
//									}
//								} else {
//									if (secondPlayer.getPing() - firstPlayer.getPing() >= 50) {
//										continue;
//									}
//								}
//							}

                            if (queue.getType() == QueueType.RANKED) {
                                if (!firstQueueProfile.isInRange(secondQueueProfile.getElo()) ||
                                        !secondQueueProfile.isInRange(firstQueueProfile.getElo())) {
                                    continue;
                                }
                            }
                            // Find arena
                            final Arena arena = Arena.getRandom(queue.getKit());

                            if (arena == null) {
                                continue;
                            }

                            if (arena.isActive()) continue;

                            if (queue.getKit().getGameRules().isBuild()) arena.setActive(true);

                            // Remove players from queue
                            queue.getPlayers().remove(firstQueueProfile);
                            queue.getPlayers().remove(secondQueueProfile);

                            TeamPlayer firstMatchPlayer = new TeamPlayer(firstPlayer);
                            TeamPlayer secondMatchPlayer = new TeamPlayer(secondPlayer);

                            if (queue.getType() == QueueType.RANKED) {
                                firstMatchPlayer.setElo(firstProfile.getKitData().get(queue.getKit()).getElo());
                                secondMatchPlayer.setElo(secondProfile.getKitData().get(queue.getKit()).getElo());
                            }

                            // Create match
                            Match match;
                            if(queue.getKit().getGameRules().isSumo()) {
                                match = new SumoMatch(queue, firstMatchPlayer, secondMatchPlayer,
                                        queue.getKit(), arena, queue.getQueueType());
                            } else {
                                match = new SoloMatch(queue, firstMatchPlayer, secondMatchPlayer,
                                        queue.getKit(), arena, queue.getQueueType(),0,0);
                            }


                            String[] opponentMessages = formatMessages(firstPlayer.getName(),
                                    secondPlayer.getName(), firstMatchPlayer.getElo(), secondMatchPlayer.getElo(),
                                    queue.getQueueType());

                            firstPlayer.sendMessage(CC.AQUA + CC.BOLD + "Match Found!");
                            firstPlayer.sendMessage(CC.GRAY + "");
                            secondPlayer.sendMessage(CC.AQUA + CC.BOLD + "Match Found!");
                            secondPlayer.sendMessage(CC.GRAY + "");
                            firstPlayer.sendMessage(opponentMessages[1]);
                            secondPlayer.sendMessage(opponentMessages[1]);
                            new BukkitRunnable() {
                                public void run() {
                                    match.start();
                                }
                            }.runTask(Practice.get());
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
