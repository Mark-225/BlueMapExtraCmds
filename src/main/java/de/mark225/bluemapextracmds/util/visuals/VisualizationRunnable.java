package de.mark225.bluemapextracmds.util.visuals;

import com.flowpowered.math.vector.Vector2d;
import de.mark225.bluemapextracmds.data.ShapeSelection;
import org.bukkit.entity.Player;

public class VisualizationRunnable implements Runnable {

    private Player p;
    private ShapeSelection selection;
    private Vector2d playerLocation;
    private double height;

    public VisualizationRunnable(Player p, ShapeSelection selection, Vector2d playerLocation, double height) {
        this.p = p;
        this.selection = selection;
        this.playerLocation = playerLocation;
        this.height = height;
    }

    @Override
    public void run() {
        Visualization.visualizeForPlayer(p, selection, playerLocation, height);
    }
}
