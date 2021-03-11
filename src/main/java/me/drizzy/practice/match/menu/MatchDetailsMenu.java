package me.drizzy.practice.match.menu;

import me.drizzy.practice.match.MatchSnapshot;
import me.drizzy.practice.match.team.TeamPlayer;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.util.InventoryUtil;
import me.drizzy.practice.util.external.ItemBuilder;
import me.drizzy.practice.util.external.PotionUtil;
import me.drizzy.practice.util.external.TimeUtil;
import me.drizzy.practice.util.external.menu.Button;
import me.drizzy.practice.util.external.menu.Menu;
import me.drizzy.practice.util.external.menu.button.DisplayButton;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

@AllArgsConstructor
public class MatchDetailsMenu extends Menu {

    private final MatchSnapshot snapshot;
    private final MatchSnapshot opponent;

    @Override
    public String getTitle(Player player) {
        return "&7&lInventory of &b" + snapshot.getTeamPlayer().getUsername();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons=new HashMap<>();
        ItemStack[] fixedContents=InventoryUtil.fixInventoryOrder(snapshot.getContents());

        for ( int i=0; i < fixedContents.length; i++ ) {
            ItemStack itemStack=fixedContents[i];

            if (itemStack != null && itemStack.getType() != Material.AIR) {
                buttons.put(i, new DisplayButton(itemStack, true));
            }
        }

        for ( int i=0; i < snapshot.getArmor().length; i++ ) {
            ItemStack itemStack=snapshot.getArmor()[i];

            if (itemStack != null && itemStack.getType() != Material.AIR) {
                buttons.put(39 - i, new DisplayButton(itemStack, true));
            }
        }

        int pos=45;

        buttons.put(pos++, new HealthButton(snapshot.getHealth()));
        buttons.put(pos++, new HungerButton(snapshot.getHunger()));
        buttons.put(pos++, new EffectsButton(snapshot.getEffects()));

        if (snapshot.shouldDisplayRemainingPotions()) {
            buttons.put(pos++, new PotionsButton(snapshot.getTeamPlayer().getUsername(), snapshot.getRemainingPotions()));
        }

        buttons.put(pos, new StatisticsButton(snapshot.getTeamPlayer()));

        if (this.snapshot.getSwitchTo() != null || this.opponent != null) {
            buttons.put(53, new SwitchInventoryButton(this.snapshot.getSwitchTo()));
        }

        return buttons;
    }

    @AllArgsConstructor
    private class SwitchInventoryButton extends Button {

        private final TeamPlayer switchTo;

        @Override
        public ItemStack getButtonItem(Player player) {
            if (opponent != null) {
                return new ItemBuilder(Material.LEVER)
                        .name("&b&lOther Inventory")
                        .lore("&7Switch to &b" + opponent.getTeamPlayer().getUsername() + "&7's inventory")
                        .build();
            }

            return new ItemBuilder(Material.LEVER)
                    .name("&b&lOther Inventory")
                    .lore("&7Switch to &b" + switchTo.getUsername() + "&7's inventory")
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (opponent != null) {
                new MatchDetailsMenu(opponent, snapshot).openMenu(player);
            } else {
                MatchSnapshot cachedInventory;

                try {
                    cachedInventory=MatchSnapshot.getByUuid(UUID.fromString(switchTo.getUuid().toString()));
                } catch (Exception e) {
                    cachedInventory=MatchSnapshot.getByName(switchTo.getUuid().toString());
                }

                if (cachedInventory == null) {
                    player.sendMessage(CC.RED + "Couldn't find an inventory for that ID.");
                    return;
                }

                new MatchDetailsMenu(cachedInventory, null).openMenu(player);
            }
        }

    }

    @AllArgsConstructor
    private static class HealthButton extends Button {

        private final int health;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.MELON)
                    .name("&bHealth: &7" + health + "/10&c" + StringEscapeUtils.unescapeJava("\u2764"))
                    .amount(health == 0 ? 1 : health)
                    .build();
        }

    }

    @AllArgsConstructor
    private static class HungerButton extends Button {

        private final int hunger;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.COOKED_BEEF)
                    .name("&bHunger: &7" + hunger + "/20")
                    .amount(hunger == 0 ? 1 : hunger)
                    .build();
        }

    }

    @AllArgsConstructor
    private static class EffectsButton extends Button {

        private final Collection<PotionEffect> effects;

        @Override
        public ItemStack getButtonItem(Player player) {
            ItemBuilder builder=new ItemBuilder(Material.POTION).name("&b&lPotion Effects");

            if (effects.isEmpty()) {
                builder.lore("&bNo potion effects");
            } else {
                List<String> lore=new ArrayList<>();

                effects.forEach(effect -> {
                    String name = PotionUtil.getName(effect.getType()) + " " + (effect.getAmplifier() + 1);
                    String duration = " (" + TimeUtil.millisToTimer((effect.getDuration() / 20) * 1000) + ")";
                    lore.add("&b" + name + "&f" + duration);
                });

                builder.lore(lore);
            }

            return builder.build();
        }

    }

    @AllArgsConstructor
    private static class PotionsButton extends Button {

        private final String name;
        private final int potions;

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

    @AllArgsConstructor
    private static class StatisticsButton extends Button {

        private final TeamPlayer teamPlayer;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.PAPER)
                    .name("&bStatistics")
                    .lore(Arrays.asList(
                            "&fTotal Hits: &b" + teamPlayer.getHits(),
                            "&fLongest Combo: &b" + teamPlayer.getLongestCombo(),
                            "&fPotions Thrown: &b" + teamPlayer.getPotionsThrown(),
                            "&fPotions Missed: &b" + teamPlayer.getPotionsMissed(),
                            "&fPotion Accuracy: &b" + teamPlayer.getPotionAccuracy() + "%"
                    ))
                    .build();
        }

    }

}
