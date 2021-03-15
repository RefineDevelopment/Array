package me.drizzy.practice.kit.command;

import me.drizzy.practice.kit.Kit;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.queue.QueueType;
import me.drizzy.practice.statistics.StatisticsData;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "kit create", permission = "array.dev")
public class KitCreateCommand {

    public void execute(Player player, String kitName) {
        if (Kit.getByName(kitName) != null) {
            player.sendMessage(CC.translate("&8[&b&lArray&8] &7A kit with that name already exists."));
            return;
        }
        Kit kit = new Kit(kitName);
        kit.save();
        Kit.getKits().add(kit);
        kit.setEnabled(true);
        kit.getGameRules().setRanked(true);
        for ( Profile profile : Profile.getProfiles().values() ) {
            profile.getStatisticsData().put(kit, new StatisticsData());
        }
        if (kit.isEnabled()) {
            Queue unRanked = new Queue(kit, QueueType.UNRANKED);
            Queue ranked = new Queue(kit, QueueType.RANKED);
            kit.setUnrankedQueue(unRanked);
            kit.setRankedQueue(ranked);
        }

        player.sendMessage(CC.translate("&8[&b&lArray&8] &7Successfully created a new kit &b" + kit.getDisplayName() + "."));
    }

}
