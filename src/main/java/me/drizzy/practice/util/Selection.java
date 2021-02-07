/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 */
package me.drizzy.practice.util;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public class Selection {
    private Location positionOne;
    private Location positionTwo;
    private String worldName;
    private int x1;
    private int y1;
    private int z1;
    private int x2;
    private int y2;
    private int z2;
    private List<Block> listOfBlocks = new ArrayList<Block>();
    private List<BlockState> blocksState = new ArrayList<BlockState>();

    public Selection(Location location, Location location2) {
        this.positionOne = location;
        this.positionTwo = location2;
    }

    public Selection select() {
        for (int i2 = this.y1; i2 <= this.y2; ++i2) {
            for (int i3 = this.x1; i3 <= this.x2; ++i3) {
                for (int i4 = this.z1; i4 <= this.z2; ++i4) {
                    Block block = this.getPositionOne().getWorld().getBlockAt(i3, i2, i4);
                    this.listOfBlocks.add(block);
                    this.blocksState.add(block.getState());
                }
            }
        }
        return this;
    }

    public boolean isInsideCuboid(Location location) {
        return location.getBlockX() >= this.x1 && location.getBlockX() <= this.x2 && location.getBlockZ() >= this.z1 && location.getBlockZ() <= this.z2 && location.getBlockY() >= this.y1 && location.getBlockY() <= this.y2;
    }

    public boolean isYOutside(Location location) {
        return location.getBlockY() >= this.y1;
    }

    public void assignValues() {
        this.worldName = this.positionOne.getWorld().getName();
        this.x1 = Math.min(this.positionOne.getBlockX(), this.positionTwo.getBlockX());
        this.y1 = Math.min(this.positionOne.getBlockY(), this.positionTwo.getBlockY());
        this.z1 = Math.min(this.positionOne.getBlockZ(), this.positionTwo.getBlockZ());
        this.x2 = Math.max(this.positionOne.getBlockX(), this.positionTwo.getBlockX());
        this.y2 = Math.max(this.positionOne.getBlockY(), this.positionTwo.getBlockY());
        this.z2 = Math.max(this.positionOne.getBlockZ(), this.positionTwo.getBlockZ());
    }

    public String getWorldName() {
        return this.worldName;
    }

    public Location getPositionOne() {
        return this.positionOne;
    }

    public void setPositionOne(Location location) {
        this.positionOne = location;
    }

    public Location getPositionTwo() {
        return this.positionTwo;
    }

    public void setPositionTwo(Location location) {
        this.positionTwo = location;
    }

    public List<BlockState> getBlocksState() {
        return this.blocksState;
    }

    public void setBlocksState(List<BlockState> list) {
        this.blocksState = list;
    }

    public List<Block> getBlocks() {
        return this.listOfBlocks;
    }
}

