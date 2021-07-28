package de.mark225.bluemapextracmds.commands;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.marker.MarkerAPI;
import de.bluecolored.bluemap.api.marker.MarkerSet;
import de.bluecolored.bluemap.api.marker.Shape;
import de.mark225.bluemapextracmds.BlueMapExtraCmds;
import de.mark225.bluemapextracmds.data.ShapeSelection;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class DebugMarker implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        ShapeSelection sel = BlueMapExtraCmds.getInstance().getOrCreateSelection(p.getUniqueId());
        if(!sel.isValidPolygon()) return false;
        MarkerAPI markerAPI = BlueMapExtraCmds.getInstance().getBlueMapIntegration().loadMarkerAPI();
        if(markerAPI == null) return false;
        MarkerSet set = markerAPI.createMarkerSet("debugExtraCmds");
        World w = p.getWorld();
        BlueMapAPI bApi = BlueMapExtraCmds.getInstance().getBlueMapIntegration().getApi();
        Vector2d center = sel.getBounds().getCenterPoint();
        Vector3d position = new Vector3d(center.getX(), sel.getMinY(), center.getY());
        bApi.getWorld(w.getUID()).ifPresent(bmw -> {
            bmw.getMaps().forEach(bmm ->{
                set.createShapeMarker("debugMarker" + ":" + bmm.getId(),bmm, position, new Shape(sel.getPoints().toArray(new Vector2d[0])), sel.getMinY());
            });
        });
        try {
            markerAPI.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
