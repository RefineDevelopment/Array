package me.array.ArrayPractice.queue.menu;

import me.array.ArrayPractice.match.Match;
import me.array.ArrayPractice.profile.Profile;
import me.array.ArrayPractice.queue.Queue;
import me.array.ArrayPractice.queue.QueueType;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.util.external.ItemBuilder;
import org.bukkit.event.inventory.ClickType;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import me.array.ArrayPractice.util.external.menu.Button;
import java.util.Map;
import org.bukkit.entity.Player;
import me.array.ArrayPractice.util.external.menu.Menu;

public class QueueSelectKitMenu extends Menu
{
    private QueueType queueType;
    
    @Override
    public String getTitle(final Player player) {
        return "&8Select a Kit";
    }
    
    @Override
    public Map<Integer, Button> getButtons(final Player player) {
        final Map<Integer, Button> buttons = new HashMap<Integer, Button>();
        int i = 0;
        for (final Queue queue : Queue.getQueues()) {
            if (queue.getType() == this.queueType) {
                buttons.put(i++, new SelectKitButton(queue));
            }
        }
        return buttons;
    }
    
    @ConstructorProperties({ "queueType" })
    public QueueSelectKitMenu(final QueueType queueType) {
        this.queueType = queueType;
    }
    
    private class SelectKitButton extends Button
    {
        private Queue queue;
        
        @Override
        public ItemStack getButtonItem(final Player player) {
            final List<String> lore = new ArrayList<String>();
            lore.add(CC.SB_BAR);
            lore.add("&fIn Queue: &b" + this.queue.getPlayers().size());
            lore.add("&fIn Fight: &b" + Match.getInFights(this.queue));
            lore.add(CC.SB_BAR);
            lore.add("&7Click to join queue.");
            return new ItemBuilder(this.queue.getKit().getDisplayIcon()).name("&b&l" + this.queue.getKit().getName()).lore(lore).build();
        }
        
        @Override
        public void clicked(final Player player, final ClickType clickType) {
            final Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile == null) {
                return;
            }
            if (profile.isBusy(player)) {
                player.sendMessage(CC.RED + "You cannot queue right now.");
                return;
            }
            player.closeInventory();
            if (QueueSelectKitMenu.this.queueType == QueueType.UNRANKED) {
                this.queue.addPlayer(player, 0);
            }
            else if (QueueSelectKitMenu.this.queueType == QueueType.RANKED) {
                this.queue.addPlayer(player, profile.getKitData().get(this.queue.getKit()).getElo());
            }
        }
        
        @ConstructorProperties({ "queue" })
        public SelectKitButton(final Queue queue) {
            this.queue = queue;
        }
    }
}
