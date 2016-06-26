package com.winthier.custom.item;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

@Getter
@RequiredArgsConstructor
public class DummyItem extends AbstractItem {
    final String id;
    final String displayName;
    final Material material;
    final String description;
    final Map<Enchantment, Integer> enchantments;
}
