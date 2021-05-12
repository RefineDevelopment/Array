package me.drizzy.practice.events.types.sumo.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "sumo forcestart", permission="array.staff")
public class SumoForceStartCommand {
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getSumo() == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Sumo"));
            return;
        }
        profile.getSumo().onRound();
        player.sendMessage(CC.translate("&7Successfully force started the &cSumo Event&7!"));
    }
}
