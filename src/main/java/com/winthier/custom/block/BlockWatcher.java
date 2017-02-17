package com.winthier.custom.block;

import com.winthier.custom.CustomConfig;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

public interface BlockWatcher extends Listener {
    Block getBlock();

    CustomBlock getCustomBlock();

    CustomConfig getCustomConfig();

    void blockWasDiscovered();

    void blockWillUnload();
}
