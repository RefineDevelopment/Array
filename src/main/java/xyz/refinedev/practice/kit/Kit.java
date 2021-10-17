package xyz.refinedev.practice.kit;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.leaderboards.LeaderboardsAdapter;
import xyz.refinedev.practice.queue.Queue;
import xyz.refinedev.practice.util.chat.CC;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/21/2021
 * Project: Array
 */

@Getter @Setter
public class Kit {

    private final Array plugin;

    private final List<LeaderboardsAdapter> eloLeaderboards = new ArrayList<>();
    private final List<LeaderboardsAdapter> winLeaderboards = new ArrayList<>();

    private final KitGameRules gameRules = new KitGameRules();
    private final KitInventory kitInventory = new KitInventory();
    private final List<ItemStack> editorItems = new ArrayList<>();

    private final String name;
    private boolean enabled;
    private List<String> kitDescription;
    private ItemStack displayIcon;
    private String displayName, knockbackProfile;
    private Queue unrankedQueue, rankedQueue, clanQueue;

    public Kit(Array plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.displayName = CC.RED + name;
        this.displayIcon = new ItemStack(Material.DIAMOND_CHESTPLATE);
        this.knockbackProfile = "default";
        this.kitDescription = new ArrayList<>();

        plugin.getKitManager().getKits().add(this);
    }

    public ItemStack getDisplayIcon() {
        return this.displayIcon.clone();
    }

    public boolean isParty() {
        return (!this.gameRules.isDisablePartyFFA() && !this.gameRules.isParkour() && !this.gameRules.isBridge() && !this.gameRules.isDisablePartySplit() && this.isEnabled());
    }

    public void applyToPlayer(Player player) {
        player.getInventory().setArmorContents(getKitInventory().getArmor());
        player.getInventory().setContents(getKitInventory().getContents());
        player.updateInventory();
    }
}
