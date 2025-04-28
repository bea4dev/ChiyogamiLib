package world.chiyogami.chiyogamilib;

import org.bukkit.World;

public interface ServerThreadProvider {
    Thread getMainThread();

    Thread getWorldThread(World world);
}
