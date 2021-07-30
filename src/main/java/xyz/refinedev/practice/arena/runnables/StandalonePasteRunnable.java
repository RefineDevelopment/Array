package xyz.refinedev.practice.arena.runnables;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.impl.StandaloneArena;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.location.CustomLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @since 11/25/2017
 * @author Zonix
 */
@Getter
@AllArgsConstructor
public class StandalonePasteRunnable implements Runnable {

    private static final Array plugin = Array.getInstance();
    private final StandaloneArena copiedArena;

    private int times;
    private static int amount;

    @Override
    public void run() {
        amount = times;
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

                CustomLocation min = new CustomLocation(arena.getSpawn1().getWorld(), minX, arena.getMin().getY(), minZ, arena.getMin().getYaw(), arena.getMin().getPitch());
                CustomLocation max = new CustomLocation(arena.getSpawn1().getWorld(), maxX, arena.getMax().getY(), maxZ, arena.getMax().getYaw(), arena.getMax().getPitch());
                CustomLocation a = new CustomLocation(arena.getSpawn1().getWorld(), aX, arena.getSpawn1().getY(), aZ, arena.getSpawn1().getYaw(), arena.getSpawn1().getPitch());
                CustomLocation b = new CustomLocation(arena.getSpawn1().getWorld(), bX, arena.getSpawn2().getY(), bZ, arena.getSpawn2().getYaw(), arena.getSpawn2().getPitch());

                Arena duplicate = new Arena(arena.getName());
                duplicate.setSpawn1(a.toBukkitLocation());
                duplicate.setSpawn2(b.toBukkitLocation());
                duplicate.setMax(max.toBukkitLocation());
                duplicate.setMin(min.toBukkitLocation());
                duplicate.setDisplayName(arena.getDisplayName());
                arena.getDuplicates().add(duplicate);
                Arena.getArenas().add(duplicate);

                if (--times > 0) {
                    duplicateArena(arena, (int) Math.round(maxX), (int) Math.round(maxZ));
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.hasPermission("array.arena.admin") || player.isOp()) {
                            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Finished pasting &c" + copiedArena.getName() + "&7's " + amount + " &7duplicate arenas."));
                        }
                    }
                    plugin.logger("&8[&c&lArray&8] &7Finished pasting &c" + copiedArena.getName() + "&7's " + amount + " &7duplicate arenas.");
                    Arena.setPasting(false);
                    Arena.getArenas().forEach(Arena::save);
                }
            }
        }.run();
    }
}