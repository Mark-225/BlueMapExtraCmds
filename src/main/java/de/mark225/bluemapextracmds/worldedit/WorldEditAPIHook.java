package de.mark225.bluemapextracmds.worldedit;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3d;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionOwner;
import de.mark225.bluemapextracmds.data.ShapeSelection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class WorldEditAPIHook {

    private static WorldEdit loadApi(){
        return WorldEdit.getInstance();
    }

    public static ShapeSelection convertPlayerSelection(Player player){
        WorldEdit we = loadApi();
        LocalSession session = we.getSessionManager().getIfPresent(BukkitAdapter.adapt(player));
        Region selection = null;
        try {
            selection = session.getSelection(BukkitAdapter.adapt(player.getWorld()));
        }catch(IncompleteRegionException e){
            return null;
        }
        List<Vector2d> points = selection.polygonize(-1).stream().map(bv2 -> new Vector2d(bv2.getX() + 0.5, bv2.getZ() + 0.5)).collect(Collectors.toList());
        return new ShapeSelection(selection.getMinimumPoint().getY(), selection.getMaximumPoint().getY(), points);
    }

}
