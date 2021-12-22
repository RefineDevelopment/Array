package xyz.refinedev.practice.queue;

import lombok.RequiredArgsConstructor;
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
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;

@RequiredArgsConstructor
public class QueueThread extends Thread {

    private final Array plugin;

    private Arena arena;
    private Kit kit;
    private Match match;

    @Override
    public void run() {
        while (true) {
            try {
                for (Queue queue : plugin.getQueueManager().getQueues()) {
                    queue.getPlayers().forEach(QueueProfile::tickRange);

                    if (queue.getPlayers().size() < 2) {
                        continue;
                    }

                    for (QueueProfile firstQueueProfile : queue.getPlayers()) {
                        Player firstPlayer = Bukkit.getPlayer(firstQueueProfile.getUniqueId());

                        if (firstPlayer == null) continue;

                        Profile firstProfile = plugin.getProfileManager().getByUUID(firstQueueProfile.getUniqueId());

                        for (QueueProfile secondQueueProfile : queue.getPlayers()) {
                            if (firstQueueProfile.equals(secondQueueProfile)) continue;

                            Player secondPlayer = Bukkit.getPlayer(secondQueueProfile.getUniqueId());
                            Profile secondProfile = plugin.getProfileManager().getByUUID(secondQueueProfile.getUniqueId());

                            if (secondPlayer == null) continue;

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

                            if (plugin.getArenaManager().getArenas().isEmpty()) {
                                firstPlayer.sendMessage(CC.translate("&cThere are no arenas setup!"));
                                secondPlayer.sendMessage(CC.translate("&cThere are no arenas setup!"));
                                continue;
                            }

                            // Find arena
                            arena = plugin.getArenaManager().getByKit(queue.getKit());
                            if (arena == null || !arena.isSetup() || arena.isActive()) continue;

                            if (queue.getKit().getGameRules().isBuild()) arena.setActive(true);

                            // Remove players from queue
                            queue.getPlayers().remove(firstQueueProfile);
                            queue.getPlayers().remove(secondQueueProfile);

                            TeamPlayer firstMatchPlayer = new TeamPlayer(firstPlayer);
                            TeamPlayer secondMatchPlayer = new TeamPlayer(secondPlayer);

                            if (queue.getType() == QueueType.RANKED) {
                                firstMatchPlayer.setElo(firstProfile.getStatisticsData().get(queue.getKit()).getElo());
                                secondMatchPlayer.setElo(secondProfile.getStatisticsData().get(queue.getKit()).getElo());

                                plugin.getProfileManager().calculateGlobalElo(secondProfile);
                                plugin.getProfileManager().calculateGlobalElo(firstProfile);
                            }

                            kit = queue.getKit();

                            // Create match
                            this.match = plugin.getMatchManager().createSoloKitMatch(queue, firstMatchPlayer, secondMatchPlayer, kit, arena, queue.getType());

                            for ( String string : Locale.MATCH_SOLO_STARTMESSAGE.toList() ) {
                                String opponentMessages = this.formatMessages(string, firstPlayer, secondPlayer, queue.getType());
                                firstPlayer.sendMessage(this.replaceOpponent(opponentMessages, firstPlayer));
                                secondPlayer.sendMessage(this.replaceOpponent(opponentMessages, secondPlayer));
                            }
                            TaskUtil.run(() -> this.plugin.getMatchManager().start(match));
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
        Profile senderProfile = plugin.getProfileManager().getByUUID(sender.getUniqueId());
        Profile targetProfile = plugin.getProfileManager().getByUUID(target.getUniqueId());

        int senderELO = senderProfile.getStatisticsData().get(kit).getElo();
        int targetELO = targetProfile.getStatisticsData().get(kit).getElo();

        String senderName = plugin.getProfileManager().getColouredName(senderProfile);
        String targetName = plugin.getProfileManager().getColouredName(targetProfile);

        return string
                .replace("<ranked>", type == QueueType.RANKED ? "&aTrue" : "&cFalse")
                .replace("<player1_ping>", String.valueOf(PlayerUtil.getPing(sender)))
                .replace("<player2_ping>", String.valueOf(PlayerUtil.getPing(target)))
                .replace("<player1>", type == QueueType.RANKED ? senderName + CC.GRAY + " (" + senderELO + ")" : senderName)
                .replace("<player2>", type == QueueType.RANKED ? targetName + CC.GRAY + " (" + targetELO + ")" : targetName);
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
