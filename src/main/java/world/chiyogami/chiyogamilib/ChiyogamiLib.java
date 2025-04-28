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

public final class ChiyogamiLib {

    private static final ServerType serverType;
    private static ServerThreadProvider serverThreadProvider;

    static {
        serverType = ServerType.getTypeByName(Bukkit.getName());
    }

    /**
     * Get server type.
     *
     * @return ServerType(CRAFT_BUKKIT, PAPER, CHIYOGAMI)
     */
    public static ServerType getServerType() {
        return serverType;
    }

    public static boolean isMainThread() {
        return serverThreadProvider.getMainThread() == Thread.currentThread();
    }

    public static ServerThreadProvider getServerThreadProvider() {
        return serverThreadProvider;
    }
}
