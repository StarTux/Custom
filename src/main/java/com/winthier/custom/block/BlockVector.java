package com.winthier.custom.block;

import lombok.Value;
import org.bukkit.World;
import org.bukkit.block.Block;

@Value
class BlockVector {
    private final int x, y, z;

    static BlockVector of(int x, int y, int z) {
        return new BlockVector(x, y, z);
    }

    static BlockVector of(Block block) {
        return new BlockVector(block.getX(), block.getY(), block.getZ());
    }

    Block getBlock(World world) {
        return world.getBlockAt(x, y, z);
    }
}
