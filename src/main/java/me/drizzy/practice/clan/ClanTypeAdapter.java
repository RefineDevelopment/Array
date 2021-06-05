package me.drizzy.practice.clan;

import me.drizzy.practice.util.command.argument.CommandArg;
import me.drizzy.practice.util.command.exception.CommandExitMessage;
import me.drizzy.practice.util.command.parametric.DrinkProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClanTypeAdapter extends DrinkProvider<Clan> {

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
    public Clan provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
       String name = arg.get();
       Clan clan = Clan.getByName(name);
       if (clan != null) {
           return clan;
       }

        throw new CommandExitMessage("A clan with that name does not exist.");
    }

    @Override
    public String argumentDescription() {
        return "clan";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        return Clan.getClans().stream().map(Clan::getName).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
