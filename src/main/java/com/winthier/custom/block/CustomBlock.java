package com.winthier.custom.block;

import com.winthier.custom.CustomConfig;
import org.bukkit.block.Block;

public interface CustomBlock {
    String getCustomId();

    void setBlock(Block block, CustomConfig config);

    BlockWatcher createBlockWatcher(Block block, CustomConfig config);
}
