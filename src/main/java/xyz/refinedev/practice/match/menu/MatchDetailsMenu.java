package xyz.refinedev.practice.match.menu;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.match.MatchSnapshot;
import xyz.refinedev.practice.match.team.TeamPlayer;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.config.impl.FoldersConfigurationFile;
import xyz.refinedev.practice.util.inventory.InventoryUtil;
import xyz.refinedev.practice.util.inventory.ItemBuilder;
import xyz.refinedev.practice.util.menu.Button;
import xyz.refinedev.practice.util.menu.ButtonUtil;
import xyz.refinedev.practice.util.menu.Menu;
import xyz.refinedev.practice.util.menu.button.DisplayButton;
import xyz.refinedev.practice.util.other.PotionUtil;
import xyz.refinedev.practice.util.other.TimeUtil;

import javax.annotation.Nullable;
import java.util.*;

public class MatchDetailsMenu extends Menu {
    
    private final FoldersConfigurationFile config;

    private final MatchSnapshot snapshot;
    private final MatchSnapshot opponent;

    public MatchDetailsMenu(Array plugin, MatchSnapshot snapshot, @Nullable MatchSnapshot opponent) {
        super(plugin);

        this.config = this.getPlugin().getMenuHandler().getConfigByName("general");
        this.snapshot = snapshot;
        this.opponent = opponent;
    }

    @Override
    public String getTitle(Player player) {
        return config.getString("MATCH_DETAILS_MENU.TITLE")
                .replace("<snapshot_name>", snapshot.getTeamPlayer().getUsername());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Preconditions.checkNotNull(snapshot, "Snapshot can not be null!");

        Map<Integer, Button> buttons = new HashMap<>();
        ItemStack[] fixedContents = InventoryUtil.fixInventoryOrder(snapshot.getContents());

        for ( int i = 0; i < fixedContents.length; i++ ) {
            ItemStack itemStack = fixedContents[i];

            if (itemStack != null && itemStack.getType() != Material.AIR) {
                buttons.put(i, new DisplayButton(itemStack, true));
            }
        }

        for ( int i = 0; i < snapshot.getArmor().length; i++ ) {
            ItemStack itemStack = snapshot.getArmor()[i];

            if (itemStack != null && itemStack.getType() != Material.AIR) {
                buttons.put(39 - i, new DisplayButton(itemStack, true));
            }
        }

        buttons.put(48, new HealthButton(snapshot.getHealth()));
        buttons.put(49, new HungerButton(snapshot.getHunger()));
        buttons.put(50, new EffectsButton(snapshot.getEffects()));

        if (snapshot.shouldDisplayRemainingPotions()) {
            buttons.put(51, new PotionsButton(snapshot.getTeamPlayer().getUsername(), snapshot.getRemainingPotions()));
        }

        buttons.put(47, new StatisticsButton(snapshot.getTeamPlayer()));

        if (this.snapshot.getSwitchTo() != null || this.opponent != null) {
            buttons.put(45, new SwitchInventoryButton(this.snapshot.getSwitchTo()));
            buttons.put(53, new SwitchInventoryButton(this.snapshot.getSwitchTo()));
        }

        return buttons;
    }

    private class SwitchInventoryButton extends Button {

        private final TeamPlayer switchTo;

        public SwitchInventoryButton(TeamPlayer switchTo) {
            super(MatchDetailsMenu.this.getPlugin());
            this.switchTo = switchTo;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            String path = "MATCH_DETAILS_MENU.SWITCH_INVENTORY_BUTTON";
            int data = config.getInteger(path + ".DATA");
            Material material = ButtonUtil.getMaterial(config, path + ".MATERIAL");
            if (material == null) player.closeInventory();

            ItemBuilder itemBuilder  = new ItemBuilder(material);
            itemBuilder.durability(data);
            itemBuilder.name(config.getString(path + ".NAME")
                    .replace("<snapshot_next_name>", opponent == null ?
                    switchTo.getUsername() :
                    opponent.getTeamPlayer().getUsername()));

            return itemBuilder.build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (opponent != null) {
                MatchDetailsMenu menu = new MatchDetailsMenu(this.getPlugin(), opponent, snapshot);
                menu.openMenu(player);
            }

            MatchSnapshot cachedInventory;
            if (opponent == null) {
                try {
                    cachedInventory = this.getPlugin().getMatchManager().getByString(switchTo.getUniqueId().toString());
                } catch (Exception e) {
                    player.sendMessage(CC.RED + "Couldn't find an inventory for that ID.");
                    return;
                }

                if (cachedInventory == null) {
                    player.sendMessage(CC.RED + "Couldn't find an inventory for that ID.");
                    return;
                }

                MatchDetailsMenu menu = new MatchDetailsMenu(this.getPlugin(), cachedInventory, snapshot);
                menu.openMenu(player);
            }
        }

    }

    private class HealthButton extends Button {

        private final int health;

        public HealthButton(int health) {
            super(MatchDetailsMenu.this.getPlugin());
            this.health = health;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.MELON)
                    .name("&cHealth: &7" + health + "/10&c + \u2764")
                    .amount(health == 0 ? 1 : health)
                    .build();
        }

    }

    private class HungerButton extends Button {

        private final int hunger;

        public HungerButton(int hunger) {
            super(MatchDetailsMenu.this.getPlugin());
            this.hunger = hunger;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.COOKED_BEEF)
                    .name("&cHunger: &7" + hunger + "/20")
                    .amount(hunger == 0 ? 1 : hunger)
                    .build();
        }

    }

    private class EffectsButton extends Button {

        private final Collection<PotionEffect> effects;

        public EffectsButton(Collection<PotionEffect> effects) {
            super(MatchDetailsMenu.this.getPlugin());
            this.effects = effects;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            ItemBuilder builder = new ItemBuilder(Material.POTION);
            builder.name("&c&lPotion Effects");

            if (effects.isEmpty()) {
                builder.lore("&cNo potion effects");
            } else {
                List<String> lore = new ArrayList<>();
                for ( PotionEffect effect : effects ) {
                    String name = PotionUtil.getName(effect.getType()) + " " + (effect.getAmplifier() + 1);
                    String duration = " (" + TimeUtil.millisToTimer((effect.getDuration() / 20) * 1000L) + ")";
                    lore.add("&c" + name + "&f" + duration);
                }
                builder.lore(lore);
            }
            return builder.build();
        }
    }

    private class PotionsButton extends Button {

        private final String name;
        private final int potions;

        public PotionsButton(String name, int potions) {
            super(MatchDetailsMenu.this.getPlugin());
            this.name = name;
            this.potions = potions;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.POTION)
                    .durability(16421)
                    .amount(potions == 0 ? 1 : potions)
                    .name("&dPotions")
                    .lore("&f" + name + " had " + potions + " potion" + (potions == 1 ? "" : "s") + " left.")
                    .build();
        }

    }

    private class StatisticsButton extends Button {

        private final TeamPlayer teamPlayer;

        public StatisticsButton(TeamPlayer teamPlayer) {
            super(MatchDetailsMenu.this.getPlugin());
            this.teamPlayer = teamPlayer;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.PAPER)
                    .name("&aStatistics")
                    .lore(Arrays.asList(
                            "&fTotal Hits: &a" + teamPlayer.getHits(),
                            "&fLongest Combo: &a" + teamPlayer.getLongestCombo(),
                            "&fPotions Thrown: &a" + teamPlayer.getPotionsThrown(),
                            "&fPotions Missed: &a" + teamPlayer.getPotionsMissed(),
                            "&fPotion Accuracy: &a" + teamPlayer.getPotionAccuracy() + "%"
                    ))
                    .build();
        }

    }

}
