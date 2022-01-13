package xyz.refinedev.practice.arena;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import xyz.refinedev.practice.util.command.argument.CommandArg;
import xyz.refinedev.practice.util.command.exception.CommandExitMessage;
import xyz.refinedev.practice.util.command.parametric.DrinkProvider;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class ArenaProvider extends DrinkProvider<Arena> {

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
    public Arena defaultNullValue() {
        return null; // The value to use when the arg.next() value is null (before it gets passed to #provide()) (when #allowNullArgument() is true)
    }

    @Nullable
    @Override
    public Arena provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        Arena arena = this.getPlugin().getArenaManager().getByName(name);
        if (arena != null) {
            return arena;
        }
        throw new CommandExitMessage("No Arena found with the name '" + name + "'.");
    }

    @Override
    public String argumentDescription() {
        return "arena";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        final String finalPrefix = prefix;
        return this.getPlugin().getArenaManager().getArenas().stream().filter(arena -> !arena.isDuplicate()).map(Arena::getName).filter(s -> finalPrefix.length() == 0 || s.startsWith(finalPrefix)).collect(Collectors.toList());
    }
}

