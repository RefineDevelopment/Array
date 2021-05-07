package me.drizzy.practice.events.types.gulag.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label="gulag forcestart", permission="array.staff")
public class GulagForceStartCommand {
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getGulag() == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Gulag"));
            return;
        }
        profile.getGulag().onRound();
        player.sendMessage(CC.translate("&7Successfully force started the &cGulag Event&7!"));
    }
}
