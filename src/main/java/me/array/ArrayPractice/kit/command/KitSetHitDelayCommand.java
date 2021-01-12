package me.array.ArrayPractice.kit.command;

import com.qrakn.honcho.command.CPL;
import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.kit.Kit;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label={"kit hitdelay", "kit set hitdelay"}, permission="practice.staff")
public class KitSetHitDelayCommand {
    public void execute(Player player, @CPL("kit") Kit kit, @CPL("delay") int delay) {
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Kit does not exist");
        } else {
            kit.getGameRules().setHitDelay(delay);
            kit.save();
            player.sendMessage(CC.GREEN + "Kit hitdelay set to " + delay);
        }
    }
}