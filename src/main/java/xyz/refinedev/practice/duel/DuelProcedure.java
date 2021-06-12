package xyz.refinedev.practice.duel;

import lombok.Data;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.rank.Rank;
import xyz.refinedev.practice.profile.rank.RankType;
import xyz.refinedev.practice.util.chat.Clickable;
import xyz.refinedev.practice.util.other.PlayerUtil;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.profile.Profile;
import org.bukkit.entity.Player;

@Data
public class DuelProcedure {

    public static RankType rank = Rank.getRankType();

    private final Player sender;
    private final Player target;
    private final boolean party;

    private Kit kit;
    private Arena arena;

    public void send() {
        if (!sender.isOnline() || !target.isOnline()) {
            return;
        }

        DuelRequest request = new DuelRequest(sender.getUniqueId(), party);
        request.setKit(kit);
        request.setArena(arena);

        Profile senderProfile = Profile.getByPlayer(sender);

        senderProfile.setDuelProcedure(null);
        senderProfile.getSentDuelRequests().put(target.getUniqueId(), request);

        sender.sendMessage(Locale.DUEL_SENT.toString()
                .replace("<target_name>", rank.getFullName(target))
                .replace("<target_ping>", String.valueOf(PlayerUtil.getPing(target)))
                .replace("<duel_kit>", request.getKit().getDisplayName())
                .replace("<duel_arena>", request.getArena().getDisplayName()));

        target.sendMessage(Locale.DUEL_RECEIVED.toString()
                .replace("<sender_name>", rank.getFullName(sender))
                .replace("<sender_ping>", String.valueOf(PlayerUtil.getPing(sender)))
                .replace("<duel_kit>", request.getKit().getDisplayName())
                .replace("<duel_arena>", request.getArena().getDisplayName()));

        Clickable clickable = new Clickable(Locale.DUEL_ACCEPT.toString(), Locale.DUEL_HOVER.toString(), "/duel accept " + sender.getName());
        clickable.sendToPlayer(target);

    }

}
