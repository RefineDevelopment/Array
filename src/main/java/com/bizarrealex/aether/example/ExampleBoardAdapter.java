package com.bizarrealex.aether.example;
import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import com.bizarrealex.aether.scoreboard.cooldown.BoardFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ExampleBoardAdapter implements BoardAdapter, Listener {

    public ExampleBoardAdapter(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String getTitle(Player player) {
        return "&6&lAether";
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        List<String> strings = new ArrayList<>();
        strings.add("&7&m-------------------");

        for (BoardCooldown cooldown : cooldowns) {
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
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Board board = Board.getByPlayer(player);

        if (event.getAction().name().contains("RIGHT")) {
            if (event.getItem() == null) return;
            if (event.getItem().getType() != Material.ENDER_PEARL) return;
            if (board == null) return;
            if (player.getGameMode() == GameMode.CREATIVE) return;

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
                return;
            }

            BoardCooldown cooldown = board.getCooldown("enderpearl");
            if (cooldown != null) {
                event.setCancelled(true);
                player.updateInventory();
                player.sendMessage(ChatColor.RED + "You must wait " + cooldown.getFormattedString(BoardFormat.SECONDS) + " seconds before enderpearling again!");
                return;
            }

            new BoardCooldown(board, "enderpearl", 16);
        }
    }

}