package xyz.refinedev.practice.arena.meta;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.refinedev.practice.arena.ArenaType;
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
 * Created at 7/12/2021
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
            RatingType type = RatingType.valueOf(name);
            return type;
        } catch (Exception e) {
           throw new CommandExitMessage("A Rating with that name does not exist!");
        }
    }

    @Override
    public String argumentDescription() {
        return "rating";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        return Arrays.stream(RatingType.values()).map(Enum::name).collect(Collectors.toList());
    }
}
