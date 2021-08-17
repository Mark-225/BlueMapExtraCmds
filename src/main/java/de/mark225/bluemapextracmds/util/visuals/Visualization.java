package de.mark225.bluemapextracmds.util.visuals;

import com.flowpowered.math.vector.Vector2d;
import de.mark225.bluemapextracmds.BlueMapExtraCmds;
import de.mark225.bluemapextracmds.commands.BMETool;
import de.mark225.bluemapextracmds.data.ShapeSelection;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.List;

public class Visualization {

    private static boolean isStarted = false;

    public static synchronized void startRepeatingTask(){
        if(!isStarted)
            reSchedule();
        isStarted = true;
    }

    private static void runTask(){
        BulkRunnable bulkRunnable = new BulkRunnable();
        for(Player p : Bukkit.getOnlinePlayers()){
            ItemStack is = p.getInventory().getItemInMainHand();
            if(is != null && is.getType() == Material.GOLDEN_AXE && BMETool.isBMETool(is.getItemMeta())){
                ShapeSelection selection = BlueMapExtraCmds.getInstance().getOrCreateSelection(p.getUniqueId());
                Vector2d playerLocation = new Vector2d(p.getLocation().getX(), p.getLocation().getZ());
                RayTraceResult result = p.rayTraceBlocks(150, FluidCollisionMode.ALWAYS);
                double height = result != null ? result.getHitPosition().getY() : selection.getMinY();
                bulkRunnable.addRunnable(new VisualizationRunnable(p, selection, playerLocation, height));
            }
        }
        bulkRunnable.addRunnable(Visualization::reSchedule);
        Bukkit.getScheduler().runTaskAsynchronously(BlueMapExtraCmds.getInstance(), bulkRunnable);
    }

    private static void reSchedule(){
        Bukkit.getScheduler().runTaskLater(BlueMapExtraCmds.getInstance(), Visualization::runTask, 10);
    }

    public static void visualizeForPlayer(Player p, ShapeSelection selection, Vector2d playerLocation, double height){
        List<Vector2d> points = selection.getPoints();
        for(int i = 0; i < points.size(); i++){
            Vector2d currentPoint = points.get(i);
            Vector2d nextPoint = points.get((i + 1) % points.size());
            Vector2d previousPoint = points.get(Math.floorMod(i-1, points.size()));
            sendParticle(currentPoint, p, playerLocation, height, i == 0 ? LocationType.FIRST_CORNER : LocationType.CORNER);
            if(currentPoint.equals(nextPoint) || ( i > 0 && nextPoint == previousPoint)) continue;
            double distance = currentPoint.distance(nextPoint);
            int particleCount = (int) (distance * 3);
            if(particleCount < 3) particleCount = 3;
            if(particleCount > 50) particleCount = 50;
            Vector2d section = nextPoint.sub(currentPoint).div(particleCount + 1);
            for(int i2 = 0; i2 < particleCount; i2++){
                sendParticle(currentPoint.add(section.mul(i2 + 1)), p, playerLocation, height, i == points.size() - 1 ? LocationType.LAST_LINE : LocationType.LINE);
            }
        }
    }

    private static void sendParticle(Vector2d particle, Player p, Vector2d playerLocation, double height, LocationType type){
        if(particle.distance(playerLocation) > 200) return;
        p.spawnParticle(Particle.REDSTONE, particle.getX(), height, particle.getY(), 1, 0, 0, 0, 0, type.getDustOptions());
    }

    private enum LocationType{
        CORNER(new Particle.DustOptions(Color.GREEN, 2)),
        LINE(new Particle.DustOptions(Color.YELLOW, 1)),
        FIRST_CORNER(new Particle.DustOptions(Color.RED, 2)),
        LAST_LINE(new Particle.DustOptions(Color.ORANGE, 1));

        Particle.DustOptions dustOptions;

        LocationType(Particle.DustOptions dustOptions) {
            this.dustOptions = dustOptions;
        }

        public Particle.DustOptions getDustOptions() {
            return dustOptions;
        }
    }

}
