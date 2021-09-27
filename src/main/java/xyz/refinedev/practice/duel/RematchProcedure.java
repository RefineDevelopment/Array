package xyz.refinedev.practice.duel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.hook.core.CoreAdapter;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.chat.Clickable;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.util.other.TaskUtil;

import java.util.UUID;

@Getter @Setter
@RequiredArgsConstructor
public class RematchProcedure {

    private final long timestamp = System.currentTimeMillis();
    private final Array plugin = Array.getInstance();
    private final CoreAdapter rank = plugin.getCoreHandler().getCoreType().getCoreAdapter();

    private final UUID key;
    private final UUID sender;
    private final UUID target;

    private final Kit kit;
    private final Arena arena;
    private Match match;

    private boolean sent;
    private boolean receive;

    public void request() {
        Player sender = plugin.getServer().getPlayer(this.sender);
        Player target = plugin.getServer().getPlayer(this.target);

        if (!sender.isOnline() || !target.isOnline()) return;

        Profile senderProfile = plugin.getProfileManager().getByUUID(sender.getUniqueId());
        Profile targetProfile = plugin.getProfileManager().getByUUID(target.getUniqueId());

        if (senderProfile.getRematchData() == null || targetProfile.getRematchData() == null || !senderProfile.getRematchData().getKey().equals(targetProfile.getRematchData().getKey())) {
            return;
        }

        if (senderProfile.isBusy()) {
            sender.sendMessage(Locale.ERROR_NOTABLE.toString());
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

        plugin.getProfileManager().checkForHotbarUpdate(senderProfile);
        plugin.getProfileManager().checkForHotbarUpdate(targetProfile);

        TaskUtil.runLaterAsync(() -> {
            plugin.getProfileManager().checkForHotbarUpdate(senderProfile);
            plugin.getProfileManager().checkForHotbarUpdate(targetProfile);
        }, 15 * 20);
    }

    public void accept() {
        Player sender = Array.getInstance().getServer().getPlayer(this.sender);
        Player target = Array.getInstance().getServer().getPlayer(this.target);

        if (sender == null || target == null || !sender.isOnline() || !target.isOnline()) {
            return;
        }

        Profile senderProfile = plugin.getProfileManager().getByUUID(sender.getUniqueId());
        Profile targetProfile = plugin.getProfileManager().getByUUID(target.getUniqueId());

        if (senderProfile.getRematchData() == null || targetProfile.getRematchData() == null || !senderProfile.getRematchData().getKey().equals(targetProfile.getRematchData().getKey())) {
            return;
        }

        if (senderProfile.isBusy()) {
            sender.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }

        if (targetProfile.isBusy()) {
            sender.sendMessage(Locale.ERROR_BUSY.toString());
            return;
        }

        Arena arena = this.arena;

        if (arena.isActive()) {
            arena = Arena.getRandom(kit);
            sender.sendMessage(CC.translate("&7The arena was not available, finding a new arena..."));
            target.sendMessage(CC.translate("&7The arena was not available, finding a new arena..."));
        }

        if (arena == null) {
            sender.sendMessage(CC.RED + "Tried to start a match but there were no available arenas.");
            return;
        }

        arena.setActive(true);


        match = kit.createSoloKitMatch(null, new TeamPlayer(sender), new TeamPlayer(target), kit, arena, QueueType.UNRANKED);

        for ( String string : Locale.MATCH_SOLO_STARTMESSAGE.toList() ) {
            String opponentMessages = this.formatMessages(string, sender, target);
            sender.sendMessage(replaceOpponent(opponentMessages, sender));
            target.sendMessage(replaceOpponent(opponentMessages, target));
        }
        TaskUtil.run(match::start);
    }

    private String formatMessages(String string, Player sender, Player target) {
        Profile senderProfile = plugin.getProfileManager().getByUUID(sender.getUniqueId());
        Profile targetProfile = plugin.getProfileManager().getByUUID(target.getUniqueId());

        return string
                .replace("<ranked>","&cFalse")
                .replace("<player1_ping>", String.valueOf(PlayerUtil.getPing(sender)))
                .replace("<player2_ping>", String.valueOf(PlayerUtil.getPing(target)))
                .replace("<player1>", plugin.getProfileManager().getColouredName(senderProfile)
                .replace("<player2>", plugin.getProfileManager().getColouredName(targetProfile)));
    }

    private String replaceOpponent(String opponent, Player player) {
        opponent = opponent
                .replace("<opponent_ping>", String.valueOf(PlayerUtil.getPing(match.getOpponentPlayer(player))))
                .replace("<player_ping>", String.valueOf(PlayerUtil.getPing(player)))
                .replace("<arena>", this.arena.getDisplayName())
                .replace("<kit>", this.kit.getDisplayName())
                .replace("<opponent>", match.getOpponentPlayer(player).getDisplayName())
                .replace("<player>", player.getDisplayName());
        return opponent;
    }

}
