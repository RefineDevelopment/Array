

package me.array.ArrayPractice.profile.meta;

import me.array.ArrayPractice.Array;
import me.array.ArrayPractice.arena.Arena;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.match.impl.SoloMatch;
import me.array.ArrayPractice.match.team.TeamPlayer;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.queue.QueueType;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ChatComponentBuilder;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

import java.util.UUID;

public class ProfileRematchData
{
    private UUID key;
    private UUID sender;
    private UUID target;
    private Kit kit;
    private Arena arena;
    private boolean sent;
    private boolean receive;
    private long timestamp;
    
    public ProfileRematchData(final UUID key, final UUID sender, final UUID target, final Kit kit, final Arena arena) {
        this.timestamp = System.currentTimeMillis();
        this.key = key;
        this.sender = sender;
        this.target = target;
        this.kit = kit;
        this.arena = arena;
    }
    
    public void request() {
        final Player sender = Array.get().getServer().getPlayer(this.sender);
        final Player target = Array.get().getServer().getPlayer(this.target);
        if (sender == null || target == null) {
            return;
        }
        final Profile senderProfile = Profile.getByUuid(sender.getUniqueId());
        final Profile targetProfile = Profile.getByUuid(target.getUniqueId());
        if (senderProfile.getRematchData() == null || targetProfile.getRematchData() == null || !senderProfile.getRematchData().getKey().equals(targetProfile.getRematchData().getKey())) {
            return;
        }
        if (senderProfile.isBusy(sender)) {
            sender.sendMessage(CC.RED + "You cannot duel right now.");
            return;
        }
        sender.sendMessage(CC.translate("&a&l(Rematch) &fYou sent a rematch request to &b" + target.getName() + " &fwith kit &b" + this.kit.getName() + "&f."));
        target.sendMessage(CC.translate("&a&l(Rematch) &b" + sender.getName() + " &fhas sent you a rematch request with kit &b" + this.kit.getName() + "&f."));
        target.spigot().sendMessage(new ChatComponentBuilder("").parse("&a&l(Rematch) &a(Click to accept)").attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder("").parse("&aClick to accept this rematch invite.").create())).attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rematch")).create());
        this.sent = true;
        targetProfile.getRematchData().receive = true;
        senderProfile.checkForHotbarUpdate();
        targetProfile.checkForHotbarUpdate();
    }
    
    public void accept() {
        final Player sender = Array.get().getServer().getPlayer(this.sender);
        final Player target = Array.get().getServer().getPlayer(this.target);
        if (sender == null || target == null || !sender.isOnline() || !target.isOnline()) {
            return;
        }
        final Profile senderProfile = Profile.getByUuid(sender.getUniqueId());
        final Profile targetProfile = Profile.getByUuid(target.getUniqueId());
        if (senderProfile.getRematchData() == null || targetProfile.getRematchData() == null || !senderProfile.getRematchData().getKey().equals(targetProfile.getRematchData().getKey())) {
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
            arena = Arena.getRandom(this.kit);
        }
        if (arena == null) {
            sender.sendMessage(CC.RED + "Tried to start a match but there are no available arenas.");
            return;
        }
        arena.setActive(true);
        final Match match = new SoloMatch(null, new TeamPlayer(sender), new TeamPlayer(target), this.kit, arena, QueueType.UNRANKED);
        match.start();
    }
    
    public UUID getKey() {
        return this.key;
    }
    
    public UUID getSender() {
        return this.sender;
    }
    
    public UUID getTarget() {
        return this.target;
    }
    
    public Kit getKit() {
        return this.kit;
    }
    
    public Arena getArena() {
        return this.arena;
    }
    
    public boolean isSent() {
        return this.sent;
    }
    
    public boolean isReceive() {
        return this.receive;
    }
    
    public long getTimestamp() {
        return this.timestamp;
    }
}
