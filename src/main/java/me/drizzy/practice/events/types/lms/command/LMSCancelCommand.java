package me.drizzy.practice.events.types.lms.command;

import me.drizzy.practice.Array;
import me.drizzy.practice.Locale;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "lms cancel", permission = "array.staff")
public class LMSCancelCommand {

    public void execute(CommandSender sender) {
        if (Array.getInstance().getLMSManager().getActiveLMS() == null) {
            sender.sendMessage(Locale.ERROR_NOTACTIVE.toString().replace("<event>", "LMS"));
            return;
        }
        Profile.getProfiles().values().stream().filter(profile -> !profile.getKitEditor().isActive()).filter(Profile::isInLobby).forEach(Profile::refreshHotbar);
        Array.getInstance().getLMSManager().getActiveLMS().end();
    }

}
