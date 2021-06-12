package xyz.refinedev.practice.kit;

import xyz.refinedev.practice.util.command.argument.CommandArg;
import xyz.refinedev.practice.util.command.exception.CommandExitMessage;
import xyz.refinedev.practice.util.command.parametric.DrinkProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class KitProvider extends DrinkProvider<Kit> {

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
    public Kit defaultNullValue() {
        return null; // The value to use when the arg.next() value is null (before it gets passed to #provide()) (when #allowNullArgument() is true)
    }

    @Nullable
    @Override
    public Kit provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        Kit kit = Kit.getByName(name);
        if (kit != null) {
            return kit;
        }
        throw new CommandExitMessage("A Kit with that name does not exist.");
    }

    @Override
    public String argumentDescription() {
        return "kit";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        return Kit.getKits().stream().map(Kit::getName).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
