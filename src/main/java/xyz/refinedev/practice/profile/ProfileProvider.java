package xyz.refinedev.practice.profile;

import xyz.refinedev.practice.api.ArrayCache;
import xyz.refinedev.practice.util.command.argument.CommandArg;
import xyz.refinedev.practice.util.command.exception.CommandExitMessage;
import xyz.refinedev.practice.util.command.parametric.DrinkProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.List;
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
    public Profile provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        Profile profile = Profile.getByUuid(ArrayCache.getUUID(name));

        if (profile != null && ArrayCache.getUUID(name) != null) {
            return profile;
        }

        throw new CommandExitMessage("A profile with that name does not exist!");
    }

    @Override
    public String argumentDescription() {
        return "profile";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        final String finalPrefix = prefix;
        return Profile.getProfiles().values().stream().map(Profile::getName).filter(s -> finalPrefix.length() == 0 || s.startsWith(finalPrefix)).collect(Collectors.toList());
    }
}
