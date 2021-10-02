package xyz.refinedev.practice.duel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.kit.Kit;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.hook.core.CoreAdapter;
import xyz.refinedev.practice.util.chat.Clickable;
import xyz.refinedev.practice.util.other.PlayerUtil;

@Getter @Setter
@RequiredArgsConstructor
public class DuelProcedure {

    private final Array plugin = Array.getInstance();
    private final CoreAdapter rank = plugin.getCoreHandler().getCoreType().getCoreAdapter();

    private final Player sender;
    private final Player target;
    private final boolean party;

    private Kit kit;
    private Arena arena;

    /**
     * Send the duel request to the target
     */
    public void send() {
        if (!sender.isOnline() || !target.isOnline()) {
            return;
        }

        DuelRequest request = new DuelRequest(sender.getUniqueId(), party);
        request.setKit(kit);
        request.setArena(arena);

        Profile senderProfile = plugin.getProfileManager().getByPlayer(sender);

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
