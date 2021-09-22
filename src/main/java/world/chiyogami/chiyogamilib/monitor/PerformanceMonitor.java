package world.chiyogami.chiyogamilib.monitor;

import org.bukkit.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PerformanceMonitor {
    
    protected static Map<World, Long> WORLD_TICK_NANO_TIME_MAP = new ConcurrentHashMap<>();
    
    protected static long ALL_WORLD_TICK_NANO_TIME = 0;
    
    protected static long FULL_SERVER_TICK_NANO_TIME = 0;
    
    
    public static long getAllWorldTickNanoTime() {return ALL_WORLD_TICK_NANO_TIME;}
    
    public static long getFullServerTickNanoTime() {return FULL_SERVER_TICK_NANO_TIME;}
    
    public static Map<World, Long> getWorldTickNanoTimeMap() {return WORLD_TICK_NANO_TIME_MAP;}
}
