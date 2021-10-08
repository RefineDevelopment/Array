package xyz.refinedev.practice.arena.runnables;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import xyz.refinedev.practice.Array;
import xyz.refinedev.practice.arena.Arena;
import xyz.refinedev.practice.arena.impl.TheBridgeArena;
import xyz.refinedev.practice.arena.cuboid.Cuboid;
import xyz.refinedev.practice.util.chat.CC;
import xyz.refinedev.practice.util.location.CustomLocation;

/**
 * This Project is property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 9/16/2021
 * Project: Array
 */

public class TheBridgePasteRunnable extends DuplicateArenaRunnable{

    private final Array plugin;
    private final TheBridgeArena copiedArena;

    private int times;
    private int amount;
    private int arenaId = 0;

    public TheBridgePasteRunnable(Array plugin, TheBridgeArena copiedArena, int copyAmount) {
        super(plugin, copiedArena, 1000, 1000, 500, 500);

        this.plugin = plugin;
        this.times = copyAmount;
        this.copiedArena = copiedArena;
    }

    @Override
    public void run() {
        amount = times;

        while (--times > 0) {
            arenaId++;
            super.run();
        }

        this.message();
    }

    @Override
    public void onComplete() {
        double minX = this.copiedArena.getMin().getX() + this.getOffsetX();
        double minZ = this.copiedArena.getMin().getZ() + this.getOffsetZ();
        double maxX = this.copiedArena.getMax().getX() + this.getOffsetX();
        double maxZ = this.copiedArena.getMax().getZ() + this.getOffsetZ();

        double aX = this.copiedArena.getSpawn1().getX() + this.getOffsetX();
        double aZ = this.copiedArena.getSpawn1().getZ() + this.getOffsetZ();
        double bX = this.copiedArena.getSpawn2().getX() + this.getOffsetX();
        double bZ = this.copiedArena.getSpawn2().getZ() + this.getOffsetZ();

        int portalRedX1 = this.copiedArena.getRedPortal().getX1() + this.getOffsetX();
        int portalRedX2 = this.copiedArena.getRedPortal().getX2() + this.getOffsetX();
        int portalRedY1 = this.copiedArena.getRedPortal().getY1();
        int portalRedY2 = this.copiedArena.getRedPortal().getY2();
        int portalRedZ1 = this.copiedArena.getRedPortal().getZ1() + this.getOffsetZ();
        int portalRedZ2 = this.copiedArena.getRedPortal().getZ2() + this.getOffsetZ();

        int portalBlueX1 = this.copiedArena.getBluePortal().getX1() + this.getOffsetX();
        int portalBlueX2 = this.copiedArena.getBluePortal().getX2() + this.getOffsetX();
        int portalBlueY1 = this.copiedArena.getBluePortal().getY1();
        int portalBlueY2 = this.copiedArena.getBluePortal().getY2();
        int portalBlueZ1 = this.copiedArena.getBluePortal().getZ1() + this.getOffsetZ();
        int portalBlueZ2 = this.copiedArena.getBluePortal().getZ2() + this.getOffsetZ();

        int cuboidBlueX1 = this.copiedArena.getBlueCuboid().getX1() + this.getOffsetX();
        int cuboidBlueX2 = this.copiedArena.getBlueCuboid().getX2() + this.getOffsetX();
        int cuboidBlueY1 = this.copiedArena.getBlueCuboid().getY1();
        int cuboidBlueY2 = this.copiedArena.getBlueCuboid().getY2();
        int cuboidBlueZ1 = this.copiedArena.getBlueCuboid().getZ1() + this.getOffsetZ();
        int cuboidBlueZ2 = this.copiedArena.getBlueCuboid().getZ2() + this.getOffsetZ();

        int cuboidRedX1 = this.copiedArena.getRedCuboid().getX1() + this.getOffsetX();
        int cuboidRedX2 = this.copiedArena.getRedCuboid().getX2() + this.getOffsetX();
        int cuboidRedY1 = this.copiedArena.getRedCuboid().getY1();
        int cuboidRedY2 = this.copiedArena.getRedCuboid().getY2();
        int cuboidRedZ1 = this.copiedArena.getRedCuboid().getZ1() + this.getOffsetZ();
        int cuboidRedZ2 = this.copiedArena.getRedCuboid().getZ2() + this.getOffsetZ();

        World world = this.getCopiedArena().getSpawn1().getWorld();

        CustomLocation min = new CustomLocation(world, minX, this.copiedArena.getMin().getY(), minZ, this.copiedArena.getMin().getYaw(), this.copiedArena.getMin().getPitch());
        CustomLocation max = new CustomLocation(world, maxX, this.copiedArena.getMax().getY(), maxZ, this.copiedArena.getMax().getYaw(), this.copiedArena.getMax().getPitch());
        CustomLocation a = new CustomLocation(world, aX, this.copiedArena.getSpawn1().getY(), aZ, this.copiedArena.getSpawn1().getYaw(), this.copiedArena.getSpawn1().getPitch());
        CustomLocation b = new CustomLocation(world, bX, this.copiedArena.getSpawn2().getY(), bZ, this.copiedArena.getSpawn2().getYaw(), this.copiedArena.getSpawn2().getPitch());
        Cuboid redPortal = new Cuboid(world, portalRedX1, portalRedY1, portalRedZ1, portalRedX2, portalRedY2, portalRedZ2);
        Cuboid bluePortal = new Cuboid(world, portalBlueX1, portalBlueY1, portalBlueZ1, portalBlueX2, portalBlueY2, portalBlueZ2);
        Cuboid redCuboid = new Cuboid(world, cuboidRedX1, cuboidRedY1, cuboidRedZ1, cuboidRedX2, cuboidRedY2, cuboidRedZ2);
        Cuboid blueCuboid = new Cuboid(world, cuboidBlueX1, cuboidBlueY1, cuboidBlueZ1, cuboidBlueX2, cuboidBlueY2, cuboidBlueZ2);

        TheBridgeArena duplicate = new TheBridgeArena(plugin, this.copiedArena.getName() + "#" + arenaId);

        duplicate.setSpawn1(a.toBukkitLocation());
        duplicate.setSpawn2(b.toBukkitLocation());

        duplicate.setMax(max.toBukkitLocation());
        duplicate.setMin(min.toBukkitLocation());

        duplicate.setRedPortal(redPortal);
        duplicate.setBlueCuboid(bluePortal);
        duplicate.setRedCuboid(redCuboid);
        duplicate.setBlueCuboid(blueCuboid);
        duplicate.setDisplayName(this.copiedArena.getDisplayName());

        duplicate.setDuplicate(true);

        this.copiedArena.getDuplicates().add(duplicate);
        plugin.getArenaManager().getArenas().add(duplicate);
    }

    public void message() {
        for ( Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("array.arena.admin") || player.isOp()) {
                player.sendMessage(CC.translate("&8[&c&lArray&8] &7Finished pasting &c" + copiedArena.getName() + "&7's " + amount + " &7duplicate arenas."));
            }
        }
        plugin.logger("&8[&c&lArray&8] &7Finished pasting &c" + copiedArena.getName() + "&7's " + amount + " &7duplicate arenas.");
        Arena.setPasting(false);
        plugin.getArenaManager().getArenas().forEach(Arena::save);
    }
}
