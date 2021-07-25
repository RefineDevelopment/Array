package xyz.refinedev.practice.duel;

import lombok.Data;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.api.ArrayCache;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.profile.rank.Rank;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.match.types.SoloMatch;
import xyz.refinedev.practice.match.types.kit.BridgeMatch;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.profile.rank.RankType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chat.Clickable;
import xyz.refinedev.practice.util.other.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class RematchProcedure {

    private final long timestamp = System.currentTimeMillis();
    public static RankType rank = Rank.getRankType();

    private final UUID key;
    private final UUID sender;
    private final UUID target;

    private final Kit kit;
    private final Arena arena;
    private Match match;

    private boolean sent;
    private boolean receive;


    public void request() {

        Player sender = Array.getInstance().getServer().getPlayer(this.sender);
        Player target = Array.getInstance().getServer().getPlayer(this.target);

        if (!sender.isOnline() || !target.isOnline()) {
            return;
        }

        Profile senderProfile = Profile.getByUuid(sender.getUniqueId());
        Profile targetProfile = Profile.getByUuid(target.getUniqueId());

        if (senderProfile.getRematchData() == null || targetProfile.getRematchData() == null ||
                !senderProfile.getRematchData().getKey().equals(targetProfile.getRematchData().getKey())) {
            return;
        }

        if (senderProfile.isBusy()) {
            sender.sendMessage(CC.RED + "You cannot duel right now.");
            return;
        }

        sender.sendMessage(Locale.DUEL_SENT.toString()
                .replace("<target_name>", rank.getFullName(target))
                .replace("<target_ping>", String.valueOf(PlayerUtil.getPing(target)))
                .replace("<duel_kit>", getKit().getDisplayName())
                .replace("<duel_arena>", getArena().getDisplayName()));

        target.sendMessage(Locale.DUEL_RECEIVED.toString()
                .replace("<sender_name>", rank.getFullName(sender))
                .replace("<sender_ping>", String.valueOf(PlayerUtil.getPing(sender)))
                .replace("<duel_kit>", getKit().getDisplayName())
                .replace("<duel_arena>", getArena().getDisplayName()));

        Clickable clickable = new Clickable(Locale.DUEL_ACCEPT.toString(), Locale.DUEL_HOVER.toString(), "/rematch");
        clickable.sendToPlayer(target);

        this.sent = true;
        targetProfile.getRematchData().receive = true;

        senderProfile.checkForHotbarUpdate();
        targetProfile.checkForHotbarUpdate();

        Bukkit.getScheduler().runTaskLaterAsynchronously(Array.getInstance(), () -> {
            senderProfile.checkForHotbarUpdate();
            targetProfile.checkForHotbarUpdate();
        }, 15 * 20);
    }

    public void accept() {
        Player sender = Array.getInstance().getServer().getPlayer(this.sender);
        Player target = Array.getInstance().getServer().getPlayer(this.target);

        if (sender == null || target == null || !sender.isOnline() || !target.isOnline()) {
            return;
        }

        Profile senderProfile = Profile.getByUuid(sender.getUniqueId());
        Profile targetProfile = Profile.getByUuid(target.getUniqueId());

        if (senderProfile.getRematchData() == null || targetProfile.getRematchData() == null ||
                !senderProfile.getRematchData().getKey().equals(targetProfile.getRematchData().getKey())) {
            return;
        }

        if (senderProfile.isBusy()) {
            sender.sendMessage(CC.RED + "You cannot duel right now.");
            return;
        }

        if (targetProfile.isBusy()) {
            sender.sendMessage(CC.translate(CC.RED + target.getDisplayName()) + CC.RED + " is currently busy.");
            return;
        }

        Arena arena = this.arena;

        if (arena.isActive()) {
            arena = Arena.getRandom(kit);
            sender.sendMessage(CC.translate("&7The arena was not available, finding a new arena..."));
            target.sendMessage(CC.translate("&7The arena was not available, finding a new arena..."));
        }

        if (arena == null) {
            sender.sendMessage(CC.RED + "Tried to start a match but there are no available arenas.");
            return;
        }

        arena.setActive(true);

        if (getKit().getGameRules().isBridge()) {
            match = new BridgeMatch(null, new TeamPlayer(sender), new TeamPlayer(target), getKit(), arena,
                    QueueType.UNRANKED);
        } else {
            match = new SoloMatch(null, new TeamPlayer(sender), new TeamPlayer(target), getKit(), arena,
                    QueueType.UNRANKED);
        }
        for ( String string : Locale.MATCH_SOLO_STARTMESSAGE.toList() ) {
            String opponentMessages = this.formatMessages(string, rank.getFullName(sender), rank.getFullName(target), senderProfile.getStatisticsData().get(kit).getElo(), targetProfile.getStatisticsData().get(kit).getElo(), QueueType.UNRANKED);
            sender.sendMessage(replaceOpponent(opponentMessages, sender));
            target.sendMessage(replaceOpponent(opponentMessages, target));
        }
        match.start();
    }

    private String formatMessages(String string, String player1, String player2, int player1Elo, int player2Elo, QueueType type) {
        return string
                .replace("<player1>", type == QueueType.RANKED ? player1 + CC.GRAY + " (" + player1Elo + ")" : player1)
                .replace("<ranked>", type == QueueType.RANKED ? "&aYes" : "&cNo")
                .replace("<player1_ping>", String.valueOf(PlayerUtil.getPing(Bukkit.getPlayer(ArrayCache.getUUID(player1)))))
                .replace("<player2_ping>", String.valueOf(PlayerUtil.getPing(Bukkit.getPlayer(ArrayCache.getUUID(player2)))))
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
