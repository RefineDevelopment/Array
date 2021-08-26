package xyz.refinedev.practice.cmds.standalone;

import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.meta.Rating;
import xyz.refinedev.practice.arena.meta.RatingType;
import xyz.refinedev.practice.managers.RatingsManager;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.command.annotation.Command;
import xyz.refinedev.practice.util.command.annotation.Sender;

/**
 * This Project is property of Purge Community © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 6/4/2021
 * Project: Array
 */

public class RateCommand {

    @Command(name = "", desc = "Rate an Arena", usage = "<arena> <rating>")
    public void rate(@Sender Player player, Arena arena, RatingType ratingType) {
        Profile profile = Profile.getByPlayer(player);

        if (!profile.isCanIssueRating()) {
            player.sendMessage(Locale.ERROR_NOTABLE.toString());
            return;
        }

        if (!profile.getRatingArena().getName().equalsIgnoreCase(arena.getName())) {
            player.sendMessage(CC.translate("&7Invalid Arena!"));
            return;
        }

        Rating rating = arena.getRating();
        rating.recordVote(ratingType);

        RatingsManager ratingsManager = Array.getInstance().getRatingsManager();
        ratingsManager.save();

        player.sendMessage(Locale.MATCH_RATING.toString());
        profile.setCanIssueRating(false);
    }
}
