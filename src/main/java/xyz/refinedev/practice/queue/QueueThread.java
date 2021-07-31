package xyz.refinedev.practice.queue;

import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.SoloMatch;
import xyz.refinedev.practice.match.types.kit.BridgeMatch;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.rank.Rank;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class QueueThread extends Thread {

    private Arena arena;
    private Kit kit;
    private Match match;

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
                        final Player firstPlayer = Bukkit.getPlayer(firstQueueProfile.getUuid());

                        if (firstPlayer == null) {
                            continue;
                        }

                        final Profile firstProfile = Profile.getByUuid(firstQueueProfile.getUuid());

                        for (QueueProfile secondQueueProfile : queue.getPlayers()) {
                            if (firstQueueProfile.equals(secondQueueProfile)) {
                                continue;
                            }

                            Player secondPlayer = Bukkit.getPlayer(secondQueueProfile.getUuid());
                            Profile secondProfile = Profile.getByUuid(secondQueueProfile.getUuid());

                            if (secondPlayer == null) {
                                continue;
                            }

                            if (firstProfile.getSettings().isPingFactor() || secondProfile.getSettings().isPingFactor()) {
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

                            if (queue.getType() == QueueType.RANKED || queue.getType() == QueueType.CLAN) {
                                if (!firstQueueProfile.isInRange(secondQueueProfile.getElo()) ||
                                        !secondQueueProfile.isInRange(firstQueueProfile.getElo())) {
                                    continue;
                                }
                            }

                            if (queue.getType() == QueueType.CLAN) {
                                Clan firstClan = firstProfile.getClan();
                                Clan secondClan = secondProfile.getClan();

                                if (firstClan == secondClan){
                                    continue;
                                }
                            }

                            if (Arena.getArenas().isEmpty()) continue;

                            // Find arena
                            arena = Arena.getRandom(queue.getKit());

                            if (arena == null) continue;

                            if (!arena.isSetup()) continue;

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
                            kit = queue.getKit();

                            // Create match
                            Match match;
                            if (queue.getKit().getGameRules().isBuild() && queue.getKit().getGameRules().isBridge()) {
                                match = new BridgeMatch(queue, firstMatchPlayer, secondMatchPlayer, queue.getKit(), arena, queue.getType());
                            } else {
                                match = new SoloMatch(queue, firstMatchPlayer, secondMatchPlayer, queue.getKit(), arena, queue.getType());
                            }
                            this.match = match;

                            for ( String string : Locale.MATCH_SOLO_STARTMESSAGE.toList() ) {
                                String opponentMessages = this.formatMessages(firstPlayer, secondPlayer, string, Rank.getRankType().getFullName(firstPlayer), Rank.getRankType().getFullName(secondPlayer), firstProfile.getStatisticsData().get(kit).getElo(), secondProfile.getStatisticsData().get(kit).getElo(), queue.getType());
                                firstPlayer.sendMessage(replaceOpponent(opponentMessages, firstPlayer));
                                secondPlayer.sendMessage(replaceOpponent(opponentMessages, secondPlayer));
                            }

                            TaskUtil.run(match::start);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }

                continue;
            } try {
                Thread.sleep(200L);
            } catch (InterruptedException e3) {
                e3.printStackTrace();
            }
        }
    }

    private String formatMessages(Player player1Player, Player player2Player, String string, String player1, String player2, int player1Elo, int player2Elo, QueueType type) {
        return string
                .replace("<player1>", type == QueueType.RANKED ? player1 + CC.GRAY + " (" + player1Elo + ")" : player1)
                .replace("<match_type>", type == QueueType.RANKED ? "Ranked" : type == QueueType.CLAN ? "Clan" : "Unranked")
                .replace("<player1_ping>", String.valueOf(PlayerUtil.getPing(player1Player)))
                .replace("<player2_ping>", String.valueOf(PlayerUtil.getPing(player2Player)))
                .replace("<player2>", type == QueueType.RANKED ? player2 + CC.GRAY + " (" + player2Elo + ")" : player2);
    }

    private String replaceOpponent(String opponent, Player player) {
        opponent = opponent
                .replace("<opponent>", match.getOpponentPlayer(player).getDisplayName())
                .replace("<opponent_ping>", String.valueOf(PlayerUtil.getPing(match.getOpponentPlayer(player))))
                .replace("<player_ping>", String.valueOf(PlayerUtil.getPing(player)))
                .replace("<arena>", this.arena.getDisplayName())
                .replace("<kit>", this.kit.getDisplayName())
                .replace("<player>", player.getDisplayName());
        return opponent;
    }
}