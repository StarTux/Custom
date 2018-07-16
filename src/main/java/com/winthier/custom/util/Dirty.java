package com.winthier.custom.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.v1_13_R1.ItemStack;
import net.minecraft.server.v1_13_R1.NBTBase;
import net.minecraft.server.v1_13_R1.NBTTagByte;
import net.minecraft.server.v1_13_R1.NBTTagByteArray;
import net.minecraft.server.v1_13_R1.NBTTagCompound;
import net.minecraft.server.v1_13_R1.NBTTagDouble;
import net.minecraft.server.v1_13_R1.NBTTagFloat;
import net.minecraft.server.v1_13_R1.NBTTagInt;
import net.minecraft.server.v1_13_R1.NBTTagIntArray;
import net.minecraft.server.v1_13_R1.NBTTagList;
import net.minecraft.server.v1_13_R1.NBTTagLong;
import net.minecraft.server.v1_13_R1.NBTTagLongArray;
import net.minecraft.server.v1_13_R1.NBTTagShort;
import net.minecraft.server.v1_13_R1.NBTTagString;
import net.minecraft.server.v1_13_R1.TileEntity;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack;

public final class Dirty {
    private Dirty() { }
    private static Field fieldCraftItemStackHandle = null;
    private static final String KEY_ITEM_CUSTOM_ID = "Winthier.Custom.ID";
    private static final String KEY_ITEM_CUSTOM_CONFIG = "Winthier.Custom.Config";
    private static final int NBT_TYPE_INT_ARRAY = 11;
    private static final int NBT_TYPE_COMPOUND = 10;
    private static final int NBT_TYPE_LIST = 9;
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

    public static org.bukkit.inventory.ItemStack setCustomId(org.bukkit.inventory.ItemStack bukkitItem, String customId) {
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
            nmsItem.getTag().setString(KEY_ITEM_CUSTOM_ID, customId);
            return obcItem;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static org.bukkit.inventory.ItemStack assertItemTag(org.bukkit.inventory.ItemStack bukkitItem) {
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
            return obcItem;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static NBTTagCompound getItemTag(org.bukkit.inventory.ItemStack bukkitItem) {
        try {
            if (!(bukkitItem instanceof CraftItemStack)) return null;
            CraftItemStack obcItem = (CraftItemStack)bukkitItem;
            getFieldCraftItemStackHandle().setAccessible(true);
            ItemStack nmsItem = (ItemStack)fieldCraftItemStackHandle.get(obcItem);
            return nmsItem.getTag();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCustomId(org.bukkit.inventory.ItemStack bukkitItem) {
        try {
            if (!(bukkitItem instanceof CraftItemStack)) return null;
            CraftItemStack obcItem = (CraftItemStack)bukkitItem;
            getFieldCraftItemStackHandle().setAccessible(true);
            ItemStack nmsItem = (ItemStack)fieldCraftItemStackHandle.get(obcItem);
            if (nmsItem == null) return null;
            if (!nmsItem.hasTag()) return null;
            if (!nmsItem.getTag().hasKeyOfType(KEY_ITEM_CUSTOM_ID, NBT_TYPE_STRING)) return null;
            return nmsItem.getTag().getString(KEY_ITEM_CUSTOM_ID);
        } catch (Exception e) {
            throw new RuntimeException(e);
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
            NBTTagCompound texture = (NBTTagCompound)textures.get(0); // FIXME: check cast
            texture.setString("Value", textureString);
            return obcItem;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static org.bukkit.inventory.ItemStack applyMap(org.bukkit.inventory.ItemStack item, Map<String, Object> map) {
        item = assertItemTag(item);
        TagWrapper.getItemTagOf(item).applyMap(map);
        return item;
    }

    public static Object fromTag(NBTBase value) {
        if (value instanceof NBTTagCompound) {
            // Recursive
            NBTTagCompound tag = (NBTTagCompound)value;
            Map<String, Object> result = new HashMap<>();
            for (String key: tag.getKeys()) {
                result.put(key, fromTag(tag.get(key)));
            }
            return result;
        } else if (value instanceof NBTTagList) {
            // Recursive
            NBTTagList tag = (NBTTagList)value;
            List<Object> result = new ArrayList<>();
            for (int i = 0; i < tag.size(); i += 1) {
                result.add(fromTag(tag.get(i)));
            }
            return result;
        } else if (value instanceof NBTTagString) {
            return (String)((NBTTagString)value).b_();
        } else if (value instanceof NBTTagInt) {
            return (int)((NBTTagInt)value).e();
        } else if (value instanceof NBTTagLong) {
            return (long)((NBTTagLong)value).d();
        } else if (value instanceof NBTTagShort) {
            return (short)((NBTTagShort)value).f();
        } else if (value instanceof NBTTagFloat) {
            return (float)((NBTTagFloat)value).i();
        } else if (value instanceof NBTTagDouble) {
            return (double)((NBTTagDouble)value).asDouble();
        } else if (value instanceof NBTTagByte) {
            return (byte)((NBTTagByte)value).g();
        } else if (value instanceof NBTTagByteArray) {
            return (byte[])((NBTTagByteArray)value).c();
        } else if (value instanceof NBTTagIntArray) {
            return (int[])((NBTTagIntArray)value).d();
        } else if (value instanceof NBTTagLongArray) {
            return (long[])((NBTTagLongArray)value).d();
        } else {
            System.err.println("TagWrapper.fromTag: Unsupported value type: " + value.getClass().getName());
            return value.toString();
        }
    }

    public static Map<String, Object> getBlockTag(org.bukkit.block.Block bukkitBlock) {
        CraftWorld craftWorld = (CraftWorld)bukkitBlock.getWorld();
        TileEntity tileEntity = craftWorld.getTileEntityAt(bukkitBlock.getX(), bukkitBlock.getY(), bukkitBlock.getZ());
        if (tileEntity == null) return null;
        NBTTagCompound tag = new NBTTagCompound();
        tileEntity.save(tag);
        return (Map<String, Object>)fromTag(tag);
    }

    public static boolean setBlockTag(org.bukkit.block.Block bukkitBlock, Map<String, Object> json) {
        CraftWorld craftWorld = (CraftWorld)bukkitBlock.getWorld();
        TileEntity tileEntity = craftWorld.getTileEntityAt(bukkitBlock.getX(), bukkitBlock.getY(), bukkitBlock.getZ());
        if (tileEntity == null) return false;
        NBTTagCompound tag = (NBTTagCompound)toTag(json);
        tileEntity.load(tag);
        return true;
    }

    public static NBTBase toTag(Object value) {
        if (value instanceof Map) {
            // Recursive
            NBTTagCompound tag = new NBTTagCompound();
            for (Map.Entry<String, Object> e: ((Map<String, Object>)value).entrySet()) {
                tag.set(e.getKey(), toTag(e.getValue()));
            }
            return tag;
        } else if (value instanceof List) {
            // Recursive
            NBTTagList tag = new NBTTagList();
            for (Object e: (List<Object>)value) {
                tag.add(toTag(e));
            }
            return tag;
        } else if (value instanceof String) {
            return new NBTTagString((String)value);
        } else if (value instanceof Integer) {
            return new NBTTagInt((Integer)value);
        } else if (value instanceof Long) {
            return new NBTTagLong((Long)value);
        } else if (value instanceof Short) {
            return new NBTTagShort((Short)value);
        } else if (value instanceof Float) {
            return new NBTTagFloat((Float)value);
        } else if (value instanceof Double) {
            return new NBTTagDouble((Double)value);
        } else if (value instanceof Boolean) {
            return new NBTTagInt((Boolean)value ? 1: 0);
        } else if (value instanceof byte[]) {
            return new NBTTagByteArray((byte[])value);
        } else if (value instanceof int[]) {
            return new NBTTagIntArray((int[])value);
        } else if (value instanceof long[]) {
            return new NBTTagLongArray((long[])value);
        } else {
            System.err.println("TagWrapper.toTag: Unsupported value type: " + value.getClass().getName());
            return new NBTTagString(value.toString());
        }
    }

    public static final class TagWrapper {
        private final NBTTagCompound tag;

        TagWrapper() {
            this.tag = new NBTTagCompound();
        }

        TagWrapper(NBTTagCompound tag) {
            this.tag = tag;
        }

        public static boolean hasItemConfig(org.bukkit.inventory.ItemStack item) {
            NBTTagCompound tag = getItemTag(item);
            if (tag == null) return false;
            return tag.hasKeyOfType(KEY_ITEM_CUSTOM_CONFIG, NBT_TYPE_COMPOUND);
        }

        /**
         * This will create an item config if it is not present.  May
         * return null if the item is not customized.
         */
        public static TagWrapper getItemConfigOf(org.bukkit.inventory.ItemStack item) {
            NBTTagCompound tag = getItemTag(item);
            if (tag == null) return null;
            if (!tag.hasKeyOfType(KEY_ITEM_CUSTOM_CONFIG, NBT_TYPE_COMPOUND)) {
                tag.set(KEY_ITEM_CUSTOM_CONFIG, new NBTTagCompound());
            }
            return new TagWrapper(tag.getCompound(KEY_ITEM_CUSTOM_CONFIG));
        }

        public static TagWrapper getItemTagOf(org.bukkit.inventory.ItemStack item) {
            NBTTagCompound tag = getItemTag(item);
            if (tag == null) return null;
            return new TagWrapper(tag);
        }

        public boolean isSet(String key) {
            return tag.hasKey(key);
        }

        public String getString(String key) {
            if (!tag.hasKeyOfType(key, NBT_TYPE_STRING)) return null;
            return tag.getString(key);
        }

        public void setString(String key, String value) {
            tag.setString(key, value);
        }

        public int getInt(String key) {
            return tag.getInt(key);
        }

        public void setInt(String key, int value) {
            tag.setInt(key, value);
        }

        public long getLong(String key) {
            return tag.getLong(key);
        }

        public void setLong(String key, long value) {
            tag.setLong(key, value);
        }

        public float getFloat(String key) {
            return tag.getFloat(key);
        }

        public void setFloat(String key, float value) {
            tag.setFloat(key, value);
        }

        public double getDouble(String key) {
            return tag.getDouble(key);
        }

        public void setDouble(String key, double value) {
            tag.setDouble(key, value);
        }

        public boolean getBoolean(String key) {
            return tag.getBoolean(key);
        }

        public void setBoolean(String key, boolean value) {
            tag.setBoolean(key, value);
        }

        public TagWrapper getCompound(String key) {
            if (!tag.hasKeyOfType(key, NBT_TYPE_COMPOUND)) return null;
            return new TagWrapper(tag.getCompound(key));
        }

        public void setStringList(String key, List<String> value) {
            NBTTagList list = new NBTTagList();
            for (String string: value) list.add(new NBTTagString(string));
            tag.set(key, list);
        }

        public List<String> getStringList(String key) {
            NBTTagList list = tag.getList(key, NBT_TYPE_STRING);
            List<String> result = new ArrayList<>(list.size());
            for (int i = 0; i < list.size(); i += 1) result.add(list.getString(i));
            return result;
        }

        public void setIntList(String key, List<Integer> value) {
            NBTTagIntArray list = new NBTTagIntArray(value);
            tag.set(key, list);
        }

        public List<Integer> getIntList(String key) {
            int[] array = tag.getIntArray(key);
            List<Integer> result = new ArrayList<>(array.length);
            for (int i: array) result.add(i);
            return result;
        }

        public TagWrapper createCompound(String key) {
            NBTTagCompound newCompound = new NBTTagCompound();
            tag.set(key, newCompound);
            return new TagWrapper(newCompound);
        }

        public TagListWrapper getList(String key) {
            if (!tag.hasKeyOfType(key, NBT_TYPE_LIST)) return null;
            return new TagListWrapper(tag.getList(key, NBT_TYPE_COMPOUND));
        }

        public TagListWrapper createList(String key) {
            NBTTagList newList = new NBTTagList();
            tag.set(key, newList);
            return new TagListWrapper(newList);
        }

        public void remove(String key) {
            tag.remove(key);
        }

        public Map<String, Object> toMap() {
            return (Map<String, Object>)fromTag(tag);
        }

        public void applyMap(Map<String, Object> map) {
            for (Map.Entry<String, Object> e: map.entrySet()) {
                tag.set(e.getKey(), toTag(e.getValue()));
            }
        }
    }

    public static final class TagListWrapper {
        private final NBTTagList list;

        TagListWrapper(NBTTagList list) {
            this.list = list;
        }

        public String getString(int i) {
            if (i < 0 || i >= list.size()) return null;
            return list.getString(i);
        }

        public void addString(String str) {
            NBTTagString tag = new NBTTagString(str);
            list.add(tag);
        }

        public TagWrapper getCompound(int i) {
            if (i < 0 || i >= list.size()) return null;
            return new TagWrapper((NBTTagCompound)list.get(i)); // FIXME: Check cast
        }

        public TagWrapper createCompound() {
            NBTTagCompound compound = new NBTTagCompound();
            list.add(compound);
            return new TagWrapper(compound);
        }

        public int size() {
            return list.size();
        }

        public void remove(int i) {
            list.remove(i);
        }

        public List<Object> toList() {
            return (List<Object>)fromTag(this.list);
        }
    }
}
