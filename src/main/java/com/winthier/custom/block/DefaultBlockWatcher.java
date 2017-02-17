package com.winthier.custom.block;

import com.winthier.custom.CustomConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;

@Getter @RequiredArgsConstructor
public class DefaultBlockWatcher extends AbstractBlockWatcher {
    final Block block;
    final CustomBlock customBlock;
    final CustomConfig customConfig;
}
