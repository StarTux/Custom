package com.winthier.custom.util;

import com.winthier.custom.CustomConfig;
import java.lang.reflect.Field;
import java.util.UUID;
import net.minecraft.server.v1_11_R1.ItemStack;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagList;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;

public final class Dirty {
    private Dirty() { }
    private static Field fieldCraftItemStackHandle = null;
    private static final String KEY_ITEM_CUSTOM = "Winthier.Custom";
    private static final String KEY_ITEM_ID = "id";
    private static final String KEY_ITEM_TAG = "tag";
    private static final int NBT_TYPE_COMPOUND = 10;
    private static final int NBT_TYPE_STRING = 8;

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
            String json = config.getJsonString();
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

    public static org.bukkit.inventory.ItemStack setSkullOwner(org.bukkit.inventory.ItemStack bukkitItem, String name, UUID uuid, String textureString) {
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
            if (!nmsItem.hasTag()) nmsItem.setTag(new NBTTagCompound());
            NBTTagCompound tag = nmsItem.getTag();
            if (!tag.hasKeyOfType("SkullOwner", 10)) tag.set("SkullOwner", new NBTTagCompound());
            NBTTagCompound ownerTag = tag.getCompound("SkullOwner");
            ownerTag.setString("Name", name);
            ownerTag.setString("Id", uuid.toString());
            if (!ownerTag.hasKeyOfType("Properties", 10)) ownerTag.set("Properties", new NBTTagCompound());
            NBTTagCompound propTag = ownerTag.getCompound("Properties");
            if (!propTag.hasKeyOfType("textures", 9)) propTag.set("textures", new NBTTagList());
            NBTTagList textures = propTag.getList("textures", 9);
            if (textures.size() < 1) textures.add(new NBTTagCompound());
            NBTTagCompound texture = textures.get(0);
            texture.setString("Value", textureString);
            return obcItem;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
