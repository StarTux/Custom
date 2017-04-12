# Custom
Customize items, blocks, and entities on the server.  This plugin provides a protocol to mark these things as customized, persistently.  There is support for specific event handling.
## How to write a client plugin
#### Add the dependency to your pom.xml
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
#### Add the dependency to your plugin.yml
```yml
depend: [ Custom ]
```
#### Implement CustomItem, CustomBlock, or CustomEntity
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
#### Listen to CustomRegisterEvent and add your item, block, or entity.
```java
  @EventHandler
  public void onCustomRegister(CustomRegisterEvent event) {
    event.addItem(new MyCustomItem());
    event.addBlock(new MyCustomBlock());
    event.addEntity(new MyCustomEntity());
  }
```
#### Spawn them in-game
- /custom give YourNameHere my:awesome_item
- /custom summon my:awesome_entity
- /custom setblock my:awesome_block
## Event Handling
CustomItem, CustomBlock, and CustomEntity can listen to events.  It works exactly like in Bukkit, except there is an additional (optional) parameter: ItemContext, BlockContext, EntityContext.  The EventHandler function will only be called if your custom thing was involved in the event.  Never should you blanket listen to all events and then check if your item was involved, but instead rely on the Custom plugin to do all the heavy lifting.
```java
public class MyCustomItem implements CustomItem {
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event, ItemContext context) {
    event.getPlayer().sendMessage("Hello World!");
  }
}
```
