package world.chiyogami.chiyogamilib;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import world.chiyogami.chiyogamilib.scheduler.WorldThreadRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class ChiyogamiLib{
    
    private static final ServerType serverType;
    
    static {
        serverType = ServerType.getTypeByName(Bukkit.getName());
    }
    
    /**
     * Get server type.
     * @return ServerType(CRAFT_BUKKIT, PAPER, CHIYOGAMI)
     */
    public static ServerType getServerType() {return serverType;}
    
    
    
    private final JavaPlugin plugin;
    
    public ChiyogamiLib(JavaPlugin plugin){
        this.plugin = plugin;
    }
    
    /**
     * Load the chunk to be teleported to first to ensure smooth teleportation.
     * @param player Player to teleport
     * @param location Location to teleport to
     * @return CompletableFuture<Void> that completes.
     */
    public CompletableFuture<Void> smoothTeleport(Player player, Location location){
        return smoothTeleport(player, location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
    
    /**
     * Load the chunk to be teleported to first to ensure smooth teleportation.
     * @param player Player to teleport
     * @param location Location to teleport to
     * @param cause Reason for teleport
     * @return CompletableFuture<Void> that completes.
     */
    public CompletableFuture<Void> smoothTeleport(Player player, Location location, PlayerTeleportEvent.TeleportCause cause){
        
        if(serverType == ServerType.CRAFT_BUKKIT){
            player.teleport(location);
            return CompletableFuture.completedFuture(null);
        }
        
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();
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
                            completableFuture.complete(null);
                        }
                    }.runTask(plugin);
                });
            }
        }.runTask(plugin);
        
        return completableFuture;
    }
    
    /**
     * Load the chunk to be teleported to first to ensure smooth teleportation.
     * @param player Player to teleport
     * @param location Location to teleport to
     * @param delay tick delay
     */
    public void smoothTeleport(Player player, Location location, long delay){
        smoothTeleport(player, location, delay, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }
    
    /**
     * Load the chunk to be teleported to first to ensure smooth teleportation.
     * @param player Player to teleport
     * @param location Location to teleport to
     * @param delay tick delay
     * @param cause Reason for teleport
     */
    public void smoothTeleport(Player player, Location location, long delay, PlayerTeleportEvent.TeleportCause cause){
    
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
                }.runTaskLater(plugin, delay);
            }
        }.runTask(plugin);
    }
}
