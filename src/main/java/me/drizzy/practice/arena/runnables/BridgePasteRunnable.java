package me.drizzy.practice.arena.runnables;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.drizzy.practice.Array;
import me.drizzy.practice.arena.Arena;
import me.drizzy.practice.arena.impl.StandaloneArena;
import me.drizzy.practice.arena.impl.TheBridgeArena;
import me.drizzy.practice.util.chat.CC;
import me.drizzy.practice.arena.cuboid.Cuboid;
import me.drizzy.practice.util.location.CustomLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @since 11/25/2017
 * @author Zonix
 */
@Getter
@AllArgsConstructor
public class BridgePasteRunnable implements Runnable {

    private static final Array plugin = Array.getInstance();
    private final TheBridgeArena copiedArena;

    private int times;
    private static int amount;

    @Override
    public void run() {
        amount = times;
        this.duplicateArena(this.copiedArena, 10000, 10000);
    }

    private void duplicateArena(TheBridgeArena arena, int offsetX, int offsetZ) {

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
                
                int portalRedX1 = arena.getRedPortal().getX1();
                int portalRedX2 = arena.getRedPortal().getX2();
                int portalRedY1 = arena.getRedPortal().getY1();
                int portalRedY2 = arena.getRedPortal().getY2();
                int portalRedZ1 = arena.getRedPortal().getZ1();
                int portalRedZ2 = arena.getRedPortal().getZ2();

                int portalBlueX1 = arena.getBluePortal().getX1() + this.getOffsetX();
                int portalBlueX2 = arena.getBluePortal().getX2() + this.getOffsetX();
                int portalBlueY1 = arena.getBluePortal().getY1();
                int portalBlueY2 = arena.getBluePortal().getY2();
                int portalBlueZ1 = arena.getBluePortal().getZ1() + this.getOffsetZ();
                int portalBlueZ2 = arena.getBluePortal().getZ2() + this.getOffsetZ();

                int cuboidBlueX1 = arena.getBlueCuboid().getX1() + this.getOffsetX();
                int cuboidBlueX2 = arena.getBlueCuboid().getX2() + this.getOffsetX();
                int cuboidBlueY1 = arena.getBlueCuboid().getY1();
                int cuboidBlueY2 = arena.getBlueCuboid().getY2();
                int cuboidBlueZ1 = arena.getBlueCuboid().getZ1() + this.getOffsetZ();
                int cuboidBlueZ2 = arena.getBlueCuboid().getZ2() + this.getOffsetZ();

                int cuboidRedX1 = arena.getRedCuboid().getX1() + this.getOffsetX();
                int cuboidRedX2 = arena.getRedCuboid().getX2() + this.getOffsetX();
                int cuboidRedY1 = arena.getRedCuboid().getY1();
                int cuboidRedY2 = arena.getRedCuboid().getY2();
                int cuboidRedZ1 = arena.getRedCuboid().getZ1() + this.getOffsetZ();
                int cuboidRedZ2 = arena.getRedCuboid().getZ2() + this.getOffsetZ();
                

                CustomLocation min = new CustomLocation(arena.getSpawn1().getWorld(), minX, arena.getMin().getY(), minZ, arena.getMin().getYaw(), arena.getMin().getPitch());
                CustomLocation max = new CustomLocation(arena.getSpawn1().getWorld(), maxX, arena.getMax().getY(), maxZ, arena.getMax().getYaw(), arena.getMax().getPitch());
                CustomLocation a = new CustomLocation(arena.getSpawn1().getWorld(), aX, arena.getSpawn1().getY(), aZ, arena.getSpawn1().getYaw(), arena.getSpawn1().getPitch());
                CustomLocation b = new CustomLocation(arena.getSpawn1().getWorld(), bX, arena.getSpawn2().getY(), bZ, arena.getSpawn2().getYaw(), arena.getSpawn2().getPitch());
                Cuboid redPortal = new Cuboid(arena.getSpawn1().getWorld(), portalRedX1, portalRedY1, portalRedZ1, portalRedX2, portalRedY2, portalBlueZ2);
                Cuboid bluePortal = new Cuboid(arena.getSpawn1().getWorld(), portalBlueX1, portalBlueY1, portalBlueZ1, portalBlueX2, portalBlueY2, portalBlueZ2);
                Cuboid redCuboid = new Cuboid(arena.getSpawn1().getWorld(), cuboidRedX1, cuboidRedY1, cuboidRedZ1, cuboidRedX2, cuboidRedY2, cuboidRedZ2);
                Cuboid blueCuboid = new Cuboid(arena.getSpawn1().getWorld(), cuboidBlueX1, cuboidBlueY1, cuboidBlueZ1, cuboidBlueX2, cuboidBlueY2, cuboidBlueZ2);

                
                TheBridgeArena duplicate = new TheBridgeArena(arena.getName() + " #2");
                duplicate.setSpawn1(a.toBukkitLocation());
                duplicate.setSpawn2(b.toBukkitLocation());
                duplicate.setMax(max.toBukkitLocation());
                duplicate.setMin(min.toBukkitLocation());
                duplicate.setRedPortal(redPortal);
                duplicate.setBlueCuboid(bluePortal);
                duplicate.setRedCuboid(redCuboid);
                duplicate.setBlueCuboid(blueCuboid);
                duplicate.setDisplayName(arena.getDisplayName());

                Arena.getArenas().add(duplicate);
                
                if (--times > 0) {
                    duplicateArena(arena, (int) Math.round(maxX), (int) Math.round(maxZ));
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.hasPermission("array.arena.admin") || player.isOp()) {
                            player.sendMessage(CC.translate("&8[&c&lArray&8] &7Finished pasting &c" + copiedArena.getName() + "&7's " + amount + " &7duplicate arenas."));
                        }
                    }
                    Array.logger("&8[&c&lArray&8] &7Finished pasting &c" + copiedArena.getName() + "&7's " + amount + " &7duplicate arenas.");
                    Arena.setPasting(false);
                    Arena.getArenas().forEach(Arena::save);
                }
            }
        }.run();
    }
}