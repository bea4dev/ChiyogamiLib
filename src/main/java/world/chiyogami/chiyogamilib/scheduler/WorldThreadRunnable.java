package world.chiyogami.chiyogamilib.scheduler;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import world.chiyogami.chiyogamilib.ChiyogamiLib;
import world.chiyogami.chiyogamilib.ServerType;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public abstract class WorldThreadRunnable implements Runnable{
    
    private static final ReentrantLock ID_LOCK = new ReentrantLock(true);
    private static int id = 0;
    
    private static long currentTick = 0;
    
    private static final Map<World, Set<WorldThreadRunnable>> worldThreadRunnableMap = new ConcurrentHashMap<>();
    private static final Map<World, Set<WorldThreadRunnable>> worldThreadTickRunnableMap = new ConcurrentHashMap<>();
    
    private static final Map<Long, Set<WorldThreadRunnable>> delayMap = new ConcurrentHashMap<>();
    
    public static void worldThreadHeartBeat(World world, int tick){
        currentTick = tick;
        
        Set<WorldThreadRunnable> delayRunnableSet = delayMap.get(currentTick);
        if(delayRunnableSet != null){
            delayRunnableSet.forEach(WorldThreadRunnable::registerRunnable);
        }
        delayMap.remove(currentTick);
        
        Set<WorldThreadRunnable> runnableSet = worldThreadRunnableMap.get(world);
        if(runnableSet != null){
            runnableSet.forEach(runnable -> {
                try {
                    runnable.run();
                }catch (Exception e){e.printStackTrace();}
            });
            runnableSet.clear();
        }
        
        Set<WorldThreadRunnable> tickRunnableSet = worldThreadTickRunnableMap.get(world);
        if(tickRunnableSet != null){
            tickRunnableSet.forEach(runnable -> {
                try {
                    if(currentTick % runnable.tick == 0) runnable.run();
                }catch (Exception e){e.printStackTrace();}
                if(runnable.isCancelled) {
                    tickRunnableSet.remove(runnable);
                }
            });
        }
    }
    
    private static void registerRunnable(WorldThreadRunnable worldThreadRunnable){
        Set<WorldThreadRunnable> set;
        if(worldThreadRunnable.tick == -1){
            set = worldThreadRunnableMap.computeIfAbsent(worldThreadRunnable.world, k -> ConcurrentHashMap.newKeySet());
        }else {
            set = worldThreadTickRunnableMap.computeIfAbsent(worldThreadRunnable.world, k -> ConcurrentHashMap.newKeySet());
        }
        set.add(worldThreadRunnable);
    }
    
    private static void registerDelayRunnable(WorldThreadRunnable worldThreadRunnable, long delay){
        Set<WorldThreadRunnable> delayRunnableSet = delayMap.computeIfAbsent(currentTick + delay + 1, k -> ConcurrentHashMap.newKeySet());
        delayRunnableSet.add(worldThreadRunnable);
    }
    
    
    
    
    private World world;
    private boolean isCancelled = false;
    private long tick = -1;
    private int taskID = -1;
    
    public WorldThreadRunnable(World world){
        this.world = world;
    }
    
    
    public synchronized void runTask(JavaPlugin plugin){
        if(ChiyogamiLib.getServerType() != ServerType.CHIYOGAMI){
            new BukkitRunnable(){
                @Override
                public void run() {
                    WorldThreadRunnable.this.run();
                }
            }.runTask(plugin);
            return;
        }
        
        checkNotYetScheduled();
        setupID();
        registerRunnable(this);
    }
    
    public synchronized void runTaskLater(JavaPlugin plugin, long delay){
        if(ChiyogamiLib.getServerType() != ServerType.CHIYOGAMI){
            new BukkitRunnable(){
                @Override
                public void run() {
                    WorldThreadRunnable.this.run();
                }
            }.runTaskLater(plugin, delay);
            return;
        }
        
        checkNotYetScheduled();
        setupID();
        registerDelayRunnable(this, delay);
    }
    
    public synchronized void runTaskTimer(JavaPlugin plugin, long delay, long period){
        if(ChiyogamiLib.getServerType() != ServerType.CHIYOGAMI){
            new BukkitRunnable(){
                @Override
                public void run() {
                    WorldThreadRunnable.this.run();
                }
            }.runTaskTimer(plugin, delay, period);
            return;
        }
        
        checkNotYetScheduled();
        setupID();
        tick = period;
        registerDelayRunnable(this, delay);
    }
    
    
    public World getWorld() {return world;}
    
    public void setWorld(World world) {this.world = world;}
    
    public synchronized void cancel(){
        isCancelled = true;
    }
    
    public synchronized boolean isCancelled(){return isCancelled;}
    
    
    private void checkNotYetScheduled() {
        if (taskID != -1) {
            throw new IllegalStateException("Already scheduled as " + taskID);
        }
    }
    
    private void setupID(){
        try {
            ID_LOCK.lock();
            id++;
            this.taskID = id;
        } finally {
            ID_LOCK.unlock();
        }
    }
}
