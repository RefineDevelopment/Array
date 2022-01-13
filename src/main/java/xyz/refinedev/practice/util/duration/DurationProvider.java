package xyz.refinedev.practice.util.duration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import xyz.refinedev.practice.util.command.argument.CommandArg;
import xyz.refinedev.practice.util.command.exception.CommandExitMessage;
import xyz.refinedev.practice.util.command.parametric.DrinkProvider;

import java.lang.annotation.Annotation;
import java.util.List;

public class DurationProvider extends DrinkProvider<Duration> {

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
    public Duration provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String type = arg.get();
        return Duration.fromString(type);
    }

    @Override
    public String argumentDescription() {
        return "duration";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return null;
    }
}

