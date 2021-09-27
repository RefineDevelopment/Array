package xyz.refinedev.practice.match;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import xyz.refinedev.practice.match.team.TeamPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class MatchSnapshot {

    private UUID uniqueID = UUID.randomUUID();
    private TeamPlayer teamPlayer;

    private transient TeamPlayer switchTo;
    private int health;
    private int hunger;
    private ItemStack[] armor;
    private ItemStack[] contents;
    private Collection<PotionEffect> effects;
    private long created = System.currentTimeMillis();

    public MatchSnapshot(TeamPlayer teamPlayer) {
        this(teamPlayer, null);
    }

    public MatchSnapshot(TeamPlayer teamPlayer, TeamPlayer switchTo) {
        this.teamPlayer = teamPlayer;
        this.switchTo = switchTo;

        Player player = this.teamPlayer.getPlayer();

        health = player.getHealth() == 0 ? 0 : (int) Math.round(player.getHealth() / 2);
        hunger = player.getFoodLevel();
        armor = player.getInventory().getArmorContents();
        contents = player.getInventory().getContents();
        effects = player.getActivePotionEffects();
    }

    public int getRemainingPotions() {
        int amount = 0;

        for (ItemStack itemStack : this.contents) {
            if (itemStack != null && itemStack.getType() == Material.POTION && itemStack.getDurability() == 16421) {
                amount++;
            }
        }

        return amount;
    }

    public boolean shouldDisplayRemainingPotions() {
        return this.getRemainingPotions() > 0 || this.teamPlayer.getPotionsThrown() > 0 || this.teamPlayer.getPotionsMissed() > 0;
    }

}
