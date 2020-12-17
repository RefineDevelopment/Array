package com.bizarrealex.aether.example;

import org.bukkit.plugin.java.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import com.bizarrealex.aether.scoreboard.*;
import com.bizarrealex.aether.scoreboard.cooldown.*;
import java.util.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.*;
import org.bukkit.event.*;

public class ExampleBoardAdapter implements BoardAdapter, Listener
{
    public ExampleBoardAdapter(final JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    @Override
    public String getTitle(final Player player) {
        return "&6&lAether";
    }
    
    @Override
    public List<String> getScoreboard(final Player player, final Board board, final Set<BoardCooldown> cooldowns) {
        final List<String> strings = new ArrayList<String>();
        strings.add("&7&m-------------------");
        for (final BoardCooldown cooldown : cooldowns) {
            if (cooldown.getId().equals("enderpearl")) {
                strings.add("&e&lEnderpearl&7:&c " + cooldown.getFormattedString(BoardFormat.SECONDS));
            }
        }
        strings.add("&7&m-------------------&r");
        if (strings.size() == 2) {
            return null;
        }
        return strings;
    }
    
    @EventHandler
    public void onPlayerInteractEvent(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Board board = Board.getByPlayer(player);
        if (event.getAction().name().contains("RIGHT")) {
            if (event.getItem() == null) {
                return;
            }
            if (event.getItem().getType() != Material.ENDER_PEARL) {
                return;
            }
            if (board == null) {
                return;
            }
            if (player.getGameMode() == GameMode.CREATIVE) {
                return;
            }
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
                return;
            }
            final BoardCooldown cooldown = board.getCooldown("enderpearl");
            if (cooldown != null) {
                event.setCancelled(true);
                player.updateInventory();
                player.sendMessage(ChatColor.RED + "You must wait " + cooldown.getFormattedString(BoardFormat.SECONDS) + " seconds before enderpearling again!");
                return;
            }
            new BoardCooldown(board, "enderpearl", 16.0);
        }
    }
}
