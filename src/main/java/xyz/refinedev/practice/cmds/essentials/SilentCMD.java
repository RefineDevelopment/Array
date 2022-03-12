package xyz.refinedev.practice.cmds.essentials;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.managers.ProfileManager;
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
public class SilentCMD {

    private final Array plugin;

    @Command(name = "", desc = "Toggle silent mode for your profile")
    @Require("array.profile.silent")
    public void build(@Sender Player player) {
        ProfileManager profileManager = plugin.getProfileManager();
        Profile profile = profileManager.getProfile(player.getUniqueId());
        profile.setSilent(!profile.isSilent());

        player.sendMessage(CC.translate(profile.isSilent() ? "&aEnabled Silent-Mode for your profile." : "&cDisabled Silent-Mode for your profile."));
    }
}
