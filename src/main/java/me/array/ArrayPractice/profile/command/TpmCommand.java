package me.array.ArrayPractice.profile.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.util.external.CC;
import me.activated.core.data.other.systems.MessageSystem;
import me.activated.core.plugin.AquaCoreAPI;
import org.bukkit.entity.Player;

@CommandMeta(label = {"tpm", "togglepm", "togglemsg", "msgtoggle"})
public class TpmCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        boolean state = profile.getOptions().isPrivateMessages();
        MessageSystem messages = AquaCoreAPI.INSTANCE.getGlobalPlayer(player.getUniqueId()).getMessageSystem();
        if (messages.isMessagesToggled()) {
            messages.setMessagesToggled(false);
        } else if (!messages.isMessagesToggled()) {
            messages.setMessagesToggled(true);
        }
        player.sendMessage(CC.translate("&7Private Messages: " + (messages.isMessagesToggled() ? "&aOn" : "&cOff")));
        profile.getOptions().setPrivateMessages(!state);
    }

}
