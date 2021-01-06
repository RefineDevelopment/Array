package me.array.ArrayPractice.match.team;

import me.array.ArrayPractice.util.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class TeamPlayer {

    @Getter
    private final UUID uuid;
    @Getter
    private final String username;
    @Getter
    @Setter
    private boolean alive = true;
    @Getter
    @Setter
    private boolean disconnected;
    @Getter
    @Setter
    private int elo;
    @Getter
    @Setter
    private int potionsThrown;
    @Getter
    @Setter
    private int potionsMissed;
    @Getter
    @Setter
    private int potions;
    @Getter
    @Setter
    private int hits;
    @Getter
    @Setter
    private int combo;
    @Getter
    @Setter
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

    public double getPotionAccuracy() {
        if (potionsMissed == 0) {
            return 100.0;
        } else if (potionsThrown == potionsMissed) {
            return 50.0;
        }

        return Math.round(100.0D - (((double) potionsMissed / (double) potionsThrown) * 100.0D));
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
