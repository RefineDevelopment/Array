package me.drizzy.practice.duel;

import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.external.ChatComponentBuilder;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

public class DuelProcedure {

    @Getter
    private final boolean party;
    @Getter
    private final Player sender;
    @Getter
    private final Player target;
    @Getter
    @Setter
    private Kit kit;
    @Getter
    @Setter
    private Arena arena;

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

        Profile senderProfile = Profile.getByUuid(sender.getUniqueId());
        senderProfile.setDuelProcedure(null);
        senderProfile.getSentDuelRequests().put(target.getUniqueId(), request);

        this.sender.sendMessage(CC.translate("&8[&b&lDuel&8] &fYou sent a duel request to &b" + this.target.getName() + "&f with kit &b" + (this.kit.getName())));
        this.target.sendMessage(CC.translate("&8[&b&lDuel&8] &b" + this.sender.getName() + " &fhas sent you a duel request with kit &b" + (this.kit.getName())));
        target.spigot().sendMessage(new ChatComponentBuilder("")
                .parse("&a(Click to accept)")
                .attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + sender.getName()))
                .attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder(CC.GREEN + "Click to accept this duel invite.").create()))
                .create());
    }

}
