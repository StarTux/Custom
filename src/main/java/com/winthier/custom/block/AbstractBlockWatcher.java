package com.winthier.custom.block;

import com.winthier.custom.CustomConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

@Getter @RequiredArgsConstructor
public abstract class AbstractBlockWatcher implements BlockWatcher {
    final Block block;
    final CustomBlock customBlock;
    final CustomConfig customConfig;
}
