package me.drizzy.practice.events.types.spleef.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;

@CommandMeta(label = {"spleef forcestart"}, permission="array.staff")
public class SpleefForceStartCommand {
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getSpleef() == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Spleef"));
            return;
        }
        profile.getSpleef().onRound();
        player.sendMessage(CC.translate("&7Successfully force started the &cSpleef Event&7!"));
    }
}
