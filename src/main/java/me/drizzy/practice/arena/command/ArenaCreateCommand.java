package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.impl.SharedArena;
import me.drizzy.practice.arena.impl.StandaloneArena;
import me.drizzy.practice.arena.impl.TheBridgeArena;
import me.drizzy.practice.enums.ArenaType;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena create", permission = "array.dev")
public class ArenaCreateCommand {

    public void execute(Player player, @CPL("name") String name, @CPL("[shared|standalone|bridge|duplicate]") String type) {
        if (!type.equalsIgnoreCase("standalone") && !type.equalsIgnoreCase("shared") && !type.equalsIgnoreCase("duplicate") && !type.equalsIgnoreCase("bridge")) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Invalid Type."));
            return;
        }

        if (name == null) {
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Please provide a name."));
            return;
        }

        Arena arena;

        Arena duplicate = Arena.getByName(name);
        if (Arena.getArenas().contains(duplicate) && duplicate != null && type.equalsIgnoreCase("duplicate")) {

            if (duplicate.getType() != ArenaType.STANDALONE) {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7You can't convert a Shared arena to a duped one."));
                return;
            }
            arena = new Arena(name);

            Location loc1 = new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(),
                            player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

            arena.setSpawn1(loc1);
            arena.setSpawn2(loc1);

            StandaloneArena sarena = (StandaloneArena) duplicate;
            sarena.getDuplicates().add(arena);

            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Saved a duplicate arena from &c" + name + "&8(&7#&c" + sarena.getDuplicates().size() + "&8)"));
            player.sendMessage(CC.translate("&8[&cTIP&8] &7Please note the &cDuplicate ID&7 of the arena for later use to setup its spawn points. " + "&8(&7#&c" + sarena.getDuplicates().size() + "&8)"));
        } else {
            if (type.equalsIgnoreCase("shared")){
                arena = new SharedArena(name);
           } else if (type.equalsIgnoreCase("bridge")) {
               arena = new TheBridgeArena(name);
            } else if (type.equalsIgnoreCase("standalone")){
               arena = new StandaloneArena(name);
            } else {
                arena = new StandaloneArena(name);
            }

            Location loc1 = new Location(player.getLocation().getWorld(), player.getLocation().getX(), player.getLocation().getY(),
                    player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());

            arena.setSpawn1(loc1);
            arena.setSpawn2(loc1);
            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully created an Arena called &c" + name + "&7 of type &c" + type));
        }
        Arena.getArenas().add(arena);
        Arena.getArenas().forEach(Arena::save);
    }

}