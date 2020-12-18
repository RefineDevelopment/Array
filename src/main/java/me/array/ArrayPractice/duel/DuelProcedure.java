

package me.array.ArrayPractice.duel;

import me.array.ArrayPractice.kit.Kit;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import me.array.ArrayPractice.util.external.ChatComponentBuilder;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.arena.Arena;
import org.bukkit.entity.Player;

public class DuelProcedure
{
    private Player sender;
    private Player target;
    private final boolean party;
    private Kit kit;
    private Arena arena;
    
    public DuelProcedure(final Player sender, final Player target, final boolean party) {
        this.sender = sender;
        this.target = target;
        this.party = party;
    }
    
    public void send() {
        if (!this.sender.isOnline() || !this.target.isOnline()) {
            return;
        }
        final DuelRequest request = new DuelRequest(this.sender.getUniqueId(), this.party);
        request.setKit(this.kit);
        request.setArena(this.arena);
        final Profile senderProfile = Profile.getByUuid(this.sender.getUniqueId());
        senderProfile.setDuelProcedure(null);
        senderProfile.getSentDuelRequests().put(this.target.getUniqueId(), request);
        this.sender.sendMessage(CC.translate("&e&l(Duel) &fYou sent a duel request to &b" + this.target.getName() + "&f with kit &b" + (this.kit.getName())));
        this.target.sendMessage(CC.translate("&e&l(Duel) &b" + this.sender.getName() + " &ehas sent you a duel request with kit &b" + (this.kit.getName())));
        this.target.spigot().sendMessage(new ChatComponentBuilder("").parse("&a&l(Duel) &a(Click to accept)").attachToEachPart(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + this.sender.getName())).attachToEachPart(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentBuilder(CC.AQUA + "Click to accept this duel invite.").create())).create());
    }
    
    public Player getSender() {
        return this.sender;
    }
    
    public Player getTarget() {
        return this.target;
    }
    
    public boolean isParty() {
        return this.party;
    }
    
    public Kit getKit() {
        return this.kit;
    }
    
    public void setKit(final Kit kit) {
        this.kit = kit;
    }
    
    public Arena getArena() {
        return this.arena;
    }
    
    public void setArena(final Arena arena) {
        this.arena = arena;
    }
}
