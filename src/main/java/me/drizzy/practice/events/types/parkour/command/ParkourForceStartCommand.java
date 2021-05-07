package me.drizzy.practice.events.types.parkour.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;

@CommandMeta(label={"parkour forcestart"}, permission="array.staff")
public class ParkourForceStartCommand {
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getParkour() == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Parkour"));
            return;
        }
        profile.getParkour().onRound();
        player.sendMessage(CC.translate("&7Successfully force started the &cParkour Event&7!"));
    }
}
