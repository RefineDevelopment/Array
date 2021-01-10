package me.array.ArrayPractice.kit.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit sumo", "kit enable sumo"}, permission="practice.staff")
public class KitSumoCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Kit does not exist");
        } else {
            if (kit.getGameRules().isSumo()) {
                kit.getGameRules().setSumo(false);
            } else if (!kit.getGameRules().isSumo()) {
                kit.getGameRules().setSumo(true);
            }
            kit.save();
            player.sendMessage(CC.GREEN + "Kit set sumo mode to " + (kit.getGameRules().isSumo() ? "true!" : "false!"));
        }
    }
}
