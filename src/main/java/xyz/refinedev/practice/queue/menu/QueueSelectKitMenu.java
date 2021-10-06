package xyz.refinedev.practice.queue.menu;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.Locale;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.queue.QueueType;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.Menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class QueueSelectKitMenu extends Menu {

    private final Array plugin = this.getPlugin();
    private final QueueType queueType;

    @Override
    public String getTitle(Player player) {
        return "&8Select a Kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        
        for (Queue queue : plugin.getQueueManager().getQueueMap().values()) {
            if (queue.getType() == this.queueType) {
                buttons.put(buttons.size(), new SelectKitButton(queue));
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
            
            this.getLore().forEach(lines -> lore.add(CC.translate(this.replace(plugin.getProfileManager().getByPlayer(player), lines))));
            return new ItemBuilder(queue.getKit().getDisplayIcon()).name(queue.getKit().getDisplayName()).lore(lore).clearFlags().build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());

            player.closeInventory();
            if (profile.isBusy()) {
                player.sendMessage(Locale.ERROR_NOTABLE.toString());
                return;
            }

            switch (queueType) {
                case UNRANKED: {
                    plugin.getQueueManager().addPlayer(queue, player, 0);
                    break;
                }
                case RANKED: {
                    plugin.getQueueManager().addPlayer(queue, player, profile.getStatisticsData().get(this.queue.getKit()).getElo());
                    break;
                }
                case CLAN: {
                    plugin.getQueueManager().addPlayer(queue, player, profile.getClan().getElo());
                    break;
                }
            }
        }

        public String replace(Profile profile, String input) {
            input = input.replace("<in_queue>", String.valueOf(this.queue.getPlayers().size()))
                         .replace("<queue_elo>", String.valueOf(queueType == QueueType.RANKED ? profile.getStatisticsData().get(this.queue.getKit()).getElo() : queueType == QueueType.CLAN ? profile.getClan().getElo() : "None"))
                         .replace("<in_fight>", String.valueOf(this.queue.getInFights()));
            return input;
        }

        public List<String> getLore() {
            List<String> lore = new ArrayList<>();

           for ( String line : plugin.getConfigHandler().getQUEUE_LORE()) {
               if (line.contains("<description>")) {
                   line = line.replace("<description>", "");
                   lore.addAll(this.queue.getKit().getKitDescription().stream().map(CC::translate).collect(Collectors.toList()));
               }
               lore.add(CC.translate(line));
           }
           return lore;
        }
    }
}
