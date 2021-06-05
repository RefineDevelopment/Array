package me.drizzy.practice.match.team;

import me.drizzy.practice.util.other.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter @Setter
public class TeamPlayer {

    private Map<UUID, List<Long>> cpsMap = new HashMap<>();

    private final UUID uuid;
    private final String username;

    private Location playerSpawn;
    private Location parkourCheckpoint;

    private boolean alive = true;
    private boolean disconnected;

    private int elo;
    private int potionsThrown;
    private int potionsMissed;
    private int potions;
    private int hits;
    private int combo;
    private int longestCombo;

    public TeamPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.username = player.getName();
        int pots = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null)
                continue;
            if (item.getType() == Material.AIR)
                continue;
            if (item.getType() != Material.POTION)
                continue;
            if (item.getDurability() != (short)16421)
                continue;
            pots++;
        }
        this.potions = pots;
    }

    public TeamPlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        int pots = 0;
        for (ItemStack item : Bukkit.getPlayer(uuid).getInventory().getContents()) {
            if (item == null)
                continue;
            if (item.getType() == Material.AIR)
                continue;
            if (item.getType() != Material.POTION)
                continue;
            if (item.getDurability() != (short)16421)
                continue;
            pots++;
        }
        this.potions = pots;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public String getDisplayName() {
        Player player = getPlayer();
        return player == null ? username : player.getDisplayName();
    }

    public int getPing() {
        Player player = getPlayer();
        return player == null ? 0 : PlayerUtil.getPing(player);
    }

    public int getCps() {
        if (cpsMap.get(uuid) == null) {
            return 0;
        }
        cpsMap.get(uuid).removeIf(count -> count < System.currentTimeMillis() - 1000L);
        return cpsMap.get(uuid).size();
    }

    public double getPotionAccuracy() {
        if (potionsThrown == 0) {
            return 100.0;
        }
        if (potionsMissed == 0) {
            return 100.0;
        } else if (potionsThrown == potionsMissed) {
            return 50.0;
        }

        return Math.round(100.0D - (((double) this.potionsMissed / (double) this.potionsThrown) * 100.0D));
    }

    public void incrementPotionsThrown() {
        potionsThrown++;
    }

    public void incrementPotionsMissed() {
        potionsMissed++;
    }

    public void handleHit() {
        hits++;
        combo++;

        if (combo > longestCombo) {
            longestCombo = combo;
        }
    }

    public void resetCombo() {
        combo = 0;
    }

}
