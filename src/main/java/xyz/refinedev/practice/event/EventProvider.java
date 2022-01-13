package xyz.refinedev.practice.event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
 * Created at 7/10/2021
 * Project: Array
 */

public class EventProvider extends DrinkProvider<EventType> {

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
    public EventType provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();

        EventType type = getPlugin().getEventManager().getByName(name);
        if (type != null) return type;

        throw new CommandExitMessage("An Event with that name does not exist!");
    }

    @Override
    public String argumentDescription() {
        return "event";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        final String finalPrefix = prefix;
        return Arrays.stream(EventType.values()).map(EventType::getName).filter(s -> finalPrefix.length() == 0 || s.startsWith(finalPrefix)).collect(Collectors.toList());
    }
}
