package xyz.refinedev.practice.arena.rating;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.refinedev.practice.util.command.argument.CommandArg;
import xyz.refinedev.practice.util.command.exception.CommandExitMessage;
import xyz.refinedev.practice.util.command.parametric.DrinkProvider;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 10/8/2021
 * Project: Array
 */

public class RatingTypeProvider extends DrinkProvider<RatingType> {

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
    public RatingType provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        try {
            return RatingType.valueOf(name);
        } catch (Exception e) {
            throw new CommandExitMessage("A rating with that name does not exist!");
        }
    }

    @Override
    public String argumentDescription() {
        return "rating";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        final String finalPrefix = prefix;
        return Arrays.stream(RatingType.values()).map(RatingType::name).filter(s -> finalPrefix.length() == 0 || s.startsWith(finalPrefix)).collect(Collectors.toList());
    }
}
