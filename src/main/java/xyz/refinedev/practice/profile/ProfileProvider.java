package xyz.refinedev.practice.profile;

import xyz.refinedev.practice.util.command.argument.CommandArg;
import xyz.refinedev.practice.util.command.exception.CommandExitMessage;
import xyz.refinedev.practice.util.command.parametric.DrinkProvider;
import xyz.refinedev.practice.util.other.PlayerUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/10/2021
 * Project: Array
 */

public class ProfileProvider extends DrinkProvider<Profile> {

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Nullable
    @Override
    public Profile provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        UUID uuid = PlayerUtil.getUUIDByName(name);

        if (uuid != null) {
            return this.getPlugin().getProfileManager().getProfile(uuid);
        }

        throw new CommandExitMessage("A profile with that name does not exist!");
    }

    @Override
    public String argumentDescription() {
        return "profile";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        final String finalPrefix = prefix;
        return this.getPlugin().getProfileManager().getProfiles().values().stream().map(Profile::getName).filter(s -> finalPrefix.length() == 0 || s.startsWith(finalPrefix)).collect(Collectors.toList());
    }
}
