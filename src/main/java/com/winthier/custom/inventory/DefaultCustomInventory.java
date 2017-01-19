package com.winthier.custom.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;

@Getter
@RequiredArgsConstructor
public class DefaultCustomInventory extends AbstractCustomInventory {
    final Inventory inventory;
}
