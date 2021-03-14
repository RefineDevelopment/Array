package me.drizzy.practice.util;

import org.bukkit.World;
import org.bukkit.block.Block;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class BlockUtil {
    private static Set<Byte> blockSolidPassSet;
    private static Set<Byte> blockStairsSet;
    private static Set<Byte> blockLiquidsSet;
    private static Set<Byte> blockWebsSet;
    private static Set<Byte> blockIceSet;
    private static Set<Byte> blockCarpetSet;

    public static boolean isOnStairs(final Location location, final int down) {
        return isUnderBlock(location, BlockUtil.blockStairsSet, down);
    }

    public static boolean isOnLiquid(final Location location, final int down) {
        return isUnderBlock(location, BlockUtil.blockLiquidsSet, down);
    }

    public static boolean isOnWeb(final Location location, final int down) {
        return isUnderBlock(location, BlockUtil.blockWebsSet, down);
    }

    public static boolean isOnIce(final Location location, final int down) {
        return isUnderBlock(location, BlockUtil.blockIceSet, down);
    }

    public static boolean isOnCarpet(final Location location, final int down) {
        return isUnderBlock(location, BlockUtil.blockCarpetSet, down);
    }

    private static boolean isUnderBlock(final Location location, final Set<Byte> itemIDs, final int down) {
        final double posX = location.getX();
        final double posZ = location.getZ();
        final double fracX = (posX % 1.0 > 0.0) ? Math.abs(posX % 1.0) : (1.0 - Math.abs(posX % 1.0));
        final double fracZ = (posZ % 1.0 > 0.0) ? Math.abs(posZ % 1.0) : (1.0 - Math.abs(posZ % 1.0));
        final int blockX = location.getBlockX();
        final int blockY = location.getBlockY() - down;
        final int blockZ = location.getBlockZ();
        final World world = location.getWorld();
        if (itemIDs.contains((byte)world.getBlockAt(blockX, blockY, blockZ).getTypeId())) {
            return true;
        }
        if (fracX < 0.3) {
            if (itemIDs.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (itemIDs.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
            }
            else if (fracZ > 0.7) {
                if (itemIDs.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
            }
        }
        else if (fracX > 0.7) {
            if (itemIDs.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (itemIDs.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
            }
            else if (fracZ > 0.7) {
                if (itemIDs.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (itemIDs.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
            }
        }
        else if (fracZ < 0.3) {
            if (itemIDs.contains((byte)world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                return true;
            }
        }
        else if (fracZ > 0.7 && itemIDs.contains((byte)world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
            return true;
        }
        return false;
    }

    public static boolean isOnGround(final Location location, final int down) {
        final double posX = location.getX();
        final double posZ = location.getZ();
        final double fracX = (posX % 1.0 > 0.0) ? Math.abs(posX % 1.0) : (1.0 - Math.abs(posX % 1.0));
        final double fracZ = (posZ % 1.0 > 0.0) ? Math.abs(posZ % 1.0) : (1.0 - Math.abs(posZ % 1.0));
        final int blockX = location.getBlockX();
        final int blockY = location.getBlockY() - down;
        final int blockZ = location.getBlockZ();
        final World world = location.getWorld();
        if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX, blockY, blockZ).getTypeId())) {
            return true;
        }
        if (fracX < 0.3) {
            if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
            }
            else if (fracZ > 0.7) {
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
            }
        }
        else if (fracX > 0.7) {
            if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ).getTypeId())) {
                return true;
            }
            if (fracZ < 0.3) {
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ - 1).getTypeId())) {
                    return true;
                }
            }
            else if (fracZ > 0.7) {
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX - 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
                if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX + 1, blockY, blockZ + 1).getTypeId())) {
                    return true;
                }
            }
        }
        else if (fracZ < 0.3) {
            if (!BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX, blockY, blockZ - 1).getTypeId())) {
                return true;
            }
        }
        else if (fracZ > 0.7 && !BlockUtil.blockSolidPassSet.contains((byte)world.getBlockAt(blockX, blockY, blockZ + 1).getTypeId())) {
            return true;
        }
        return false;
    }

    static {
        BlockUtil.blockSolidPassSet = new HashSet<Byte>();
        BlockUtil.blockStairsSet = new HashSet<Byte>();
        BlockUtil.blockLiquidsSet = new HashSet<Byte>();
        BlockUtil.blockWebsSet = new HashSet<Byte>();
        BlockUtil.blockIceSet = new HashSet<Byte>();
        BlockUtil.blockCarpetSet = new HashSet<Byte>();
        BlockUtil.blockSolidPassSet.add((byte)0);
        BlockUtil.blockSolidPassSet.add((byte)6);
        BlockUtil.blockSolidPassSet.add((byte)8);
        BlockUtil.blockSolidPassSet.add((byte)9);
        BlockUtil.blockSolidPassSet.add((byte)10);
        BlockUtil.blockSolidPassSet.add((byte)11);
        BlockUtil.blockSolidPassSet.add((byte)27);
        BlockUtil.blockSolidPassSet.add((byte)28);
        BlockUtil.blockSolidPassSet.add((byte)30);
        BlockUtil.blockSolidPassSet.add((byte)31);
        BlockUtil.blockSolidPassSet.add((byte)32);
        BlockUtil.blockSolidPassSet.add((byte)37);
        BlockUtil.blockSolidPassSet.add((byte)38);
        BlockUtil.blockSolidPassSet.add((byte)39);
        BlockUtil.blockSolidPassSet.add((byte)40);
        BlockUtil.blockSolidPassSet.add((byte)50);
        BlockUtil.blockSolidPassSet.add((byte)51);
        BlockUtil.blockSolidPassSet.add((byte)55);
        BlockUtil.blockSolidPassSet.add((byte)59);
        BlockUtil.blockSolidPassSet.add((byte)63);
        BlockUtil.blockSolidPassSet.add((byte)66);
        BlockUtil.blockSolidPassSet.add((byte)68);
        BlockUtil.blockSolidPassSet.add((byte)69);
        BlockUtil.blockSolidPassSet.add((byte)70);
        BlockUtil.blockSolidPassSet.add((byte)72);
        BlockUtil.blockSolidPassSet.add((byte)75);
        BlockUtil.blockSolidPassSet.add((byte)76);
        BlockUtil.blockSolidPassSet.add((byte)77);
        BlockUtil.blockSolidPassSet.add((byte)78);
        BlockUtil.blockSolidPassSet.add((byte)83);
        BlockUtil.blockSolidPassSet.add((byte)90);
        BlockUtil.blockSolidPassSet.add((byte)104);
        BlockUtil.blockSolidPassSet.add((byte)105);
        BlockUtil.blockSolidPassSet.add((byte)115);
        BlockUtil.blockSolidPassSet.add((byte)119);
        BlockUtil.blockSolidPassSet.add((byte)(-124));
        BlockUtil.blockSolidPassSet.add((byte)(-113));
        BlockUtil.blockSolidPassSet.add((byte)(-81));
        BlockUtil.blockStairsSet.add((byte)53);
        BlockUtil.blockStairsSet.add((byte)67);
        BlockUtil.blockStairsSet.add((byte)108);
        BlockUtil.blockStairsSet.add((byte)109);
        BlockUtil.blockStairsSet.add((byte)114);
        BlockUtil.blockStairsSet.add((byte)(-128));
        BlockUtil.blockStairsSet.add((byte)(-122));
        BlockUtil.blockStairsSet.add((byte)(-121));
        BlockUtil.blockStairsSet.add((byte)(-120));
        BlockUtil.blockStairsSet.add((byte)(-100));
        BlockUtil.blockStairsSet.add((byte)(-93));
        BlockUtil.blockStairsSet.add((byte)(-92));
        BlockUtil.blockStairsSet.add((byte)(-76));
        BlockUtil.blockStairsSet.add((byte)126);
        BlockUtil.blockStairsSet.add((byte)(-74));
        BlockUtil.blockStairsSet.add((byte)44);
        BlockUtil.blockStairsSet.add((byte)78);
        BlockUtil.blockStairsSet.add((byte)99);
        BlockUtil.blockStairsSet.add((byte)(-112));
        BlockUtil.blockStairsSet.add((byte)(-115));
        BlockUtil.blockStairsSet.add((byte)(-116));
        BlockUtil.blockStairsSet.add((byte)(-105));
        BlockUtil.blockStairsSet.add((byte)(-108));
        BlockUtil.blockStairsSet.add((byte)100);
        BlockUtil.blockLiquidsSet.add((byte)8);
        BlockUtil.blockLiquidsSet.add((byte)9);
        BlockUtil.blockLiquidsSet.add((byte)10);
        BlockUtil.blockLiquidsSet.add((byte)11);
        BlockUtil.blockWebsSet.add((byte)30);
        BlockUtil.blockIceSet.add((byte)79);
        BlockUtil.blockIceSet.add((byte)(-82));
        BlockUtil.blockCarpetSet.add((byte)(-85));
    }
    public static final BlockFace[] blockFaces;

    public static Entity[] getNearbyEntities(final Location l, final int radius) {
        final int chunkRadius = (radius < 16) ? 1 : ((radius - radius % 16) / 16);
        final HashSet<Entity> radiusEntities = new HashSet<Entity>();
        for (int chX = 0 - chunkRadius; chX <= chunkRadius; ++chX) {
            for (int chZ = 0 - chunkRadius; chZ <= chunkRadius; ++chZ) {
                final int x = (int)l.getX();
                final int y = (int)l.getY();
                final int z = (int)l.getZ();
                for (final Entity e : new Location(l.getWorld(), (double)(x + chX * 16), (double)y, (double)(z + chZ * 16)).getChunk().getEntities()) {
                    if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock()) {
                        radiusEntities.add(e);
                    }
                }
            }
        }
        return radiusEntities.toArray(new Entity[radiusEntities.size()]);
    }

    public static boolean generatesCobble(final int id, final Block b) {
        final int mirrorID1 = (id == 8 || id == 9) ? 10 : 8;
        final int mirrorID2 = (id == 8 || id == 9) ? 11 : 9;
        for (final BlockFace face : BlockUtil.blockFaces) {
            final Block r = b.getRelative(face, 1);
            if (r.getTypeId() == mirrorID1 || r.getTypeId() == mirrorID2) {
                return true;
            }
        }
        return false;
    }

    static {
        blockFaces = new BlockFace[] { BlockFace.SELF, BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    }
}
