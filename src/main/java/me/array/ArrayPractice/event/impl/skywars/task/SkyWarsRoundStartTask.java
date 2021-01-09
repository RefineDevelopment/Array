package me.array.ArrayPractice.event.impl.skywars.task;

import me.array.ArrayPractice.Practice;
import me.array.ArrayPractice.util.external.CC;
import me.array.ArrayPractice.event.impl.skywars.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pt.foxspigot.jar.knockback.KnockbackModule;

import java.util.Random;

public class SkyWarsRoundStartTask extends SkyWarsTask {

    public SkyWarsRoundStartTask(SkyWars skyWars) {
        super(skyWars, SkyWarsState.ROUND_STARTING);
    }

    @Override
    public void onRun() {
        if (getTicks() >= 3) {
            this.getSkyWars().broadcastMessage(CC.GREEN + "The round has started!");
            this.getSkyWars().setEventTask(null);
            this.getSkyWars().setState(SkyWarsState.ROUND_FIGHTING);
            this.getSkyWars().setRoundStart(System.currentTimeMillis());

            this.getSkyWars().getPlayers().forEach(player -> ((CraftPlayer)player).getHandle().setKnockback(KnockbackModule.INSTANCE.profiles.get("default")));

            Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> {
                int x = 0;
                Random random = new Random();
                OUTTER_LOOP: for ( SkyWarsChest chest : SkyWarsChest.chests.values()) {
                    Inventory cInv = chest.getBlock().getInventory();
                    for (int i = 0; i < 27; i++) {

                        if (x == 8) {
                            x = 0;
                            continue OUTTER_LOOP;
                        }

                        ItemStack randomItem = ChestType.getLootTable(chest.getType()).next();
                        int randomSlot = random.nextInt(27);

                        while (cInv.contains(randomItem)) {
                            if (randomItem.getAmount() == 1) {
                                randomItem = ChestType.getLootTable(chest.getType()).next();
                            } else {
                                break;
                            }
                        }

                        while (cInv.getItem(randomSlot) != null && cInv.getItem(randomSlot).getType() != Material.AIR) {
                            randomSlot = random.nextInt(27);
                        }

                        cInv.setItem(randomSlot, randomItem);

                        if (cInv.contains(Material.BOW)) {
                            int r = random.nextInt(27);
                            while (cInv.getItem(r) != null) {
                                r = random.nextInt(27);
                            }
                            cInv.setItem(r, new ItemStack(Material.ARROW, 32));
                        }

                        x++;
                    }
                    chest.getBlock().update();
                }
            });

        } else {
            int seconds = getSeconds();

            this.getSkyWars().broadcastMessage("&b" + seconds + "...");
        }
    }

}
