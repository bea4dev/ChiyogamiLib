package world.chiyogami.chiyogamilib;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ChiyogamiLib{
    
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
        try {
            Method method = Player.class.getMethod("teleportSmooth", Location.class, PlayerTeleportEvent.TeleportCause.class);
            method.invoke(player, location, cause);
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //player.teleportSmooth(location, cause);
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
        try {
            Method method = Player.class.getMethod("teleportSmooth", Location.class, long.class, PlayerTeleportEvent.TeleportCause.class);
            method.invoke(player, location, delay, cause);
        } catch (NoSuchMethodException e) {
            //e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //player.teleportSmooth(location, delay, cause);
    }
}
