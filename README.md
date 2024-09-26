ChiyogamiLib
===========
API for [Chiyogami](https://github.com/bea4dev/Chiyogami)

maven
------
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.bea4dev</groupId>
    <artifactId>ChiyogamiLib</artifactId>
    <version>7ed1df4960</version>
    <scope>compile</scope>
</dependency>
```

Example
------

```java
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import world.chiyogami.chiyogamilib.ChiyogamiLib;

public class TestPlugin extends JavaPlugin {
    
    //ChiyogamiLib instance.
    private static ChiyogamiLib chiyogamiLib;
    //Plugin instance.
    private static TestPlugin plugin;
    
    @Override
    public void onEnable() {
        //Create instance on startup.
        chiyogamiLib = new ChiyogamiLib(this);
        
        //Store plugin instance.
        plugin = this;
        
        //Register test event listener.
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new TestListener(), this);
    }
    
    public static ChiyogamiLib getChiyogamiLib() {
        return chiyogamiLib;
    }
    
    public static TestPlugin getPlugin() {
        return plugin;
    }
}
```

```java
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import world.chiyogami.chiyogamilib.scheduler.WorldThreadRunnable;
import java.util.concurrent.CompletableFuture;

public class TestListener implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        //The WorldThreadRunnable can be scheduled in the same way as the BukkitRunnable.
        //And it can be run in a different thread for each world.
        new WorldThreadRunnable(player.getWorld()) {
            @Override
            public void run() {
                //Teleport destination
                Location location = player.getLocation().add(1000, 0, 0);
                
                //Load or generate the chunks around the destination first for smooth teleportation.
                CompletableFuture<Void> future = TestPlugin.getChiyogamiLib.smoothTeleport(player, location);
                future.thenAccept(v -> {
                    player.sendMessage("Teleportation completed!");
                });
            }
        }.runTask(TestPlugin.getPlugin());
    }
}
```
