[CustomItem]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/item/CustomItem.java
[CustomBlock]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/block/CustomBlock.java
[CustomEntity]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/entity/CustomEntity.java
[ItemContext]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/item/ItemContext.java
[BlockContext]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/block/BlockContext.java
[EntityContext]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/entity/EntityContext.java
[TagWrapper]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/util/Dirty.java#L109
[BlockManager]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/block/BlockManager.java
[BlockWatcher]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/block/BlockWatcher.java
[EntityManager]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/entity/EntityManager.java
[EntityWatcher]:https://github.com/StarTux/Custom/blob/master/src/main/java/com/winthier/custom/entity/EntityWatcher.java
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
The event is called on the tick after the Custom plugin is enabled.  Should your client plugin be loaded or reloaded later, e.g. via *PlugMan*, issue the `/custom reload` command to trigger the calling of the event again.  This event handler method is also a great place to reload your plugin configuration.
#### Spawn them in-game
- `/custom give YourNameHere my:awesome_item`
- `/custom summon my:awesome_entity`
- `/custom setblock my:awesome_block`
## Event Handling
`CustomItem`, `CustomBlock`, and `CustomEntity` can listen to events.  There is no need to register them as the framework will scan all registered custom things for event handling methods.  They work mostly like in Bukkit.
- Annotate it with `@EventHandler`
- `EventPriority` and `ignoreCancelled` are respected, with the same defaults as Bukkit.
- First argument must be a subclass of `Event`

There are some minor differences.
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
- `CustomEntity` should use [`EntityManager`][EntityManager] `saveEntityData()`, `loadEntityData()` and `removeEntityData()`
#### CustomItem
```java
public class MyAwesomeItem implements CustomItem {
  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event, ItemContext context) {
    ItemStack item = context.getItemStack();
    Dirty.TagWrapper config = Dirty.TagWrapper.getItemConfigOf(item);
    int level = config.getInt("level");
    level += 1;
    config.setInt("level");
  }
}
```
`Dirty.TagWrapper` reads and writes directly from and to hidden NBT tags within the item, so there is no need to explicitly save and load.  It is **inadvisable** to ever keep an instance of the `TagWrapper` around longer than necessary.  All other changes done to a Bukkit `ItemStack` may invalidate the instance of `TagWrapper` and vice versa.  **Always make a fresh copy!**
#### CustomBlock
The block framework comes with its own data storage that can hold any Java data structures, provided they can be serialized with JSON.  Therefore it is recommended to store a `Map<String, Object>` to hold your custom data.  The corresponding load function will return an `Object` that needs to be cast, or `null`.  `@SuppressWarnings` is your friend.
```java
public class MyAwesomeBlock implements CustomBlock {
  @EventHandler
  public void onBlockDamage(BlockDamageEvent event, BlockContext context) {
    BlockWatcher watcher = context.getBlockWatcher();
    @SuppressWarnings("unchecked")
    Map<String, Object> map = (Map<String, Object>)CustomPlugin.getInstance().getBlockManager().loadBlockData(watcher);
    if (map == null) map = new HashMap<>();
    Integer level = (Integer)map.get("level");
    if (level == null) level = 0;
    map.put("level", level + 1);
    CustomPlugin.getInstance().getBlockManager().saveBlockData(watcher, map);
  }
}
```
#### CustomEntity
EntityManager provides 3 functions to load and store (and remove) entity data.  They all except a JSON style mapping of `String` to `Object`.  The map will be split into its keys and stored as JSON serialization in the entity's Scoreboard Tags.  Since scoreboard tags are limited to 1024 characters, it is advisable to keep the map flat beyond the top level.  The result of attempting to store data which yield in a scoreboard tag which is too long is undefined.
- `saveEntityData(entity, key, data)` stores the provided data map under a provided key in the scoreboard tags, one tag per key-value pairing in the map.
- `loadEntityData(entity, key)` retrieves the data stored above in one map which is largely identical to the input.  Keep in mind that JSON likes to change data types; e.g. `Integer` will be turned into `Long`.
- `removeEntityData(entity, key)` removes all scoreboard data for the given key.  Useful when the entity is unwrapped.
```java
public class MyAwesomeEntity implements CustomEntity {
  @EventHandler
  void onEntityDamage(EntityDamageEvent event, EntityContext context) {
    Entity entity = context.getEntity();
    Map<String, Object> data = CustomPlugin.getInstance().getEntityManager().loadEntityData(entity, "MyAwesomeEntity");
    int damageTaken;
    if (data.containsKey("Damage")) {
      damageTaken = ((Number)data.get("Damage")).intValue();
    } else {
      damageTaken = 0;
    }
    damageTaken = event.getFinalDamage();
    data.put("Damage", damageTaken);
    CustomPlugin.getEntitymanager().saveEntityData(entity, "MyAwesomeEntity", data);
  }
}
```
## [`BlockWatcher`][BlockWatcher] and [`EntityWatcher`][EntityWatcher]
Custom blocks and entities that are alive in the world are kept track of by means of watchers.  Both are required to hold a reference to the block or entity instance as well as the registered custom thing.  Many API calls and hooks use the watcher instead of just a block.  `BlockContext` and `EntityContext` contain a reference to the watcher of the object in question.  It is possible and recommended to override these watchers in order to cache persistent data or store transient values.
- Subclass `BlockWatcher` or `EntityWatcher`
- Override `CustomBlock.createBlockWatcher()` or `CustomEntity.createEntityWatcher()` to return an instance in your subclass.

```java
/** This block needs to be broken 100 times before it disappears. */
public class MyAwesomeBlock implements CustomBlock {
  @Override public String getCustomId() { return "my:awesome_block"; }
  @Override public void setBlock(Block block) { block.setType(Material.GLASS); }
  
  class Watcher implements BlockWatcher {
    private final Block block;
    private final MyAwesomeBlock myAwesomeBlock;
    private int health = 100;

    Watcher(Block block, MyAwesomeBlock myAwesomeBlock) {
      this.block = block;
      this.myAwesomeBlock = myAwesomeBlock;
    }
    @Override public Block getBlock() { return block; }
    @Override public CustomBlock getBlockWatcher() { return myAweomeBlock; }
  }

  @Override
  public void createEntityWatcher(Block block) {
    return new Watcher(block, this);
  }
  
  @Override public void blockWasLoaded(BlockWatcher blockWatcher) {
    Integer data = (Integer)CustomPlugin.getInstance().getBlockManager().loadBlockData(blockWatcher);
    if (data != null) ((Watcher)blockWatcher).health = data;
  }
  
  @Override public void blockWillUnload(BlockWatcher blockWatcher) {
    CustomPlugin.getInstance().getBlockManager().saveBlockData(blockWatcher, ((Watcher)blockWatcher).health);
  }
  
  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
  public void onBlockBreak(BlockBreakEvent event, BlockContext context) {
    Watcher watcher = (Watcher)context.getBlockWatcher();
    watcher.health -= 1;
    if (watcher.health == 0) {
      CustomPlugin.getInstance().getBlockManager().removeBlockWatcher(watcher);
    } else {
      event.setCancelled(true);
    }
  }
}
```
