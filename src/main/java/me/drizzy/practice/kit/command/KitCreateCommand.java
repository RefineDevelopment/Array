package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.queue.QueueType;
import me.drizzy.practice.util.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "kit create", permission = "practice.dev")
public class KitCreateCommand {

    public void execute(Player player, String kitName) {
        if (Kit.getByName(kitName) != null) {
            player.sendMessage((CC.translate("&8[&b&lArray&8] &c")) + "A kit with that name already exists.");
            return;
        }

        Kit kit = new Kit(kitName);
        kit.save();

        Kit.getKits().add(kit);
        kit.setEnabled(true);
        if (kit.isEnabled()) {
            new Queue(kit, QueueType.UNRANKED);
            if (kit.getGameRules().isRanked()) {
                new Queue(kit, QueueType.RANKED);
            }
        }

        player.sendMessage((CC.translate("&8[&b&lArray&8] &a")) + "You created a new kit.");
    }

}
