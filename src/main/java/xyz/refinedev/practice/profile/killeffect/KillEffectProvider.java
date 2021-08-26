package xyz.refinedev.practice.profile.killeffect;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.refinedev.practice.util.command.argument.CommandArg;
import xyz.refinedev.practice.util.command.exception.CommandExitMessage;
import xyz.refinedev.practice.util.command.parametric.DrinkProvider;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 8/23/2021
 * Project: Array
 */

public class KillEffectProvider extends DrinkProvider<KillEffect> {

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
    public KillEffect provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        KillEffect killEffect = this.getPlugin().getKillEffectManager().getByName(name);
        if (killEffect != null) return killEffect;

        throw new CommandExitMessage("A kill effect with that name does not exist!");
    }

    @Override
    public String argumentDescription() {
        return "killeffect";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        final String finalPrefix = prefix;
        return this.getPlugin().getKillEffectManager().getKillEffects().stream().map(KillEffect::getName).filter(s -> finalPrefix.length() == 0 || s.startsWith(finalPrefix)).collect(Collectors.toList());

    }
}
