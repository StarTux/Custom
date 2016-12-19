package com.winthier.custom.block;

import com.winthier.custom.CustomConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

public class DefaultBlockWatcher extends AbstractBlockWatcher {
    public DefaultBlockWatcher(Block block, CustomBlock customBlock, CustomConfig config) {
        super(block, customBlock, config);
    }
}
