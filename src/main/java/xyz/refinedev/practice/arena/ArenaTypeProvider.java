package xyz.refinedev.practice.arena;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import xyz.refinedev.practice.util.command.argument.CommandArg;
import xyz.refinedev.practice.util.command.exception.CommandExitMessage;
import xyz.refinedev.practice.util.command.parametric.DrinkProvider;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArenaTypeProvider extends DrinkProvider<ArenaType> {

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public boolean allowNullArgument() {
        return false; // Should this provider allow a null argument (the value for arg.next() in #provide()) (i.e when this is optional or in a flag)
    }

    @Nullable
    @Override
    public ArenaType defaultNullValue() {
        return ArenaType.SHARED; // The value to use when the arg.next() value is null (before it gets passed to #provide()) (when #allowNullArgument() is true)
    }

    @Nullable
    @Override
    public ArenaType provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        try {
            return ArenaType.valueOf(name.toUpperCase());
        } catch (Exception e) {
            throw new CommandExitMessage("No Arena Type with the name '" + name + "'.");
        }

    }

    @Override
    public String argumentDescription() {
        return "arena-type";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        final String finalPrefix = prefix;
        return Arrays.stream(ArenaType.values()).map(ArenaType::name).filter(s -> finalPrefix.length() == 0 || s.startsWith(finalPrefix)).collect(Collectors.toList());
    }

}

