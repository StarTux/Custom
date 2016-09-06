package com.winthier.custom.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
@RequiredArgsConstructor
public class DummyItem extends AbstractItem {
    final String id;

    @Override
    public ItemStack createRawItemStack(int amount, ItemJson json) {
        return new ItemStack(Material.STONE, amount);
    }

    @Override
    public String getDisplayName(ItemJson json) {
        return id;
    }
}
