package de.mark225.bluemapextracmds;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.mark225.bluemapextracmds.bluemap.BlueMapIntegration;
import de.mark225.bluemapextracmds.commands.BMERegion;
import de.mark225.bluemapextracmds.commands.BMETool;
import de.mark225.bluemapextracmds.commands.DebugMarker;
import de.mark225.bluemapextracmds.data.ShapeSelection;
import de.mark225.bluemapextracmds.util.BMEKeys;
import de.mark225.bluemapextracmds.util.BukkitEventListener;
import de.mark225.bluemapextracmds.util.visuals.Visualization;
import de.mark225.bluemapextracmds.worldedit.WorldEditAPIHook;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class BlueMapExtraCmds extends JavaPlugin {

    private static BlueMapExtraCmds instance;

    public static BlueMapExtraCmds getInstance(){
        return instance;
    }

    private BlueMapIntegration integration;
    private HashMap<UUID, ShapeSelection> playerSelections = new HashMap<>();


    @Override
    public void onEnable() {
        instance = this;
        integration = new BlueMapIntegration();

        BMEKeys.initialize();

        PluginCommand bmeRegion = Bukkit.getPluginCommand("bmeregion");
        BMERegion bmeRegionExecutor = new BMERegion();
        PluginCommand bmeDebug = Bukkit.getPluginCommand("bmedebug");
        DebugMarker bmeDebugExecutor = new DebugMarker();
        PluginCommand bmeTool = Bukkit.getPluginCommand("bmetool");
        BMETool bmeToolExecutor = new BMETool();

        bmeRegion.setExecutor(bmeRegionExecutor);
        bmeRegion.setTabCompleter(bmeRegionExecutor);
        bmeDebug.setExecutor(bmeDebugExecutor);
        bmeTool.setExecutor(bmeToolExecutor);

        Bukkit.getPluginManager().registerEvents(new BukkitEventListener(), this);
        Bukkit.getPluginManager().registerEvents(bmeToolExecutor, this);

        BlueMapAPI.onEnable(integration::onEnable);
        BlueMapAPI.onDisable(integration::onDisable);

        Visualization.startRepeatingTask();
    }

    public boolean isWorldEditInstalled(){
        Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
        return worldEdit != null;
    }

    public ShapeSelection getOrCreateSelection(UUID player){
        if(!playerSelections.containsKey(player))
            playerSelections.put(player, new ShapeSelection());
        return playerSelections.get(player);
    }

    public BlueMapIntegration getBlueMapIntegration(){
        return integration;
    }

    public void overwriteSelection(UUID player, ShapeSelection selection){
        playerSelections.put(player, selection);
    }

    public void clearSelection(UUID player){
        playerSelections.remove(player);
    }

    @Override
    public void onDisable() {

    }
}
