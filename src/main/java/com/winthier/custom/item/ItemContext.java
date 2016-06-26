package com.winthier.custom.item;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public class ItemContext {
    final Item item;
    final ItemStack itemStack;
    final Map<String, Object> json;
    @Setter ItemStack replaceItem = null;
    @Setter boolean handled = false;
}
