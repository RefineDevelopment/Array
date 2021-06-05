package me.drizzy.practice.util.duration;

import me.drizzy.practice.util.command.argument.CommandArg;
import me.drizzy.practice.util.command.exception.CommandExitMessage;
import me.drizzy.practice.util.command.parametric.DrinkProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public Duration provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        String type = arg.get();
        return Duration.fromString(type);
    }

    @Override
    public String argumentDescription() {
        return "duration";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        return null;
    }
}

