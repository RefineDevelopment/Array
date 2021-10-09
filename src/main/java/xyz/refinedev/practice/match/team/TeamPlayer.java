package xyz.refinedev.practice.match.team;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.profile.Profile;
import xyz.refinedev.practice.util.other.PlayerUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class TeamPlayer {

    private Map<UUID, List<Long>> cpsMap = new HashMap<>();

    private final UUID uniqueId;
    private final String username;

    private Location playerSpawn, parkourCheckpoint;

    private boolean alive = true;
    private boolean disconnected;

    private int elo, potionsThrown, potionsMissed, hits, combo, longestCombo;

    public TeamPlayer(Player player) {
        this.uniqueId = player.getUniqueId();
        this.username = player.getName();
    }

    public TeamPlayer(UUID uniqueId, String username) {
        this.uniqueId = uniqueId;
        this.username = username;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueId);
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
        if (cpsMap.get(uniqueId) == null) return 0;
        cpsMap.get(uniqueId).removeIf(count -> count < System.currentTimeMillis() - 1000L);

        return cpsMap.get(uniqueId).size();
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
