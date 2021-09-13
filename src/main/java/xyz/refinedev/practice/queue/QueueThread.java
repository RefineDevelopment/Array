package xyz.refinedev.practice.queue;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.clan.Clan;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.rank.RankAdapter;
import xyz.refinedev.practice.profile.rank.RankType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;

public class QueueThread extends Thread {

    public RankAdapter rank = Array.getInstance().getRankManager().getRankType().getRankAdapter();

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

                        if (firstPlayer == null) continue;

                        final Profile firstProfile = Profile.getByUuid(firstQueueProfile.getUuid());

                        for (QueueProfile secondQueueProfile : queue.getPlayers()) {
                            if (firstQueueProfile.equals(secondQueueProfile)) {
                                continue;
                            }

                            Player secondPlayer = Bukkit.getPlayer(secondQueueProfile.getUuid());
                            Profile secondProfile = Profile.getByUuid(secondQueueProfile.getUuid());

                            if (secondPlayer == null) continue;

                            //Ping factor code (either by nick or joe idk)
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
                                if (!firstQueueProfile.isInRange(secondQueueProfile.getElo()) || !secondQueueProfile.isInRange(firstQueueProfile.getElo())) {
                                    continue;
                                }
                            }

                            if (queue.getType() == QueueType.CLAN) {
                                Clan firstClan = firstProfile.getClan();
                                Clan secondClan = secondProfile.getClan();

                                if (firstClan == secondClan) continue;
                            }

                            if (Arena.getArenas().isEmpty()) continue;

                            // Find arena
                            arena = Arena.getRandom(queue.getKit());

                            if (arena == null || !arena.isSetup() || arena.isActive()) continue;

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
                            this.match = queue.getKit().createSoloKitMatch(queue, firstMatchPlayer, secondMatchPlayer, kit, arena, queue.getType());

                            for ( String string : Locale.MATCH_SOLO_STARTMESSAGE.toList() ) {
                                String opponentMessages = this.formatMessages(string, firstPlayer, secondPlayer, queue.getType());
                                firstPlayer.sendMessage(this.replaceOpponent(opponentMessages, firstPlayer));
                                secondPlayer.sendMessage(this.replaceOpponent(opponentMessages, secondPlayer));
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

    private String formatMessages(String string, Player sender, Player target, QueueType type) {
        Profile senderProfile = Profile.getByUuid(sender.getUniqueId());
        Profile targetProfile = Profile.getByUuid(target.getUniqueId());

        int senderELO = senderProfile.getStatisticsData().get(kit).getElo();
        int targetELO = targetProfile.getStatisticsData().get(kit).getElo();

        return string
                .replace("<ranked>", type == QueueType.RANKED ? "&aTrue" : "&cFalse")
                .replace("<player1_ping>", String.valueOf(PlayerUtil.getPing(sender)))
                .replace("<player2_ping>", String.valueOf(PlayerUtil.getPing(target)))
                .replace("<player1>", type == QueueType.RANKED ? senderProfile.getColouredName() + CC.GRAY + " (" + senderELO + ")" : senderProfile.getColouredName())
                .replace("<player2>", type == QueueType.RANKED ? targetProfile.getColouredName() + CC.GRAY + " (" + targetELO + ")" : targetProfile.getColouredName());
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
