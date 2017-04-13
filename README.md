[CustomItem]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/item/CustomItem.java
[CustomBlock]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/block/CustomBlock.java
[CustomEntity]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/entity/CustomEntity.java
[ItemContext]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/item/ItemContext.java
[BlockContext]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/block/BlockContext.java
[EntityContext]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/entity/EntityContext.java
[TagWrapper]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/util/Dirty.java#L109
[BlockManager]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/block/BlockManager.java
# Custom
Customize items, blocks, and entities on the server.  This plugin provides a protocol to mark these things as customized, persistently.  There is support for event handling.
## How to write a client plugin
#### Add the dependency to your `pom.xml`
```xml
  <dependencies>
    <dependency>
      <groupId>com.winthier.custom</groupId>
      <artifactId>custom</artifactId>
      <version>0.1</version>
      <scope>provided</scope>
    </dependency>
  </dependencies>
```
#### Add the dependency to your `plugin.yml`
```yml
depend: [ Custom ]
```
#### Implement [`CustomItem`][CustomItem], [`CustomBlock`][CustomBlock], or [`CustomEntity`][CustomEntity]
```java
import com.winthier.custom.item.CustomItem;

public class MyCustomItem implements CustomItem {
  @Override
  public String getCustomId() {
    return "my:awesome_item";
  }

  @Override
  public ItemStack spawnItemStack(int amount) {
    return new ItemStack(Material.STICK, amount);
  }
}
```
#### Listen to `CustomRegisterEvent` and add your item, block, or entity.
```java
public class MyPlugin extends JavaPlugin implements Listener {
  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(this, this);
  }

  @EventHandler
  public void onCustomRegister(CustomRegisterEvent event) {
    event.addItem(new MyCustomItem());
    event.addBlock(new MyCustomBlock());
    event.addEntity(new MyCustomEntity());
  }
}
```
#### Spawn them in-game
- `/custom give YourNameHere my:awesome_item`
- `/custom summon my:awesome_entity`
- `/custom setblock my:awesome_block`
## Event Handling
`CustomItem`, `CustomBlock`, and `CustomEntity` can listen to events.  There is no need to register them as the framework will scan all registered custom things for event handling methods.  They work mostly like in Bukkit:
- Annotate it with `@EventHandler`
- `EventPriority` and `ignoreCancelled` are respected, with the same defaults as Bukkit.
- First argument must be a subclass of `Event`
With some minor restrictions:
- Method must be public
- Only one event handler per event and priority
- There is a second (optional) argument, depending on the custom thing, providing additional information not contained in the event itself:
  - [`ItemContext`][ItemContext] for `CustomItem`
  - [`BlockContext`][BlockContext] for `CustomBlock`
  - [`EntityContext`][EntityContext] for `CustomEntity`

The EventHandler function will only be called if your custom thing was involved in the event.  Never should you listen to all events and then check yourself if your custom thing was involved, but instead rely on the Custom plugin to do all the heavy lifting.
```java
public class MyCustomItem implements CustomItem {
  // Omitted: getCustomId(), spawnItemStack()
  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
  public void onPlayerInteract(PlayerInteractEvent event, ItemContext context) {
    event.getPlayer().sendMessage("Hello World!");
  }
}
```
## Extra Data
All custom things are able to store additional data persistently.  How to do so depends on the type.
- `CustomItem` should use [`Dirty.TagWrapper`][TagWrapper]
- `CustomBlock` should use [`BlockManager`][BlockManager] `saveBlockData()` and `loadBlockData()`
- `CustomEntity` should use `Entity` `addScoreboardTag()` and `getScoreboardTags()`
#### CustomItem
```java
ItemStack item;
Dirty.TagWrapper config = Dirty.TagWrapper.getItemConfigOf(item);
int level = config.getInt("level");
level += 1;
config.setInt("level");
```
`Dirty.TagWrapper` reads and writes directly from and to hidden NBT tags within the item, so there is no need to explicitly save and load.  It is **inadvisable** to ever keep an instance of the `TagWrapper` around longer than necessary.  All other changes done to a Bukkit `ItemStack` may invalidate the instance of `TagWrapper` and vice versa.  **Always make a fresh copy!**
#### CustomBlock
The block framework comes with its own data storage that can hold any Java data structures, provided they can be serialized with JSON.  Therefore it is recommended to store a `Map<String, Object` to hold your custom data.  The corresponding load function will return an `Object` that needs to be cast, or `null`.  `@SuppressWarnings` is your friend.
```java
BlockWatcher watcher;
@SuppressWarnings("unchecked")
Map<String, Object> map = (Map<String, Object>)CustomPlugin.getInstance().getBlockManager().loadBlockData(watcher);
if (map == null) map = new HashMap<>();
String name = (String)map.get("name");
map.put("level", 1);
CustomPlugin.getInstance().getBlockManager().saveBlockData(watcher, map);
```
#### CustomEntity
Because Minecraft provides a method to persistently store strings in any entity via scoreboard tags, this framework provides no further support for entity data storage.  Make sure not to remove the tags of other plugins, including and especially this framework.  For further information, refer to the Bukkit/Spigot documentation.
- [`addScoreboardTag()`](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Entity.html#addScoreboardTag(java.lang.String))
- [`getScoreboardTags()`](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Entity.html#getScoreboardTags())
- [`removeScoreboardTag()`](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Entity.html#removeScoreboardTag(java.lang.String))
```java
Entity entity;
String myData = null;
for (String string: entity.getScoreboardTags()) {
  if (string.startsWith("MyCustomEntityData=") {
    myData = string;
  }
}
if (myData != null) {
  entity.removeScoreboardTag(myData);
  int myValue = Integer.parseInt(myData.split("=", 2)[1]);
  entity.addScoreboardTag("MyCustomEntityData=" + (myValue + 1));
} else {
  entity.addScoreboardTag("MyCustomEntityData=1");
}
```
