package de.mark225.bluemapextracmds.util;

import de.mark225.bluemapextracmds.BlueMapExtraCmds;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class BukkitEventListener implements Listener {

    private HashMap<UUID, BukkitTask> resetTasks = new HashMap<>();

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player p = event.getPlayer();
        Runnable task = () ->{
            if(!p.isOnline())
                BlueMapExtraCmds.getInstance().clearSelection(p.getUniqueId());
            resetTasks.remove(p.getUniqueId());
        };
        resetTasks.put(p.getUniqueId(), Bukkit.getScheduler().runTaskLater(BlueMapExtraCmds.getInstance(), task, 6000));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        if(resetTasks.containsKey(uuid)) {
            resetTasks.get(uuid).cancel();
            resetTasks.remove(uuid);
        }
    }

}
