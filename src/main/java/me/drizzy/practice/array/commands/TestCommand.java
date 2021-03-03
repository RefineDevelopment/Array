package me.drizzy.practice.array.commands;

import me.drizzy.practice.Array;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

@CommandMeta(label="arraysecretcommand",permission="array.dev")
public class TestCommand {
    public void execute(Player player) {
        player.setMetadata("arraytest", new FixedMetadataValue(Array.getInstance(), "nigger"));
    }
}
