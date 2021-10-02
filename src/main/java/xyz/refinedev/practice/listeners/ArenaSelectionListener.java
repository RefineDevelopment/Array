package xyz.refinedev.practice.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.selection.Selection;
import xyz.refinedev.practice.util.chat.CC;

@RequiredArgsConstructor
public class ArenaSelectionListener implements Listener {

    private final Array plugin;

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        ItemStack item = event.getItem();
        if (item != null && item.equals(Selection.SELECTION_WAND)) {
            Player player = event.getPlayer();
            Block clicked = event.getClickedBlock();
            int location = 0;

            Selection selection = Selection.createOrGetSelection(player);

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                selection.setPoint2(clicked.getLocation());
                location = 2;
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                selection.setPoint1(clicked.getLocation());
                location = 1;
            }

            event.setCancelled(true);
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);

            String message = CC.RED + (location == 1 ? "First" : "Second") +
                    " location " + CC.DARK_GRAY + "(" + CC.GRAY +
                    clicked.getX() + CC.DARK_GRAY + ", " + CC.GRAY +
                    clicked.getY() + CC.DARK_GRAY + ", " + CC.GRAY +
                    clicked.getZ() + CC.DARK_GRAY + ")" + CC.RED + " has been set!";

            if (selection.isFullObject()) {
                message += CC.DARK_GRAY + " (" + CC.GRAY + selection.getCuboid().volume() + CC.RED + " blocks" +
                        CC.DARK_GRAY + ")";
            }

            player.sendMessage(message);
        }
    }
}
