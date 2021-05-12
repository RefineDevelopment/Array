package me.drizzy.practice.profile.meta;

import lombok.Getter;
import lombok.Setter;
import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.enums.QueueType;
import me.drizzy.practice.kit.Kit;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ProfileRematchData {

    private final UUID key;
    private final UUID sender;
    private final UUID target;
    private Kit kit;
    private Arena arena;
    private boolean sent;
    private boolean receive;
    private final long timestamp = System.currentTimeMillis();
    public static RankType rank = Array.getInstance().getRankManager();

    public ProfileRematchData(UUID key, UUID sender, UUID target, Kit kit, Arena arena) {
        this.key = key;
        this.sender = sender;
        this.target = target;
        this.kit = kit;
        this.arena = arena;
    }

    public void request() {

        Player sender = Array.getInstance().getServer().getPlayer(this.sender);
        Player target = Array.getInstance().getServer().getPlayer(this.target);

        if (sender == null || target == null) {
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

        List<String> strings = new ArrayList<>();

        strings.add(Locale.DUEL_RECIEVED.toString()
                .replace("<sender_name>", rank.getFullName(sender))
                .replace("<sender_ping>", String.valueOf(PlayerUtil.getPing(sender)))
                .replace("<duel_kit>", getKit().getDisplayName())
                .replace("<duel_arena>", getArena().getDisplayName()));

        strings.add(Locale.DUEL_ACCEPT.toString());

        for ( String string : strings ) {
            Clickable clickable = new Clickable(string, Locale.DUEL_HOVER.toString(),"/rematch");
            clickable.sendToPlayer(target);
        }

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
        }

        if (arena == null) {
            sender.sendMessage(CC.RED + "Tried to start a match but there are no available arenas.");
            return;
        }

        arena.setActive(true);

        if (getKit().getGameRules().isBridge()) {
            new TheBridgeMatch(null, new TeamPlayer(sender), new TeamPlayer(target), getKit(), arena,
                    QueueType.UNRANKED);
        } else {
            new SoloMatch(null, new TeamPlayer(sender), new TeamPlayer(target), getKit(), arena,
                    QueueType.UNRANKED);
        }
    }

}
