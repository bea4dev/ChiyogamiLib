package world.chiyogami.chiyogamilib.scheduler;

import org.bukkit.World;

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
    
    public static void worldThreadHeartBeat(World world){
        Set<WorldThreadRunnable> delayRunnableSet = delayMap.get(currentTick);
        if(delayRunnableSet != null){
            delayRunnableSet.forEach(WorldThreadRunnable::registerRunnable);
        }
        delayMap.remove(currentTick);
        
        Set<WorldThreadRunnable> runnableSet = worldThreadRunnableMap.get(world);
        if(runnableSet != null){
            runnableSet.forEach(Runnable::run);
            runnableSet.clear();
        }
        
        Set<WorldThreadRunnable> tickRunnableSet = worldThreadTickRunnableMap.get(world);
        if(tickRunnableSet != null){
            tickRunnableSet.forEach(runnable -> {
                runnable.run();
                if(runnable.isCancelled) {
                    tickRunnableSet.remove(runnable);
                }
            });
        }
        
        currentTick++;
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
        Set<WorldThreadRunnable> delayRunnableSet = delayMap.computeIfAbsent(delay, k -> ConcurrentHashMap.newKeySet());
        delayRunnableSet.add(worldThreadRunnable);
    }
    
    
    
    
    private final World world;
    private boolean isCancelled = false;
    private long tick = -1;
    private int taskID = -1;
    
    public WorldThreadRunnable(World world){
        this.world = world;
    }
    
    
    public synchronized void runTask(){
        checkNotYetScheduled();
        setupID();
        registerRunnable(this);
    }
    
    public synchronized void runTaskLater(long delay){
        checkNotYetScheduled();
        setupID();
        registerDelayRunnable(this, delay);
    }
    
    public synchronized void runTaskTimer(long delay, long period){
        checkNotYetScheduled();
        setupID();
        tick = period;
        registerDelayRunnable(this, delay);
    }
    
    
    public World getWorld() {return world;}
    
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
