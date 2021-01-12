package me.array.ArrayPractice.kit.command;

import com.qrakn.honcho.command.CommandMeta;
import me.array.ArrayPractice.util.external.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@CommandMeta(label={"array goldenhead"}, permission="practice.staff")
public class GoldenHeadcommand {
    public void execute(Player player) {
        ItemStack itemStack = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("Golden Head");
        if (player.getItemInHand() != null) {
            player.sendMessage(CC.RED + "Please empty your hand!");
        } else {
            player.setItemInHand(itemStack);
            player.sendMessage("Golden Head Received!");
        }
    }
}
