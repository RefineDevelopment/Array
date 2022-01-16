package xyz.refinedev.practice.cmds.standalone;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.event.Event;
import xyz.refinedev.practice.match.Match;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/30/2021
 * Project: Array
 */

@RequiredArgsConstructor
public class StopSpecCommand {

    private final Array plugin;

    @Command(name = "", desc = "Stop spectating")
    public void stopSpec(@Sender Player player) {
        Profile profile = plugin.getProfileManager().getProfileByUUID(player.getUniqueId());
        if (!profile.isSpectating()) {
            player.sendMessage(Locale.ERROR_NOTSPECTATING.toString());
            return;
        }

        profile.setSpectating(null);

        if (profile.isInMatch()) {
            Match match = profile.getMatch();
            this.plugin.getMatchManager().removeSpectator(match, player);
        } else if (profile.isInEvent()) {
            Event event = this.plugin.getEventManager().getEventByUUID(profile.getEvent());
            this.plugin.getEventManager().removeSpectator(event, player.getUniqueId());
        }

    }
}
