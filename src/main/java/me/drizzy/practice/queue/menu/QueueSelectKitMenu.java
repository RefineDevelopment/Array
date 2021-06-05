package me.drizzy.practice.queue.menu;

import lombok.AllArgsConstructor;
import me.drizzy.practice.Locale;
import me.drizzy.practice.essentials.Essentials;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.queue.QueueType;
import me.drizzy.practice.Array;
import me.drizzy.practice.match.Match;
import me.drizzy.practice.profile.Profile;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.inventory.ItemBuilder;
import me.drizzy.practice.util.menu.Button;
import me.drizzy.practice.util.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class QueueSelectKitMenu extends Menu {

    private final QueueType queueType;

    @Override
    public String getTitle(Player player) {
        return "&8Select a Kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        
        int i = 0;
        for (final Queue queue : Queue.getQueues()) {
            if (queue.getType() == this.queueType) {
                buttons.put(i++, new SelectKitButton(queue));
            }
        }
        return buttons;
    }

    @AllArgsConstructor
    private class SelectKitButton extends Button {

        private final Queue queue;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            
            Essentials.getQueueLore().forEach(lines -> lore.add(CC.translate(this.replace(Profile.getByPlayer(player), lines))));
            return new ItemBuilder(queue.getKit().getDisplayIcon()).name(queue.getKit().getDisplayName()).lore(lore).clearFlags().build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            player.closeInventory();
            if (profile.isBusy()) {
                player.sendMessage(Locale.ERROR_UNAVAILABLE.toString());
                return;
            }

            switch (queueType) {
                case UNRANKED: {
                    this.queue.addPlayer(player, 0);
                    break;
                }
                case RANKED: {
                    this.queue.addPlayer(player, profile.getStatisticsData().get(this.queue.getKit()).getElo());
                    break;
                }
                case CLAN: {
                    this.queue.addPlayer(player, profile.getClan().getElo());
                    break;
                }
            }
        }

        public String replace(Profile profile, String input) {
            input = input.replace("<in_queue>", String.valueOf(this.queue.getPlayers().size()))
                         .replace("<queue_elo>", String.valueOf(queueType == QueueType.RANKED ? profile.getStatisticsData().get(this.queue.getKit()).getElo() : queueType == QueueType.CLAN ? profile.getClan().getElo() : "None"))
                         .replace("<in_fight>", String.valueOf(Match.getInFights(this.queue)));
            return input;
        }
    }
}
