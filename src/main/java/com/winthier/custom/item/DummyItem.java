package com.winthier.custom.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@Getter
@RequiredArgsConstructor
public class DummyItem extends AbstractItem {
    final String id;
    final String displayName;
    final Material material;
}
