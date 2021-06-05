package me.drizzy.practice.clan;

import me.drizzy.practice.api.ArrayCache;
import me.drizzy.practice.clan.meta.ClanProfile;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.command.argument.CommandArg;
import me.drizzy.practice.util.command.exception.CommandExitMessage;
import me.drizzy.practice.util.command.parametric.DrinkProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.UUID;

public class ClanProfileProvider extends DrinkProvider<ClanProfile> {

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
    public ClanProfile provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        UUID uuid = ArrayCache.getUUID(name);

        if (uuid != null) {
            ClanProfile clanProfile = Profile.getByUuid(uuid).getClanProfile();
            if (clanProfile != null) {
                return clanProfile;
            }
        }
        throw new CommandExitMessage("A profile with that name does not exist");
    }

    @Override
    public String argumentDescription() {
        return "clan-profile";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        return null;
    }
}
