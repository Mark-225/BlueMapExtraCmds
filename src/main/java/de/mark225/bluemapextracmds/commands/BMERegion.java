package de.mark225.bluemapextracmds.commands;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2i;
import de.mark225.bluemapextracmds.BlueMapExtraCmds;
import de.mark225.bluemapextracmds.data.ShapeBounds;
import de.mark225.bluemapextracmds.data.ShapeSelection;
import de.mark225.bluemapextracmds.util.Utils;
import de.mark225.bluemapextracmds.worldedit.WorldEditAPIHook;
import de.themoep.minedown.MineDown;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BMERegion implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //TODO: Add console support
        if(!(sender instanceof Player)){
            sender.spigot().sendMessage(new MineDown(Lang.WRONG_EXECUTOR_PLAYER).toComponent());
            return true;
        }
        Player p = (Player) sender;
        if(args.length == 0) return false;

        if(args[0].equalsIgnoreCase("import") && args.length == 1){
            if(!BlueMapExtraCmds.getInstance().isWorldEditInstalled()){
                p.spigot().sendMessage(new MineDown(Lang.WE_UNAVAILABLE).toComponent());
                return true;
            }
            ShapeSelection sel = WorldEditAPIHook.convertPlayerSelection(p);
            if(sel == null){
                p.spigot().sendMessage(new MineDown(Lang.WE_NO_SELECTION).toComponent());
                return true;
            }
            BlueMapExtraCmds.getInstance().overwriteSelection(p.getUniqueId(), sel);
            p.spigot().sendMessage(new MineDown(Lang.SEL_IMPORTED).toComponent());
            System.out.println(sel.toString());
            return true;
        }else if(args[0].equalsIgnoreCase("reset") && args.length == 1){
            BlueMapExtraCmds.getInstance().clearSelection(p.getUniqueId());
            p.spigot().sendMessage(new MineDown(Lang.SEL_RESET).toComponent());
            return true;
        }else if(args[0].equalsIgnoreCase("addPoint")){
            if(args.length != 3) return false;
            String pointX = args[1];
            String pointZ = args[2];
            try {
                double x = toDouble(pointX, p.getLocation().getX());
                double z = toDouble(pointZ, p.getLocation().getZ());
                BlueMapExtraCmds.getInstance().getOrCreateSelection(p.getUniqueId()).getPoints().add(new Vector2d(x, z));
                p.spigot().sendMessage(new MineDown(Lang.POINT_ADDED).replace("point", x + " : " + z).toComponent());
                return true;
            }catch(NumberFormatException e) {
                p.spigot().sendMessage(new MineDown(Lang.NUMBER_FORMAT).toComponent());
            }
        }else if(args[0].equalsIgnoreCase("blockify") && args.length == 1){
            ShapeSelection sel = BlueMapExtraCmds.getInstance().getOrCreateSelection(p.getUniqueId());
            if(!sel.isValidPolygon()){
                p.spigot().sendMessage(new MineDown(Lang.BLOCKIFY_INVALID_SEL).toComponent());
                return true;
            }

            final ShapeBounds bounds = sel.getBounds();
            final float minY = sel.getMinY();
            final float maxY = sel.getMaxY();
            final List<Vector2i> integerPoly = sel.getPoints().stream().map(Vector2d::toInt).collect(Collectors.toList());

            p.spigot().sendMessage(new MineDown(Lang.BLOCKIFY_STARTED).toComponent());

            Bukkit.getScheduler().runTaskAsynchronously(BlueMapExtraCmds.getInstance(), () ->{

                final ShapeSelection blockifiedSelection = Utils.blockifyPoly(integerPoly, bounds.getMinCorner().toInt(), bounds.getMaxCorner().toInt(), minY, maxY);

                Bukkit.getScheduler().runTask(BlueMapExtraCmds.getInstance(), () ->{
                   if(!p.isOnline()) return;
                   if(blockifiedSelection == null){
                       p.spigot().sendMessage(new MineDown(Lang.UNEXPECTED_ERROR).toComponent());
                       return;
                   }
                   BlueMapExtraCmds.getInstance().overwriteSelection(p.getUniqueId(), blockifiedSelection);
                   p.spigot().sendMessage(new MineDown(Lang.BLOCKIFY_SUCCESS).toComponent());
                });
            });

            return true;
        }
        return false;
    }

    private static double toDouble(String in, double coords) throws NumberFormatException{
        if(!in.startsWith("~")) return Double.parseDouble(in);
        String[] components = in.split("\\.");
        if(components.length > 2) throw new NumberFormatException("Arguments with \"~\" can only have one decimal point");
        if(components.length == 1) return coords;
        String fraction = "0." + components[1];
        return Math.floor(coords) + Double.parseDouble(fraction);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if(args.length == 1){
            suggestions.addAll(Arrays.asList("import", "reset", "addPoint", "blockify"));
        }else if(args.length == 2 || args.length == 3 && args[0].equalsIgnoreCase("addPoint")){
            suggestions.addAll(Arrays.asList("~", "~."));
        }

        suggestions.removeIf(string -> !string.startsWith(args[args.length -1]));
        return suggestions;
    }
}
