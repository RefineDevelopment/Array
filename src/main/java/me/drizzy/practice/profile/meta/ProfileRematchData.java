package me.drizzy.practice.profile.meta;

import me.drizzy.practice.Array;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.match.types.SoloMatch;
import me.drizzy.practice.match.types.SumoMatch;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.queue.QueueType;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.external.ChatComponentBuilder;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class ProfileRematchData {

    private final UUID key;
    private final UUID sender;
    private final UUID target;
    @Setter
    private Kit kit;
    @Setter
    private Arena arena;
    @Setter
    private boolean sent;
    @Setter
    private boolean receive;
    private final long timestamp = System.currentTimeMillis();

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

        if (senderProfile.isBusy(sender)) {
            sender.sendMessage(CC.RED + "You cannot duel right now.");
            return;
        }

        sender.sendMessage(CC.translate("&7You sent a rematch request to &c" + target.getName() + " &7with kit &c" +
                kit.getName() + "&7."));
        target.sendMessage(CC.translate("&c" + sender.getName() + " &7has sent you a rematch request with kit &c" +
                kit.getName() + "&7."));

        target.spigot().sendMessage(new ChatComponentBuilder("")
                .parse("&a(Click to accept)")
                .attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("")
                        .parse("&aClick to accept this rematch invite.").create()))
                .attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rematch"))
                .create());

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

        if (senderProfile.isBusy(sender)) {
            sender.sendMessage(CC.RED + "You cannot duel right now.");
            return;
        }

        if (targetProfile.isBusy(target)) {
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
        Match match;

        if(kit.getGameRules().isSumo()) {
            match = new SumoMatch(null, new TeamPlayer(sender), new TeamPlayer(target), kit, arena, QueueType.UNRANKED);
        } else {
            match = new SoloMatch(null, new TeamPlayer(sender), new TeamPlayer(target), kit, arena, QueueType.UNRANKED,0,0);
        }
        match.start();
    }

}
