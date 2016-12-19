package com.winthier.custom.block;

import com.winthier.custom.CustomConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;

@Getter @RequiredArgsConstructor
public class DefaultCustomBlock implements CustomBlock {
    final String customId;

    @Override
    public void setBlock(Block block, CustomConfig config) {
    }

    @Override
    public BlockWatcher createBlockWatcher(Block block, CustomConfig config) {
        return null;
    }
}
