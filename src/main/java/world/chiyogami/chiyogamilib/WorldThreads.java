package world.chiyogami.chiyogamilib;

import org.bukkit.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldThreads {
    
    public static Map<World, Thread> worldThreadMap = new ConcurrentHashMap<>();
    
    public static boolean isWorldThread(){
        return worldThreadMap.containsValue(Thread.currentThread());
    }
}
