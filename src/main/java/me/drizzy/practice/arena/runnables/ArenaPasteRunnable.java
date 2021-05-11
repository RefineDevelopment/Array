package me.drizzy.practice.arena.runnables;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.drizzy.practice.Array;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.impl.StandaloneArena;
import me.drizzy.practice.util.location.CustomLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @since 11/25/2017
 * @author Zonix
 */
@Getter
@AllArgsConstructor
public class ArenaPasteRunnable implements Runnable {

    private static final Array plugin = Array.getInstance();
    private final StandaloneArena copiedArena;

    private int times;

    @Override
    public void run() {
        this.duplicateArena(this.copiedArena, 10000, 10000);
    }

    private void duplicateArena(StandaloneArena arena, int offsetX, int offsetZ) {

        new DuplicateArenaRunnable(plugin, arena, offsetX, offsetZ, 500, 500) {
            @Override
            public void onComplete() {
                double minX = arena.getMin().getX() + this.getOffsetX();
                double minZ = arena.getMin().getZ() + this.getOffsetZ();
                double maxX = arena.getMax().getX() + this.getOffsetX();
                double maxZ = arena.getMax().getZ() + this.getOffsetZ();

                double aX = arena.getSpawn1().getX() + this.getOffsetX();
                double aZ = arena.getSpawn1().getZ() + this.getOffsetZ();
                double bX = arena.getSpawn2().getX() + this.getOffsetX();
                double bZ = arena.getSpawn2().getZ() + this.getOffsetZ();

                CustomLocation min = new CustomLocation(minX, arena.getMin().getY(), minZ, arena.getMin().getYaw(), arena.getMin().getPitch());
                CustomLocation max = new CustomLocation(maxX, arena.getMax().getY(), maxZ, arena.getMax().getYaw(), arena.getMax().getPitch());
                CustomLocation a = new CustomLocation(aX, arena.getSpawn1().getY(), aZ, arena.getSpawn1().getYaw(), arena.getSpawn1().getPitch());
                CustomLocation b = new CustomLocation(bX, arena.getSpawn2().getY(), bZ, arena.getSpawn2().getYaw(), arena.getSpawn2().getPitch());

                StandaloneArena standaloneArena = new StandaloneArena(arena.getName() + "-#" + (arena.getDuplicates().size() + 1));
                standaloneArena.setSpawn1(a.toBukkitLocation());
                standaloneArena.setSpawn2(b.toBukkitLocation());
                standaloneArena.setMax(max.toBukkitLocation());
                standaloneArena.setMin(min.toBukkitLocation());
                
                arena.getDuplicates().add(standaloneArena);
                Arena.getArenas().add(standaloneArena);

                if (--ArenaPasteRunnable.this.times > 0) {
                    Array.logger("Placed a standalone arena of " + arena.getName() + " at " + (int) minX + ", " + (int) minZ
                            + ". " + ArenaPasteRunnable.this.times + " arenas remaining.");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.isOp()) {
                            player.sendMessage("Placed a standalone arena of " + arena.getName() + " at " + (int) minX + ", " + (int) minZ
                                    + ". " + ArenaPasteRunnable.this.times + " arenas remaining.");
                        }
                    }
                    ArenaPasteRunnable.this.duplicateArena(arena, (int) Math.round(maxX), (int) Math.round(maxZ));
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.isOp()) {
                            player.sendMessage("Finished pasting " + ArenaPasteRunnable.this.copiedArena.getName() + "'s standalone arenas.");
                        }
                    }
                    Bukkit.getLogger().info("Finished pasting " + ArenaPasteRunnable.this.copiedArena.getName() + "'s standalone arenas.");
                    Arena.getArenas().forEach(Arena::save);
                }
            }
        }.run();
    }
}