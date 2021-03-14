package me.drizzy.practice.event.types.wizard.command;

import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label="wizard forcestart", permission="wizard.forcestart")
public class WizardForceStartCommand {
    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.getWizard().onRound();
    }
}
