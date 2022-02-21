package xyz.refinedev.practice.clan;

import xyz.refinedev.practice.clan.meta.ClanProfile;
import xyz.refinedev.practice.util.command.argument.CommandArg;
import xyz.refinedev.practice.util.command.exception.CommandExitMessage;
import xyz.refinedev.practice.util.command.parametric.DrinkProvider;
import xyz.refinedev.practice.util.other.PlayerUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    public ClanProfile provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        UUID uuid = PlayerUtil.getUUIDByName(name);
        if (uuid == null) throw new CommandExitMessage("A profile with that name does not exist");

        ClanProfile clanProfile = this.getPlugin().getClanManager().getProfileByUUID(uuid);
        if (clanProfile == null) throw new CommandExitMessage("A profile with that name does not exist");

        return clanProfile;
    }

    @Override
    public String argumentDescription() {
        return "clan-profile";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return null;
    }
}
