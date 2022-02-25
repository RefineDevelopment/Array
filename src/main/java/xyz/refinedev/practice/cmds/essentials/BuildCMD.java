package xyz.refinedev.practice.cmds.essentials;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Require;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/17/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class BuildCMD {

    private final Array plugin;

    @Command(name = "", desc = "Toggle build mode for your profile")
    @Require("array.profile.build")
    public void build(@Sender Player player) {
        Profile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        profile.setBuild(!profile.isBuild());

        player.sendMessage(CC.translate(profile.isBuild() ? "&aEnabled Build-Mode for your profile." : "&cDisabled Build-Mode for your profile."));
    }
}
