package me.drizzy.practice.duel;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.profile.rank.Rank;
import me.drizzy.practice.profile.rank.RankType;
import me.drizzy.practice.util.chat.Clickable;
import me.drizzy.practice.util.other.PlayerUtil;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.ChatComponentBuilder;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DuelProcedure {

    private final boolean party;
    private final Player sender;
    private final Player target;
    private Kit kit;
    private Arena arena;
    public static RankType rank = Array.getInstance().getRankManager();

    public DuelProcedure(Player sender, Player target, boolean party) {
        this.sender = sender;
        this.target = target;
        this.party = party;
    }

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

        List<String> strings = new ArrayList<>();

        strings.add(Locale.DUEL_RECIEVED.toString()
                .replace("<sender_name>", rank.getFullName(sender))
                .replace("<sender_ping>", String.valueOf(PlayerUtil.getPing(sender)))
                .replace("<duel_kit>", request.getKit().getDisplayName())
                .replace("<duel_arena>", request.getArena().getDisplayName()));

        strings.add(Locale.DUEL_ACCEPT.toString());

        for ( String string : strings ) {
            Clickable clickable = new Clickable(string, Locale.DUEL_HOVER.toString(),"/duel accept " + sender.getName());
            clickable.sendToPlayer(target);
        }
    }

}
