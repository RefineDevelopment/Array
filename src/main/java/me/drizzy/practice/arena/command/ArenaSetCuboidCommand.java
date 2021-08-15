package me.drizzy.practice.arena.command;

import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.enums.ArenaType;
import me.drizzy.practice.arena.impl.TheBridgeArena;
import me.drizzy.practice.arena.selection.Selection;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena setcuboid", permission = "array.dev")
public class ArenaSetCuboidCommand {
    public void execute(Player player,@CPL("arena") String arena, @CPL("[blue|red]") String color) {
      if (!color.equals("blue") && !color.equals("red")) {
          player.sendMessage(CC.translate("&8[&c&lArray&8] &7That is an invalid team."));
          return;
      }
        Arena mainArena = Arena.getByName(arena);
      if (mainArena == null) {
          player.sendMessage(CC.translate("&8[&c&lArray&8] &7An arena with that name does not exist."));
          return;
      }

      if (mainArena.getType() != ArenaType.THEBRIDGE) {
          player.sendMessage(CC.translate("&8[&c&lArray&8] &7That arena is not a &cTheBridge &7arena."));
          return;
      }

      if (color.equalsIgnoreCase("blue")) {
          TheBridgeArena bridgeArena = (TheBridgeArena) mainArena;
          Selection selection = Selection.createOrGetSelection(player);
          if (!selection.isFullObject()) {
              player.sendMessage(CC.translate("&8[&c&lArray&8] &7Your selection is incomplete."));
              return;
          }
          bridgeArena.setBlueCuboid(selection.getCuboid());
          player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully set the &cBlue Cuboid&7!"));
      }
      if (color.equalsIgnoreCase("red")) {
          TheBridgeArena bridgeArena = (TheBridgeArena) mainArena;
          Selection selection = Selection.createOrGetSelection(player);
          if (!selection.isFullObject()) {
              player.sendMessage(CC.translate("&8[&c&lArray&8] &7Your selection is incomplete."));
              return;
          }
          bridgeArena.setRedCuboid(selection.getCuboid());
          player.sendMessage(CC.translate("&8[&c&lArray&8] &7Successfully set the &cRed Cuboid&7!"));
      }
    }
}
