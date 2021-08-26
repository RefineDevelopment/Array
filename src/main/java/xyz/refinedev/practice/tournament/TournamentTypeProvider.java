package xyz.refinedev.practice.tournament;

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
 * Created: 8/10/2021
 * Project: Array
 */

public class TournamentTypeProvider extends DrinkProvider<TournamentType> {

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
    public TournamentType provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        TournamentType type;
        try {
            type = TournamentType.valueOf(name);
        } catch (Exception e) {
            throw new CommandExitMessage("A tournament type with that name does not exist!");
        }
        return type;
    }

    @Override
    public String argumentDescription() {
        return "tournament-type";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        return Arrays.stream(TournamentType.values()).map(TournamentType::name).filter(s -> prefix.length() == 0 || s.startsWith(prefix)).collect(Collectors.toList());
    }
}
