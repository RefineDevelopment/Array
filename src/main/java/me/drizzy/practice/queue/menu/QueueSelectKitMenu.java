package me.drizzy.practice.queue.menu;

import lombok.AllArgsConstructor;
import me.drizzy.practice.queue.Queue;
import me.drizzy.practice.enums.QueueType;
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
    public String getTitle(final Player player) {
        return "&8Select a Kit";
    }

    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons =new HashMap<>();
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
        public ItemStack getButtonItem(final Player player) {
            List<String> lore = new ArrayList<>();
            for ( String lines : Array.getInstance().getEssentials().getQueueLore() ) {
                lore.add(CC.translate(this.replace(lines)));
            }
            return new ItemBuilder(this.queue.getKit().getDisplayIcon()).name(this.queue.getKit().getDisplayName()).lore(lore).clearFlags().build();
        }

        @Override
        public void clicked(final Player player, final ClickType clickType) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile.isBusy()) {
                player.sendMessage(CC.RED + "You cannot queue right now.");
                return;
            }
            player.closeInventory();
            if (QueueSelectKitMenu.this.queueType == QueueType.UNRANKED) {
                this.queue.addPlayer(player, 0);
            }
            else if (QueueSelectKitMenu.this.queueType == QueueType.RANKED) {
                this.queue.addPlayer(player, profile.getStatisticsData().get(this.queue.getKit()).getElo());
            }
        }

        public String replace(String input) {
            input = input.replace("{in_queue}", String.valueOf(this.queue.getPlayers().size()))
                         .replace("{in_fight}", String.valueOf(Match.getInFights(this.queue)));
            return input;
        }
    }
}
