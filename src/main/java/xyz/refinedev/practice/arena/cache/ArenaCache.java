package xyz.refinedev.practice.arena.cache;

import net.minecraft.server.v1_8_R3.ChunkSection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This Project is property of Refine Development © 2021 - 2022
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created: 1/15/2022
 * Project: Array
 */

public class ArenaCache {

    public Map<ArenaChunk, ChunkSection[]> chunks = new ConcurrentHashMap<>();

    public ChunkSection[] getArenaChunkAtLocation(int x, int z) {
        for (Map.Entry<ArenaChunk, ChunkSection[]> chunksFromMap : this.chunks.entrySet()) {
            if (chunksFromMap.getKey().getX() != x || chunksFromMap.getKey().getZ() != z) continue;
            return chunksFromMap.getValue();
        }
        return null;
    }
}
