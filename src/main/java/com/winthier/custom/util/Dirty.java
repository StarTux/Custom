package com.winthier.custom.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagIntArray;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;

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
            NBTTagCompound texture = textures.get(0);
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
            return new TagWrapper(list.get(i));
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
    }

    public static final class TagWrapper {
        private final NBTTagCompound tag;

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

        public void applyMap(Map<String, Object> map) {
            for (String key: map.keySet()) {
                Object value = map.get(key);
                if (value instanceof String) {
                    setString(key, (String)value);
                } else if (value instanceof Integer) {
                    setInt(key, (Integer)value);
                } else if (value instanceof Long) {
                    setLong(key, (Long)value);
                } else if (value instanceof Float) {
                    setFloat(key, (Float)value);
                } else if (value instanceof Double) {
                    setDouble(key, (Double)value);
                } else if (value instanceof Boolean) {
                    setBoolean(key, (Boolean)value);
                } else if (value instanceof Map) {
                    TagWrapper newTag = createCompound(key);
                    newTag.applyMap((Map<String, Object>)value);
                } else if (value instanceof List) {
                    TagListWrapper newList = createList(key);
                    for (Object o: (List)value) {
                        if (o instanceof Map) {
                            TagWrapper newTag = newList.createCompound();
                            newTag.applyMap((Map<String, Object>)o);
                        } else if (o instanceof String) {
                            newList.addString((String)o);
                        }
                    }
                }
            }
        }
    }
}
