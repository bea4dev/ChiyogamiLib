package world.chiyogami.chiyogamilib;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import world.chiyogami.chiyogamilib.scheduler.ChiyogamiLibDummyPlugin;
import world.chiyogami.chiyogamilib.scheduler.WorldThreadRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class ChiyogamiLib{
    
    private static final ServerType serverType;
    private static final ChiyogamiLibDummyPlugin dummyPlugin;
    
    static {
        serverType = ServerType.getTypeByName(Bukkit.getName());
        dummyPlugin = new ChiyogamiLibDummyPlugin();
    }
    
    /**
     * Get server type.
     * @return ServerType(CRAFT_BUKKIT, PAPER, CHIYOGAMI)
     */
    public static ServerType getServerType() {return serverType;}
    
    /**
     * Get dummy plugin.
     * @return JavaPlugin
     */
    public static ChiyogamiLibDummyPlugin getDummyPlugin() {
        return dummyPlugin;
    }
    
    private ChiyogamiLib(){}
    
    /**
     * Load the chunk to be teleported to first to ensure smooth teleportation.
     * @param player Player to teleport
     * @param location Location to teleport to
     */
    public static void smoothTeleport(Player player, Location location){
        smoothTeleport(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
    
    /**
     * Load the chunk to be teleported to first to ensure smooth teleportation.
     * @param player Player to teleport
     * @param location Location to teleport to
     * @param cause Reason for teleport
     */
    public static void smoothTeleport(Player player, Location location, PlayerTeleportEvent.TeleportCause cause){
        
        if(serverType == ServerType.CRAFT_BUKKIT){
            player.teleport(location);
            return;
        }
        
        Location finalLocation = location.clone();
        new WorldThreadRunnable(finalLocation.getWorld()){
            @Override
            public void run() {
                World world = finalLocation.getWorld();
                Set<CompletableFuture<Chunk>> completableFutures = new HashSet<>();
                Set<Chunk> chunks = new HashSet<>();
            
                int range = Bukkit.getServer().getViewDistance();
                for(int x = -range; x < range; x++){
                    for(int z = -range; z < range; z++){
                        CompletableFuture<Chunk> completable = world.getChunkAtAsync((finalLocation.getBlockX() >> 4) + x, (finalLocation.getBlockZ() >> 4) + z);
                        completable.thenAccept(chunk -> {
                            chunk.setForceLoaded(true);
                            chunks.add(chunk);
                        });
                        completableFutures.add(completable);
                    }
                }
            
                CompletableFuture<Void> allChunkLoad = CompletableFuture.supplyAsync(() -> {
                    completableFutures.forEach(CompletableFuture::join);
                    return null;
                });
            
                allChunkLoad.thenAccept(v -> {
                    new WorldThreadRunnable(finalLocation.getWorld()) {
                        @Override
                        public void run() {
                            player.teleport(finalLocation, cause);
                            chunks.forEach(chunk -> chunk.setForceLoaded(false));
                        }
                    }.runTask();
                });
            }
        }.runTask();
    }
    
    /**
     * Load the chunk to be teleported to first to ensure smooth teleportation.
     * @param player Player to teleport
     * @param location Location to teleport to
     * @param delay tick delay
     */
    public static void smoothTeleport(Player player, Location location, long delay){
        smoothTeleport(player, location, delay, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
    
    /**
     * Load the chunk to be teleported to first to ensure smooth teleportation.
     * @param player Player to teleport
     * @param location Location to teleport to
     * @param delay tick delay
     * @param cause Reason for teleport
     */
    public static void smoothTeleport(Player player, Location location, long delay, PlayerTeleportEvent.TeleportCause cause){
    
        if(serverType == ServerType.CRAFT_BUKKIT){
            player.teleport(location);
            return;
        }
    
        Location finalLoc = location.clone();
        new WorldThreadRunnable(finalLoc.getWorld()){
            @Override
            public void run() {
                final boolean[] teleported = {false};
                World world = finalLoc.getWorld();
                Set<Chunk> chunks = new HashSet<>();
            
                int range = Bukkit.getServer().getViewDistance();
                for(int x = -range; x < range; x++){
                    for(int z = -range; z < range; z++){
                        CompletableFuture<Chunk> completable = world.getChunkAtAsync((finalLoc.getBlockX() >> 4) + x, (finalLoc.getBlockZ() >> 4) + z);
                        completable.thenAccept(chunk -> {
                            if(!teleported[0]) chunk.setForceLoaded(true);
                            chunks.add(chunk);
                        });
                    }
                }
                
                new WorldThreadRunnable(finalLoc.getWorld()) {
                    @Override
                    public void run() {
                        player.teleport(finalLoc, cause);
                        teleported[0] = true;
                        chunks.forEach(chunk -> chunk.setForceLoaded(false));
                    }
                }.runTaskLater(delay);
            }
        }.runTask();
    }
}
