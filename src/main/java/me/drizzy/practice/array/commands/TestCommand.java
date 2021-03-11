package me.drizzy.practice.array.commands;

import me.drizzy.practice.Array;
import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

@CommandMeta(label="arraysecretcommand",permission="array.dev")
public class TestCommand {
    public void execute(Player player) {
        Queue queue = Queue.getByKit(Kit.getByName("NoDebuff"));
        if (queue !=null) {
            player.sendMessage(CC.GREEN + "Queue works!");
        } else {
            player.sendMessage(CC.RED + "Queue failed!");
        }
        player.setMetadata("ArrayTest", new FixedMetadataValue(Array.getInstance(), "nibber"));
    }
}
