package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CPL;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label="kit enable", permission = "array.dev")
public class KitEnableCommand {
    public void execute(Player player, @CPL("kit") String kit) {
         Kit kits = Kit.getByName(kit);
         if (kits == null) {
             player.sendMessage(CC.translate("&8[&b&lArray&8] &cThat Kit does not exist!"));
             return;
         }
         kits.setEnabled(true);
         player.sendMessage(CC.translate("&8[&b&lArray&8] &aEnabled the kit " + kit));
    }
}
