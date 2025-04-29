package world.chiyogami.chiyogamilib.monitor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PerformanceMonitor {
    
    private static Map<String, Long> WORLD_TICK_NANO_TIME_MAP = new ConcurrentHashMap<>();
    
    private static long ALL_WORLD_TICK_NANO_TIME = 0;
    
    private static long FULL_SERVER_TICK_NANO_TIME = 0;
    
    
    public static long getAllWorldTickNanoTime() {return ALL_WORLD_TICK_NANO_TIME;}
    
    public static long getFullServerTickNanoTime() {return FULL_SERVER_TICK_NANO_TIME;}
    
    public static Map<String, Long> getWorldTickNanoTimeMap() {return WORLD_TICK_NANO_TIME_MAP;}
    
    public static void setAllWorldTickNanoTime(long allWorldTickNanoTime) {ALL_WORLD_TICK_NANO_TIME = allWorldTickNanoTime;}
    
    public static void setFullServerTickNanoTime(long fullServerTickNanoTime) {FULL_SERVER_TICK_NANO_TIME = fullServerTickNanoTime;}
    
    public static void setWorldTickNanoTimeMap(Map<String, Long> worldTickNanoTimeMap) {WORLD_TICK_NANO_TIME_MAP = worldTickNanoTimeMap;}
}
