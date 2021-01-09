package me.array.ArrayPractice.kit.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandMeta(label={"kit elo", "kit ranked"}, permission="practice.staff")
public class KitSetRankedCommand {
    public void execute(Player player, @CPL("kit") Kit kit) {
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Kit does not exist");
        } else {
            if (kit.getGameRules().isRanked()) {
                kit.getGameRules().setRanked(false);
            } else if (!kit.getGameRules().isRanked()) {
                kit.getGameRules().setRanked(true);
            }
            kit.save();
            player.sendMessage(CC.GREEN + "Kit elo set to " + (kit.getGameRules().isRanked() ? "true!" : "false!"));
        }
    }
}
