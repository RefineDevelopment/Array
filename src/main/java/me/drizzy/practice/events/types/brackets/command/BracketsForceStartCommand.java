package me.drizzy.practice.events.types.brackets.command;

import me.drizzy.practice.Locale;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import me.drizzy.practice.profile.Profile;
import org.bukkit.entity.Player;

@CommandMeta(label="brackets forcestart", permission="array.staff")
public class BracketsForceStartCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        if (profile.getBrackets() == null) {
            player.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "Brackets"));
            return;
        }
        profile.getBrackets().onRound();
        player.sendMessage(CC.translate("&7Successfully force started the &cBrackets Event&7!"));
    }
}
