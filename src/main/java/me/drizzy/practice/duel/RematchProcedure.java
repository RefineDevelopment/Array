package me.drizzy.practice.duel;

import lombok.Data;
import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.api.ArrayCache;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.enums.QueueType;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.match.types.SoloMatch;
import me.drizzy.practice.match.types.TheBridgeMatch;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.profile.rank.RankType;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.chat.Clickable;
import me.drizzy.practice.util.other.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class RematchProcedure {

    private final long timestamp = System.currentTimeMillis();
    public static RankType rank = Array.getInstance().getRankManager();

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

        target.sendMessage(Locale.DUEL_RECIEVED.toString()
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
            match = new TheBridgeMatch(null, new TeamPlayer(sender), new TeamPlayer(target), getKit(), arena,
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
