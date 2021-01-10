package me.array.ArrayPractice.kit.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit boxuhc", "kit enable boxuhc"}, permission="practice.staff")
public class KitBoxUHCCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Kit does not exist");
        } else {
            if (kit.getGameRules().isBuild()) {
                kit.getGameRules().setBuild(false);
            } else if (!kit.getGameRules().isBuild()) {
                kit.getGameRules().setBuild(true);
            }
            kit.save();
            player.sendMessage(CC.GREEN + "Kit set boxuhc mode to " + (kit.getGameRules().isBoxuhc() ? "true!" : "false!"));
        }
    }
}
