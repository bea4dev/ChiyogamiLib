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
    <version>793983cef1</version>
    <scope>compile</scope>
</dependency>
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
                Location location = player.getLocation().add(1000, 0, 0);
                player.teleport(location);
            }
        }.runTask(TestPlugin.getPlugin());
    }
}
```
