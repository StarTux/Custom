package com.winthier.custom.util;

import com.winthier.custom.CustomConfig;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import net.minecraft.server.v1_11_R1.*;
import org.bukkit.craftbukkit.v1_11_R1.entity.*;
import org.bukkit.craftbukkit.v1_11_R1.inventory.*;

public class Dirty {
    static Field fieldCraftItemStackHandle = null;
    final static String KEY_ITEM_CUSTOM = "Winthier.Custom";
    final static String KEY_ITEM_ID = "id";
    final static String KEY_ITEM_TAG = "tag";
    final static int NBT_TYPE_COMPOUND = 10;
    final static int NBT_TYPE_STRING = 8;

    static Field getFieldCraftItemStackHandle() {
        if (fieldCraftItemStackHandle == null) {
            try {
                fieldCraftItemStackHandle = CraftItemStack.class.getDeclaredField("handle");
            } catch (NoSuchFieldException nsfe) {
                nsfe.printStackTrace();
                fieldCraftItemStackHandle = null;
            }
        }
        return fieldCraftItemStackHandle;
    }

    public static org.bukkit.inventory.ItemStack saveConfig(org.bukkit.inventory.ItemStack bukkitItem, CustomConfig config) {
        try {
            CraftItemStack obcItem;
            ItemStack nmsItem;
            if (bukkitItem instanceof CraftItemStack) {
                obcItem = (CraftItemStack)bukkitItem;
                getFieldCraftItemStackHandle().setAccessible(true);
                nmsItem = (ItemStack)fieldCraftItemStackHandle.get(obcItem);
            } else {
                nmsItem = CraftItemStack.asNMSCopy(bukkitItem);
                obcItem = CraftItemStack.asCraftMirror(nmsItem);
            }
            if (!nmsItem.hasTag()) {
                nmsItem.setTag(new NBTTagCompound());
            }
            if (!nmsItem.getTag().hasKeyOfType(KEY_ITEM_CUSTOM, NBT_TYPE_COMPOUND)) {
                nmsItem.getTag().set(KEY_ITEM_CUSTOM, new NBTTagCompound());
            }
            NBTTagCompound tag = nmsItem.getTag().getCompound(KEY_ITEM_CUSTOM);
            tag.setString(KEY_ITEM_ID, config.getCustomId());
            String json = config.serialize();
            if (json != null) {
                tag.setString(KEY_ITEM_TAG, json);
            } else {
                tag.remove(KEY_ITEM_TAG);
            }
            return obcItem;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CustomConfig loadConfig(org.bukkit.inventory.ItemStack bukkitItem) {
        try {
            if (!(bukkitItem instanceof CraftItemStack)) return null;
            // Not sure which is faster, asNMSCopy() or this reflection bullcrap
            CraftItemStack obcItem = (CraftItemStack)bukkitItem;
            getFieldCraftItemStackHandle().setAccessible(true);
            ItemStack nmsItem = (ItemStack)fieldCraftItemStackHandle.get(obcItem);
            if (!nmsItem.hasTag()) return null;
            if (!nmsItem.getTag().hasKeyOfType(KEY_ITEM_CUSTOM, NBT_TYPE_COMPOUND)) return null;
            NBTTagCompound tag = nmsItem.getTag().getCompound(KEY_ITEM_CUSTOM);
            if (!tag.hasKeyOfType(KEY_ITEM_ID, NBT_TYPE_STRING)) return null;
            String id = tag.getString(KEY_ITEM_ID);
            String json;
            if (tag.hasKeyOfType(KEY_ITEM_TAG, NBT_TYPE_STRING)) {
                json = tag.getString(KEY_ITEM_TAG);
            } else {
                json = null;
            }
            return new CustomConfig(id, json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
