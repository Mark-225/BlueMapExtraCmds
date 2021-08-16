package de.mark225.bluemapextracmds.util.visuals;

import com.flowpowered.math.vector.Vector2d;
import de.mark225.bluemapextracmds.BlueMapExtraCmds;
import de.mark225.bluemapextracmds.commands.BMETool;
import de.mark225.bluemapextracmds.data.ShapeSelection;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Visualization {

    public static void startRepeatingTask(){
        Bukkit.getScheduler().runTaskTimer(BlueMapExtraCmds.getInstance(), Visualization::runTask, 1, 10);
    }

    private static void runTask(){
        for(Player p : Bukkit.getOnlinePlayers()){
            ItemStack is = p.getInventory().getItemInMainHand();
            if(is != null && is.getType() == Material.GOLDEN_AXE && BMETool.isBMETool(is.getItemMeta())){
                visualizeForPlayer(p);
            }
        }
    }

    private static void visualizeForPlayer(Player p){
        ShapeSelection selection = BlueMapExtraCmds.getInstance().getOrCreateSelection(p.getUniqueId());
        Vector2d playerLocation = new Vector2d(p.getLocation().getX(), p.getLocation().getZ());
        List<Vector2d> points = selection.getPoints();
        for(int i = 0; i < points.size(); i++){
            Vector2d currentPoint = points.get(i);
            Vector2d nextPoint = points.get((i + 1) % points.size());
            Vector2d previousPoint = points.get(Math.floorMod(i-1, points.size()));
            sendParticle(currentPoint, p, playerLocation, i == 0 ? LocationType.FIRST_CORNER : LocationType.CORNER);
            if(currentPoint.equals(nextPoint) || ( i > 0 && nextPoint == previousPoint)) continue;
            Vector2d section = nextPoint.sub(currentPoint).div(19);
            for(int i2 = 0; i2 < 18; i2++){
                sendParticle(currentPoint.add(section.mul(i2 + 1)), p, playerLocation, i == points.size() - 1 ? LocationType.LAST_LINE : LocationType.LINE);
            }
        }
    }

    private static void sendParticle(Vector2d particle, Player p, Vector2d playerLocation, LocationType type){
        if(particle.distance(playerLocation) > 200) return;
        p.spawnParticle(Particle.REDSTONE, particle.getX(), p.getLocation().getY(), particle.getY(), 1, type.getDustOptions());
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
